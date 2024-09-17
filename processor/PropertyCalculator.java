package philadelphia_info_calculator.processor;
import philadelphia_info_calculator.util.Property;

import java.util.List;

public class PropertyCalculator {
    private PropertyCalculationStrategy strategy;
    private MemoizationCache<String, Integer> cache = new MemoizationCache<>(100);

    public PropertyCalculator(PropertyCalculationStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(PropertyCalculationStrategy strategy) {
        this.strategy = strategy;
        this.cache.clear();
    }

    public int executeStrategy(String zipCode, List<Property> properties) {
        return strategy.calculateWithMemoization(zipCode, properties, cache);
    }
}
