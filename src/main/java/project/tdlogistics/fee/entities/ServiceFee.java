package project.tdlogistics.fee.entities;

import java.util.List;

public class ServiceFee {
    private List<RangeMass> innerArea;
    private List<RangeMass> outerArea;
    private List<RangeMass> seperateArea;
    private List<RangeMass> innerProvince;
    private List<RangeMass> ordinaryCase;
    private List<SpecialCase> specialCase;
    private double blockStepIncre;


    public ServiceFee() {
    }

    public ServiceFee(List<RangeMass> innerArea, List<RangeMass> outerArea, List<RangeMass> seperateArea,
            List<RangeMass> innerProvince, List<RangeMass> ordinaryCase, List<SpecialCase> specialCase,
            double blockStepIncre) {
        this.innerArea = innerArea;
        this.outerArea = outerArea;
        this.seperateArea = seperateArea;
        this.innerProvince = innerProvince;
        this.ordinaryCase = ordinaryCase;
        this.specialCase = specialCase;
        this.blockStepIncre = blockStepIncre;
    }
    
    public void setInnerArea(List<RangeMass> innerArea) {
        this.innerArea = innerArea;
    }
    public void setOuterArea(List<RangeMass> outerArea) {
        this.outerArea = outerArea;
    }
    public void setSeperateArea(List<RangeMass> seperateArea) {
        this.seperateArea = seperateArea;
    }
    public void setInnerProvince(List<RangeMass> innerProvince) {
        this.innerProvince = innerProvince;
    }
    public void setOrdinaryCase(List<RangeMass> ordinaryCase) {
        this.ordinaryCase = ordinaryCase;
    }
    public void setSpecialCase(List<SpecialCase> specialCase) {
        this.specialCase = specialCase;
    }
    public void setBlockStepIncre(double blockStepIncre) {
        this.blockStepIncre = blockStepIncre;
    }
    public List<RangeMass> getInnerArea() {
        return innerArea;
    }
    public List<RangeMass> getOuterArea() {
        return outerArea;
    }
    public List<RangeMass> getSeperateArea() {
        return seperateArea;
    }
    public List<RangeMass> getInnerProvince() {
        return innerProvince;
    }
    public List<RangeMass> getOrdinaryCase() {
        return ordinaryCase;
    }
    public List<SpecialCase> getSpecialCase() {
        return specialCase;
    }
    public double getBlockStepIncre() {
        return blockStepIncre;
    }

    @Override
    public String toString() {
        return "ServiceFee [innerArea=" + innerArea + ", outerArea=" + outerArea + ", seperateArea=" + seperateArea
                + ", innerProvince=" + innerProvince + ", ordinaryCase=" + ordinaryCase + ", specialCase=" + specialCase
                + ", blockStepIncre=" + blockStepIncre + ", getClass()=" + getClass() + ", getInnerArea()="
                + getInnerArea() + ", getOuterArea()=" + getOuterArea() + ", getSeperateArea()=" + getSeperateArea()
                + ", getInnerProvince()=" + getInnerProvince() + ", getOrdinaryCase()=" + getOrdinaryCase()
                + ", getSpecialCase()=" + getSpecialCase() + ", getBlockStepIncre()=" + getBlockStepIncre()
                + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
    }

    
}
