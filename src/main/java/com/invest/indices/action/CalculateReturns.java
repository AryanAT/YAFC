package com.invest.indices.action;

import com.invest.indices.domain.model.NiftyFiftyEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CalculateReturns {

    public Double with(List<NiftyFiftyEntity> niftyFiftyEntities, int invAmount){
        double totalUnitsPurchased = 0;

        for(NiftyFiftyEntity niftyFiftyEntity: niftyFiftyEntities){
            totalUnitsPurchased +=  (double) invAmount /niftyFiftyEntity.getOpen();
        }

        int finalVal = niftyFiftyEntities.get(niftyFiftyEntities.size()-1).getClose();

        return (totalUnitsPurchased*finalVal);
    }

    public Double forTheTime(List<NiftyFiftyEntity> niftyFiftyEntities, int invAmount, LocalDate inceptionDate, LocalDate redemptionDate ){
        double totalUnitsPurchased = 0;
        int closePriceOnRedemptionDate = 0;

        for(NiftyFiftyEntity niftyFiftyEntity: niftyFiftyEntities){
            if (
                    (niftyFiftyEntity.getDate().isAfter(inceptionDate) ||  niftyFiftyEntity.getDate().isEqual(inceptionDate))
                    && (niftyFiftyEntity.getDate().isBefore(redemptionDate) ||  niftyFiftyEntity.getDate().isEqual(redemptionDate))
            ) {
                totalUnitsPurchased +=  (double) invAmount /niftyFiftyEntity.getOpen();
                closePriceOnRedemptionDate = niftyFiftyEntity.getClose();
            }
        }
        return (totalUnitsPurchased*closePriceOnRedemptionDate);
    }

}
