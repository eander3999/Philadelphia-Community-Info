package philadelphia_info_calculator.util;

import java.util.Objects;

public class PopulationData {
    private String zipCode;
    private int population;

    public PopulationData(String zipCode, int population) {
        this.zipCode = zipCode;
        this.population = population;
    }

    public String getZipCode() {
        return zipCode;
    }

    public int getPopulation() {
        return population;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PopulationData that = (PopulationData) o;
        return population == that.population &&
                zipCode.equals(that.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zipCode, population);
    }

    @Override
    public String toString() {
        return "PopulationData{" +
                "zipCode='" + zipCode + '\'' +
                ", population=" + population +
                '}';
    }
}