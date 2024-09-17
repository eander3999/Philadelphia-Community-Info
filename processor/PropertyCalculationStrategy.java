package philadelphia_info_calculator.processor;
import philadelphia_info_calculator.util.Property;
import java.util.List;

public interface PropertyCalculationStrategy {
    int calculate(List<Property> properties);
    default int calculateWithMemoization(String zipCode, List<Property> properties, MemoizationCache<String, Integer> cache) {
        if (cache.containsKey(zipCode)) {
            return cache.get(zipCode);
        }
        int result = calculate(properties);
        cache.put(zipCode, result);
        return result;
    }
}
