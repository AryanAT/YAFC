package com.invest.indices.action;

import com.invest.indices.domain.model.NiftyFiftyEntity;
import com.invest.indices.infra.repository.NiftyFiftyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CleanData {

    private final NiftyFiftyRepository niftyFiftyRepository;

    public CleanData(NiftyFiftyRepository niftyFiftyRepository) {
        this.niftyFiftyRepository = niftyFiftyRepository;
    }

    public void filterFirstDatesOfMonth(List<NiftyFiftyEntity> niftyFiftyEntities) {
        List<LocalDate> dates = niftyFiftyEntities.stream().map(NiftyFiftyEntity::getDate).toList();
        LocalDate lastDate = null;

        for (LocalDate date : dates) {
            if (lastDate != null && date.getMonth().equals(lastDate.getMonth())) {
                niftyFiftyRepository.deleteById(date);
            }
            lastDate = date;
        }
    }
}
