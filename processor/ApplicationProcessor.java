package philadelphia_info_calculator.processor;

import philadelphia_info_calculator.ui.UserInterface;
import philadelphia_info_calculator.logging.Logger;

import java.util.*;

import philadelphia_info_calculator.util.CovidData;

import philadelphia_info_calculator.util.Property;


public class ApplicationProcessor {
    private boolean hasPopulationData;
    private boolean hasPropertiesData;
    private boolean hasCovidData;
    private PopulationProcessor populationProcessor;
    private PropertiesProcessor propertiesProcessor;
    private VaccinationProcessor vaccinationProcessor;
    private Logger logger;
    private UserInterface userInterface;
    private PropertyCalculator propertyCalculator;
    private MemoizationCache<String, Double> memoizationCache;

    public ApplicationProcessor(boolean hasPopulationData, boolean hasPropertiesData, boolean hasCovidData, PopulationProcessor populationProcessor, PropertiesProcessor propertiesProcessor, VaccinationProcessor vaccinationProcessor, Logger logger, UserInterface userInterface){
        this.hasPopulationData = hasPopulationData;
        this.hasPropertiesData = hasPropertiesData;
        this.hasCovidData = hasCovidData;
        if(hasPopulationData){
            this.populationProcessor = populationProcessor;
        }
        if(hasPropertiesData){
            this.propertiesProcessor = propertiesProcessor;
        }
        if(hasCovidData){
            this.vaccinationProcessor = vaccinationProcessor;
        }
        this.logger = logger;
        this.propertyCalculator = new PropertyCalculator(new AverageMarketValueStrategy());
        this.memoizationCache = new MemoizationCache<>(1000);
        this.userInterface = userInterface;
    }

    public void run() {
        boolean running = true;
        processAvailableActions();
        UserInterface.showInputPrompt();
        while (running) {
            int action = userInterface.getUserChoice();
            if (action == 0) {
                running = false;
            } else {
                processUserAction(action);
            }
        }
    }

    public void processAvailableActions() {
        userInterface.showAvailableActions(hasPopulationData, hasCovidData, hasPropertiesData);
    }

    public void processUserAction(int action) {
        logger.log("User selected action: " + action);
        String zipCode;

        switch (action) {
            case 0:
                UserInterface.displayMessage("Exiting program...");
                System.exit(0);
                break;
            case 1:
                UserInterface.displayMessage("\nBEGIN OUTPUT");
                processAvailableActions();
                UserInterface.displayMessage("END OUTPUT");
                processAvailableActions();
                UserInterface.showInputPrompt();
                break;
            case 2:
                if(!hasPopulationData){
                    UserInterface.showError("Population data is not available.");
                    processAvailableActions();
                    UserInterface.showInputPrompt();
                    break;
                }
                UserInterface.displayMessage("\nBEGIN OUTPUT");
                showTotalPopulation();
                UserInterface.displayMessage("END OUTPUT");
                processAvailableActions();
                UserInterface.showInputPrompt();
                break;
            case 3:
                if(!hasCovidData || !hasPopulationData){
                    UserInterface.showError("Data is not available.");
                    processAvailableActions();
                    UserInterface.showInputPrompt();
                    break;
                }
                String vaccinationType = userInterface.promptForVaccinationType();
                String date = userInterface.promptForDate();
                UserInterface.displayMessage("\nBEGIN OUTPUT");
                showVaccinationsPerCapita(date, vaccinationType);
                UserInterface.displayMessage("END OUTPUT");
                processAvailableActions();
                UserInterface.showInputPrompt();
                break;
            case 4:
                if(!hasPropertiesData){
                    UserInterface.showError("Property data is not available.");
                    processAvailableActions();
                    UserInterface.showInputPrompt();
                    break;
                }
                propertyCalculator.setStrategy(new AverageMarketValueStrategy());
                showAveragePropertyMetric("marketValue");
                break;
            case 5:
                if(!hasPropertiesData){
                    UserInterface.showError("Property data is not available.");
                    processAvailableActions();
                    UserInterface.showInputPrompt();
                    break;
                }
                propertyCalculator.setStrategy(new AverageLivableAreaStrategy());
                showAveragePropertyMetric("livableArea");
                break;
            case 6:
                if(!hasPropertiesData || !hasPopulationData){
                    UserInterface.showError("Data is not available.");
                    processAvailableActions();
                    UserInterface.showInputPrompt();
                    break;
                }
                zipCode = userInterface.promptForZipCode();
                UserInterface.displayMessage("\nBEGIN OUTPUT");
                showTotalMarketValuePerCapita(zipCode);
                UserInterface.displayMessage("END OUTPUT");
                processAvailableActions();
                UserInterface.showInputPrompt();
                break;
            case 7:
                if(!hasPopulationData || !hasPropertiesData || !hasCovidData){
                    UserInterface.showError("Data is not available.");
                    processAvailableActions();
                    UserInterface.showInputPrompt();
                    break;
                }
                UserInterface.displayMessage("\nBEGIN OUTPUT");
                calculateAndDisplayCommunityResilienceScores();
                UserInterface.displayMessage("END OUTPUT");
                processAvailableActions();
                UserInterface.showInputPrompt();
                break;
            default:
                UserInterface.showError("Invalid action. Please try again.");
                UserInterface.showInputPrompt();
                break;
        }
    }

