package philadelphia_info_calculator.util;

public class CovidData {
    private String zipCode;
    private String date;
    private int partiallyVaccinated;
    private int fullyVaccinated;

    public CovidData(String zipCode, String date, int partiallyVaccinated, int fullyVaccinated) {
        this.zipCode = zipCode;
        this.date = date;
        this.partiallyVaccinated = partiallyVaccinated;
        this.fullyVaccinated = fullyVaccinated;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getDate() {
        return date;
    }

    public int getPartiallyVaccinated() {
        return partiallyVaccinated;
    }

    public int getFullyVaccinated() {
        return fullyVaccinated;
    }

}