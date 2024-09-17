package philadelphia_info_calculator.processor;
import philadelphia_info_calculator.util.Property;
import java.util.List;
public class AverageLivableAreaStrategy implements PropertyCalculationStrategy {
    @Override
    public int calculate(List<Property> properties) {
        if (properties.isEmpty()) return 0;
        double averageLivableArea = properties.stream()
                .filter(property -> property.getTotalLivableArea() != null)
                .mapToDouble(Property::getTotalLivableArea)
                .average()
                .orElse(0.0);
        return (int) averageLivableArea;
    }
}
