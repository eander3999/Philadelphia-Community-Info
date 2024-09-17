package philadelphia_info_calculator.util;

import java.util.Objects;

public class Property {
    private String zipCode;
    private Double marketValue;
    private Double totalLivableArea;

    public Property(String zipCode, Double marketValue, Double totalLivableArea) {
        this.zipCode = zipCode;
        this.marketValue = marketValue;
        this.totalLivableArea = totalLivableArea;
    }

    public String getZipCode() {
        return zipCode;
    }

    public Double getMarketValue() {
        return marketValue;
    }

    public Double getTotalLivableArea() {
        return totalLivableArea;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Property property = (Property) o;

        if (!Objects.equals(marketValue, property.marketValue)) return false;
        if (!Objects.equals(totalLivableArea, property.totalLivableArea)) return false;
        return zipCode != null ? zipCode.equals(property.zipCode) : property.zipCode == null;
    }

    @Override
    public String toString() {
        return "Property{" +
                "zipCode='" + zipCode + '\'' +
                ", marketValue=" + marketValue +
                ", totalLivableArea=" + totalLivableArea +
                '}';
    }
}