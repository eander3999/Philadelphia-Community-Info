package philadelphia_info_calculator.datamanagement;

import philadelphia_info_calculator.util.CovidData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class VaccinationDataReader {

    private String filePath;
    private List<CovidData> cachedData = null;

    public VaccinationDataReader(String filePath) {
        this.filePath = filePath;
    }

    public List<CovidData> readVaccinationData() {
        if (cachedData == null) {
            cachedData = loadData();
        }
        return cachedData;
    }

    private List<CovidData> loadData() {
        String extension = getFileExtension(filePath);
        if ("csv".equals(extension)) {
            return readVaccinationDataFromCSV(filePath);
        } else if ("json".equals(extension)) {
            return readVaccinationDataFromJSON(filePath);
        } else {
            throw new IllegalArgumentException("Unsupported file format: " + extension);
        }
    }

    private String getFileExtension(String filePath) {
        int i = filePath.lastIndexOf('.');
        if (i > 0) {
            return filePath.substring(i + 1).toLowerCase();
        }
        return "";
    }

    private List<CovidData> readVaccinationDataFromCSV(String filePath) {
        List<CovidData> data = new ArrayList<>();
        try {
            CSVReader csvReader = new CSVReader(filePath);
            List<Map<String, String>> records = csvReader.readCSV();
            for (Map<String, String> record : records) {
                String zipCode = record.get("zip_code");
                String dateTime = record.get("etl_timestamp");
                String date = extractDatePart(dateTime);
                int partiallyVaccinated = parseIntWithDefault(record.get("partially_vaccinated"), 0);
                int fullyVaccinated = parseIntWithDefault(record.get("fully_vaccinated"), 0);

                CovidData covidData = new CovidData(
                        zipCode,
                        date,
                        partiallyVaccinated,
                        fullyVaccinated
                );
                data.add(covidData);
            }
        } catch (IOException e) {
            System.err.println("Error reading vaccination data from CSV: " + e.getMessage());
        }
        return data;
    }

    private int parseIntWithDefault(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private List<CovidData> readVaccinationDataFromJSON (String filePath){
        List<CovidData> data = new ArrayList<>();
        JSONReader jsonReader = new JSONReader(filePath);
        try {
            List<Map<String, String>> records = jsonReader.readJSON();
            for (Map<String, String> record : records) {
                String zipCode = record.get("zip_code");
                if(!isValidZipCode(zipCode)){
                    continue;
                }
                String dateTime = record.get("etl_timestamp");
                String date = extractDatePart(dateTime);
                if (!isValidDate(date)) {
                    continue;
                }
                int partiallyVaccinated = parseIntWithDefault(record.get("partially_vaccinated"), 0);
                int fullyVaccinated = parseIntWithDefault(record.get("fully_vaccinated"), 0);

                CovidData covidData = new CovidData(
                        zipCode,
                        date,
                        partiallyVaccinated,
                        fullyVaccinated
                );
                data.add(covidData);
            }
        } catch (Exception e) {
            System.err.println("Error reading vaccination data from JSON: " + e.getMessage());
        }
        return data;
    }

    private String extractDatePart(String dateTime) {
        return dateTime.split(" ")[0];
    }

    public static boolean isValidZipCode(String zipCode) {
        return zipCode != null && zipCode.matches("\\d{5}");
    }

    public static boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}