    public void showTotalPopulation() {
        int totalPopulation = populationProcessor.calculateTotalPopulation();
        UserInterface.displayMessage(String.valueOf(totalPopulation));
    }

    public void showVaccinationsPerCapita(String date, String vaccinationType) {
        TreeMap<String, Double> vaccinationsPerCapita = new TreeMap<>(getVaccinationsPerCapita(date, vaccinationType));
        UserInterface.displayVaccinationsPerCapita(vaccinationsPerCapita);
    }

    private Map<String, Double> getVaccinationsPerCapita(String date, String vaccinationType) {
        Map<String, Double> vaccinationsPerCapita = new HashMap<>();
        Map<String, List<CovidData>> vaccinationDataByZip = vaccinationProcessor.getVaccinationDataByZip();

        for (Map.Entry<String, List<CovidData>> entry : vaccinationDataByZip.entrySet()) {
            String zip = entry.getKey();
            int population = populationProcessor.getPopulationForZipCode(zip);
            if (population > 0) {
                double perCapita = vaccinationProcessor.calculateVaccinationsPerCapita(date, vaccinationType, zip, population);
                if (perCapita > 0.0) {
                    vaccinationsPerCapita.put(zip, perCapita);
                }
            }
        }
        return vaccinationsPerCapita;
    }

    private void showAveragePropertyMetric(String metricType) {
        String zipCode = userInterface.promptForZipCode();
        UserInterface.displayMessage("\nBEGIN OUTPUT");
        System.out.println(propertiesProcessor.calculateAverage(zipCode, metricType));
        UserInterface.displayMessage("END OUTPUT");
        processAvailableActions();
        UserInterface.showInputPrompt();
    }

    private void showTotalMarketValuePerCapita(String zipCode) {
        int marketValuePerCapita = propertiesProcessor.calculateTotalMarketValuePerCapita(zipCode, populationProcessor.getPopulationForZipCode(zipCode));
        System.out.println(marketValuePerCapita);
    }

    public Map<String, Integer> getPropertyValuesPerCapita() {
        Map<String, Integer> propertyValuesPerCapita = new HashMap<>();
        for (Map.Entry<String, List<Property>> entry : propertiesProcessor.getPropertiesByZipCode().entrySet()) {
            String zip = entry.getKey();
            List<Property> properties = entry.getValue();
            double totalMarketValue = properties.stream()
                    .mapToDouble(Property::getMarketValue)
                    .sum();
            int population = populationProcessor.getPopulationForZipCode(zip);
            if (population > 0) {
                propertyValuesPerCapita.put(zip, (int) (totalMarketValue / population));
            } else {
                propertyValuesPerCapita.put(zip, 0);
            }
        }
        return propertyValuesPerCapita;
    }

    public Map<String, Double> getVaccinationRates() {
        Map<String, Double> vaccinationRates = new HashMap<>();
        for (Map.Entry<String, List<CovidData>> entry : vaccinationProcessor.getVaccinationDataByZip().entrySet()) {
            String zip = entry.getKey();
            List<CovidData> vaccinations = entry.getValue();
            double totalVaccinations = vaccinations.stream()
                    .mapToDouble(data -> data.getFullyVaccinated() + data.getPartiallyVaccinated())
                    .sum();
            int population = populationProcessor.getPopulationForZipCode(zip);
            if (population > 0) {
                vaccinationRates.put(zip, totalVaccinations / population);
            } else {
                vaccinationRates.put(zip, 0.0);
            }
        }
        return vaccinationRates;
    }

    private double calculateResilienceScore(double population, double propertyValue, double vaccinationRate) {

        double weightPopulation = 0.2;
        double weightPropertyValue = 0.4;
        double weightVaccinationRate = 0.4;

        return (population * weightPopulation) + (propertyValue * weightPropertyValue) + (vaccinationRate * weightVaccinationRate);
    }

    public void calculateAndDisplayCommunityResilienceScores() {
        Map<String, Integer> scores = new TreeMap<>();
        Map<String, Integer> populationByZip = populationProcessor.getPopulationDataByZip();
        Map<String, Integer> propertyValuesPerCapita = getPropertyValuesPerCapita();
        Map<String, Double> vaccinationRates = getVaccinationRates();

        for (String zip : populationByZip.keySet()) {
            if (!memoizationCache.containsKey(zip)) {
                int population = populationByZip.get(zip);
                int propertyValue = propertyValuesPerCapita.getOrDefault(zip, 0);
                double vaccinationRate = vaccinationRates.getOrDefault(zip, 0.0);
                double score = calculateResilienceScore(population, propertyValue, vaccinationRate);
                memoizationCache.put(zip, score);
            }
            scores.put(zip, (int) (double) memoizationCache.get(zip));
        }

        scores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> UserInterface.displayMessage("ZIP: " + entry.getKey() + ", Score: " + entry.getValue()));
    }

    public void clearCache() {
        memoizationCache.clear();
    }

}