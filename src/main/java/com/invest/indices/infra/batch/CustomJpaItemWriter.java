package com.invest.indices.infra.batch;


import com.invest.indices.domain.model.MutualFundEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.Transactional;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class CustomJpaItemWriter implements ItemWriter<List<MutualFundEntity>> {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Override
    @Transactional
    public void write(Chunk<? extends List<MutualFundEntity>> items) throws Exception {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            List<MutualFundEntity> flatList = new ArrayList<>();
            for (List<MutualFundEntity> sublist : items) {
                flatList.addAll(sublist);
            }

            for (MutualFundEntity mutualFundEntity : flatList) {
                entityManager.persist(mutualFundEntity);
            }

            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw e;
        } finally {
            entityManager.close();
        }
    }

}

