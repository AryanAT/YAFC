package com.invest.indices.domain.model;

import java.util.Objects;

public class ReturnOutput {
    private Double finalAmount;
    private Double invAmount;
    private String fundName;
    private Double absoluteReturns;


    @Override
    public String toString() {
        return "ReturnOutput{" +
                "finalAmount=" + finalAmount +
                ", invAmount=" + invAmount +
                ", fundName='" + fundName + '\'' +
                ", absoluteReturns=" + absoluteReturns +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReturnOutput that = (ReturnOutput) o;
        return Objects.equals(finalAmount, that.finalAmount) && Objects.equals(invAmount, that.invAmount) && Objects.equals(fundName, that.fundName) && Objects.equals(absoluteReturns, that.absoluteReturns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(finalAmount, invAmount, fundName, absoluteReturns);
    }

    public Double getAbsoluteReturns() {
        return absoluteReturns;
    }

    public void setAbsoluteReturns(Double absoluteReturns) {
        this.absoluteReturns = absoluteReturns;
    }

    public ReturnOutput(Double finalAmount, Double invAmount, String fundName, Double absoluteReturns) {
        this.finalAmount = finalAmount;
        this.invAmount = invAmount;
        this.fundName = fundName;
        this.absoluteReturns = absoluteReturns;
    }

    public ReturnOutput(Double finalAmount, Double invAmount, String fundName) {
        this.finalAmount = finalAmount;
        this.invAmount = invAmount;
        this.fundName = fundName;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public Double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(Double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public Double getInvAmount() {
        return invAmount;
    }

    public void setInvAmount(Double invAmount) {
        this.invAmount = invAmount;
    }
}
