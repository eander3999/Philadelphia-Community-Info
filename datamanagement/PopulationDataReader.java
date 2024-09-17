package philadelphia_info_calculator.datamanagement;

import philadelphia_info_calculator.util.PopulationData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PopulationDataReader {

    private CSVReader csvReader;

    public PopulationDataReader(String filePath) {
        try {
            this.csvReader = new CSVReader(filePath);
        } catch (IOException e) {
            System.err.println("Error reading population data: " + e.getMessage());
        }
    }

    public List<PopulationData> loadPopulationData() {
        List<PopulationData> populationList = new ArrayList<>();
        try {
            List<Map<String, String>> records = csvReader.readCSV();
            for (Map<String, String> record : records) {
                String zipCode = record.get("zip_code");
                String populationStr = record.get("population");
                if (isValidZipCode(zipCode) && isNumeric(populationStr)) {
                    int population = Integer.parseInt(populationStr);
                    PopulationData populationData = new PopulationData(zipCode, population);
                    populationList.add(populationData);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading population data: " + e.getMessage());
        }
        return populationList;
    }

    private boolean isValidZipCode(String zipCode) {
        return zipCode != null && zipCode.matches("\\d{5}");
    }

    private boolean isNumeric(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}