package com.invest.indices.infra.batch;

import com.invest.indices.domain.errors.InvalidResponseException;
import com.invest.indices.domain.errors.MutualFundExistsException;
import com.invest.indices.domain.model.MutualFund;
import com.invest.indices.domain.model.MutualFundEntity;
import com.invest.indices.domain.model.PriceData;
import com.invest.indices.infra.repository.MutualFundRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.Data;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private final RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private MutualFundRepository mutualFundRepository;
    @Autowired
    private EntityManagerFactory entityManagerFactory;


    @Bean
    public Job getAllMutualFundsHistoricalNavJob(
            JobRepository jobRepository,
            Step step) {
        return new JobBuilder("getAllMutualFundsHistoricalNavJob", jobRepository)
                .start(step).build();
    }

    @Bean
    public Step getSchemeCodeFromDatabase(
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager,
            EntityManagerFactory entityManagerFactory) {
        return new StepBuilder("getSchemeCodeFromDatabase", jobRepository).<Integer, List<MutualFundEntity>>chunk(50, platformTransactionManager)
                .reader(getSchemeCodes(entityManagerFactory))
                .processor(mutualFundEntityItemProcessor())
                .writer(mutualFundItemWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Integer> getSchemeCodes(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<Integer>()
                .name("getSchemeCodes")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT s.schemeCode FROM SchemeNameAndCodeMapEntity s")
                .pageSize(50)
                .build();
    }

    @Bean
    public ItemProcessor<Integer, List<MutualFundEntity>> mutualFundEntityItemProcessor() {
        return schemeCode -> {
            String url = String.format("https://api.mfapi.in/mf/%d", schemeCode);
            if (!mutualFundRepository.findBySchemeCode(schemeCode).isEmpty()) {
                throw new MutualFundExistsException("MUTUAL_FUND_ALREADY_SAVED", "We already have details for this mutual fund in database");
            }
            MutualFund mutualFund = restTemplate.getForObject(url, MutualFund.class);
            if (mutualFund == null || mutualFund.getData().isEmpty()) {
                throw new InvalidResponseException("INVALID_RESPONSE_FROM_EXTERNAL_SERVICE", String.format("MF API returned null in response for this id %d", schemeCode));
            }
            return filterFirstNavOfMonth(mutualFund);
        };
    }


    @Bean
    public CustomJpaItemWriter mutualFundItemWriter() {
        return new CustomJpaItemWriter();
    }


    private List<MutualFundEntity> filterFirstNavOfMonth(MutualFund mutualFund) {

        HashMap<MonthYear, PriceData> priceDataPerMonthMap = new HashMap<>();
        List<MutualFundEntity> mutualFundEntityList = new ArrayList<>();

        for (PriceData priceData : mutualFund.getData()) {
            LocalDate date = parseDate(priceData.getDate());
            MonthYear monthYear = new MonthYear(date.getMonth(), date.getYear());
            if (!priceDataPerMonthMap.containsKey(monthYear) || date.isBefore(parseDate(priceDataPerMonthMap.get(monthYear).getDate()))) {
                priceDataPerMonthMap.put(monthYear, priceData);
            }
        }

        mutualFund.setData(priceDataPerMonthMap.values().stream().toList());

        for (PriceData priceData : mutualFund.getData()) {
            MutualFundEntity mutualFundEntity = new MutualFundEntity();
            mutualFundEntity.setUuid(UUID.randomUUID());
            mutualFundEntity.setNav(priceData.getNav());
            mutualFundEntity.setDate(priceData.getDate());
            mutualFundEntity.setFunHouse(mutualFund.getMeta().getFundHouse());
            mutualFundEntity.setSchemeName(mutualFund.getMeta().getSchemeName());
            mutualFundEntity.setSchemeCode(mutualFund.getMeta().getSchemeCode());
            mutualFundEntity.setSchemeType(mutualFund.getMeta().getSchemeType());
            mutualFundEntity.setSchemeCategory(mutualFund.getMeta().getSchemeCategory());
            mutualFundEntityList.add(mutualFundEntity);
        }

        return mutualFundEntityList;
    }

    private LocalDate parseDate(String dateString) {
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(dateString, dateFormatter);
    }

    @Data
    private static class MonthYear {
        private final Month month;
        private final int year;
    }
}
