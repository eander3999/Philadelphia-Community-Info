package philadelphia_info_calculator.processor;

import philadelphia_info_calculator.datamanagement.PropertiesDataReader;
import philadelphia_info_calculator.util.Property;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PropertiesProcessor {
    private PropertiesDataReader dataReader;
    private Map<String, List<Property>> propertiesByZipCode;
    private MemoizationCache<String, Double> memoizationCache;
    private PropertyCalculator calculator;

    public PropertiesProcessor(String propertiesFilePath) {
        this.dataReader = new PropertiesDataReader(propertiesFilePath);
        this.memoizationCache = new MemoizationCache<>(100);
        this.calculator = new PropertyCalculator(new AverageMarketValueStrategy());
    }

    public int calculateAverage(String zipCode, String type) {
        ensureDataLoaded();
        List<Property> properties = propertiesByZipCode.getOrDefault(zipCode, List.of());
        switch (type) {
            case "marketValue":
                calculator.setStrategy(new AverageMarketValueStrategy());
                break;
            case "livableArea":
                calculator.setStrategy(new AverageLivableAreaStrategy());
                break;
        }
        return calculator.executeStrategy(zipCode, properties);
    }

    public int calculateTotalMarketValuePerCapita(String zipCode, int population) {
        if (population == 0) return 0;
        ensureDataLoaded();
        String cacheKey = zipCode + "_" + population;
        if (memoizationCache.containsKey(cacheKey)) {
            return memoizationCache.get(cacheKey).intValue();
        }

        List<Property> properties = propertiesByZipCode.getOrDefault(zipCode, List.of());
        double totalMarketValue = properties.stream()
                .mapToDouble(Property::getMarketValue)
                .sum();
        double marketValuePerCapita = totalMarketValue / population;
        memoizationCache.put(cacheKey, marketValuePerCapita);
        return (int) marketValuePerCapita;
    }

    private void ensureDataLoaded() {
        if (propertiesByZipCode == null) {
            List<Property> properties = dataReader.getProperties();
            propertiesByZipCode = properties.stream().collect(Collectors.groupingBy(Property::getZipCode));
        }
    }

    public Map<String, List<Property>> getPropertiesByZipCode() {
        ensureDataLoaded();
        return propertiesByZipCode;
    }

    public void clearCache() {
        memoizationCache.clear();
    }
}