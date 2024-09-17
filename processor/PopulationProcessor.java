package philadelphia_info_calculator.processor;

import philadelphia_info_calculator.datamanagement.PopulationDataReader;
import philadelphia_info_calculator.util.PopulationData;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PopulationProcessor {
    private List<PopulationData> populationList;
    private Map<String, Integer> populationByZipCode;

    public PopulationProcessor(String populationFilePath) {
        PopulationDataReader reader = new PopulationDataReader(populationFilePath);
        populationList = reader.loadPopulationData();
        populationByZipCode = populationList.stream()
                .collect(Collectors.toMap(PopulationData::getZipCode, PopulationData::getPopulation, (oldValue, newValue) -> oldValue));
    }

    public int calculateTotalPopulation() {
        return populationList.stream()
                .mapToInt(PopulationData::getPopulation)
                .sum();
    }

    public int getPopulationForZipCode(String zipCode) {
        return populationByZipCode.getOrDefault(zipCode, 0);
    }

    public Map<String, Integer> getPopulationDataByZip() {
        return populationByZipCode;
    }
}