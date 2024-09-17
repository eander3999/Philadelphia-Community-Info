package philadelphia_info_calculator.processor;

import philadelphia_info_calculator.datamanagement.VaccinationDataReader;
import philadelphia_info_calculator.util.CovidData;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VaccinationProcessor {
    private Map<String, List<CovidData>> vaccinationDataByZip;
    private MemoizationCache<String, Double> memoizationCache;
    private final String vaccinationFilePath;
    private boolean dataLoaded = false;

    public VaccinationProcessor(String vaccinationFilePath) {
        this.vaccinationFilePath = vaccinationFilePath;
        this.vaccinationDataByZip = null;
        this.memoizationCache = new MemoizationCache<>(1000);
    }

    public double calculateVaccinationsPerCapita(String date, String type, String zipCode, int population) {
        loadVaccinationData();

        String cacheKey = date + "|" + type + "|" + zipCode;
        if (memoizationCache.containsKey(cacheKey)) {
            return memoizationCache.get(cacheKey);
        }

        List<CovidData> records = vaccinationDataByZip.getOrDefault(zipCode, List.of());
        double vaccinations = 0;
        for (CovidData data : records) {
            if (data.getDate().equals(date)) {
                if ("partial".equals(type)) {
                    vaccinations += data.getPartiallyVaccinated();
                } else if ("full".equals(type)) {
                    vaccinations += data.getFullyVaccinated();
                }
            }
        }

        double perCapita = population > 0 ? vaccinations / population : 0;
        memoizationCache.put(cacheKey, perCapita);
        return perCapita;
    }

    private void loadVaccinationData() {
        if (!dataLoaded) {
            VaccinationDataReader reader = new VaccinationDataReader(vaccinationFilePath);
            List<CovidData> vaccinationData = reader.readVaccinationData();
            vaccinationDataByZip = vaccinationData.stream()
                    .collect(Collectors.groupingBy(CovidData::getZipCode));
            dataLoaded = true;
        }
    }

    public Map<String, List<CovidData>> getVaccinationDataByZip() {
        loadVaccinationData();
        return vaccinationDataByZip;
    }

    public void clearCache() {
        memoizationCache.clear();
    }

}