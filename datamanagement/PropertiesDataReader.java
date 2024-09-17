package philadelphia_info_calculator.datamanagement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import philadelphia_info_calculator.util.Property;

public class PropertiesDataReader {

    private CSVReader csvReader;
    private String filePath;
    private boolean isDataLoaded = false;
    private List<Property> properties;

    public PropertiesDataReader(String filePath) {
        this.filePath = filePath;
    }

    private void loadData() {
        if (isDataLoaded) return;
        try {
            this.csvReader = new CSVReader(filePath);
            this.properties = new ArrayList<>();
            List<Map<String, String>> records = csvReader.readCSV();
            for (Map<String, String> record : records) {
                String zipCode = sanitizeZipCode(record.get("zip_code"));
                String marketValueStr = record.get("market_value");
                String livableAreaStr = record.get("total_livable_area");

                if (zipCode != null) {
                    Double marketValue = parseDouble(marketValueStr);
                    Double livableArea = parseDouble(livableAreaStr);
                    Property property = new Property(zipCode, marketValue, livableArea);
                    properties.add(property);
                }
            }
            isDataLoaded = true;
        } catch (IOException e) {
            System.err.println("Error reading property data: " + e.getMessage());
        }
    }

    public List<Property> getProperties() {
        loadData();
        return properties;
    }

    private String sanitizeZipCode(String zipCode) {
        if (zipCode != null && zipCode.matches("\\d{5}(-\\d{4})?")) {
            return zipCode.substring(0, 5);
        }
        return null;
    }

    private Double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}