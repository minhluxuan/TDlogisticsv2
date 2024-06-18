package project.tdlogistics.orders.entities;

import java.util.List;

public class SpecialCase {
    private String fromProvince;
    private String toProvince;
    private List<RangeMass> detailMass;    
    
    public SpecialCase() {
    }
    public SpecialCase(String fromProvince, String toProvince, List<RangeMass> detailMass) {
        this.fromProvince = fromProvince;
        this.toProvince = toProvince;
        this.detailMass = detailMass;
    }
    public String getFromProvince() {
        return fromProvince;
    }
    public void setFromProvince(String fromProvince) {
        this.fromProvince = fromProvince;
    }
    public String getToProvince() {
        return toProvince;
    }
    public void setToProvince(String toProvince) {
        this.toProvince = toProvince;
    }
    public List<RangeMass> getDetailMass() {
        return detailMass;
    }
    public void setDetailMass(List<RangeMass> detailMass) {
        this.detailMass = detailMass;
    }
    @Override
    public String toString() {
        return "SpecialCase [fromProvince=" + fromProvince + ", toProvince=" + toProvince + ", detailMass=" + detailMass
                + ", getFromProvince()=" + getFromProvince() + ", getToProvince()=" + getToProvince()
                + ", getDetailMass()=" + getDetailMass() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
                + ", toString()=" + super.toString() + "]";
    }

    
}
