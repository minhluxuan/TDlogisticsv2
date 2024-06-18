package project.tdlogistics.fee.entities;

public class RangeMass {
    private String toMass;
    private double fromMass;
    private double baseFee;
    private double fee;
    private Double incrementPerKilogram;
    private double baseGram;

    
    public RangeMass() {
    }
    public RangeMass(String toMass, double fromMass, double baseFee, double fee, Double incrementPerKilogram,
            double baseGram) {
        this.toMass = toMass;
        this.fromMass = fromMass;
        this.baseFee = baseFee;
        this.fee = fee;
        this.incrementPerKilogram = incrementPerKilogram;
        this.baseGram = baseGram;
    }
    public String getToMass() {
        return toMass;
    }
    public void setToMass(String toMass) {
        this.toMass = toMass;
    }
    public double getFromMass() {
        return fromMass;
    }
    public void setFromMass(double fromMass) {
        this.fromMass = fromMass;
    }
    public double getBaseFee() {
        return baseFee;
    }
    public void setBaseFee(double baseFee) {
        this.baseFee = baseFee;
    }
    public double getFee() {
        return fee;
    }
    public void setFee(double fee) {
        this.fee = fee;
    }
    public Double getIncrementPerKilogram() {
        return incrementPerKilogram;
    }
    public void setIncrementPerKilogram(Double incrementPerKilogram) {
        this.incrementPerKilogram = incrementPerKilogram;
    }
    public double getBaseGram() {
        return baseGram;
    }
    public void setBaseGram(double baseGram) {
        this.baseGram = baseGram;
    }
    @Override
    public String toString() {
        return "RangeMass [toMass=" + toMass + ", fromMass=" + fromMass + ", baseFee=" + baseFee + ", fee=" + fee
                + ", incrementPerKilogram=" + incrementPerKilogram + ", baseGram=" + baseGram + ", getToMass()="
                + getToMass() + ", getFromMass()=" + getFromMass() + ", getBaseFee()=" + getBaseFee() + ", getFee()="
                + getFee() + ", getIncrementPerKilogram()=" + getIncrementPerKilogram() + ", getBaseGram()="
                + getBaseGram() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
                + super.toString() + "]";
    }

    
}
