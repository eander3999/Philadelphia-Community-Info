package philadelphia_info_calculator;

import philadelphia_info_calculator.processor.ApplicationProcessor;
import philadelphia_info_calculator.processor.PopulationProcessor;
import philadelphia_info_calculator.ui.UserInterface;
import philadelphia_info_calculator.logging.Logger;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import philadelphia_info_calculator.processor.PropertiesProcessor;
import philadelphia_info_calculator.processor.VaccinationProcessor;


public class Main {
    public static void main(String[] args) {
        String populationFile = null, propertiesFile = null, covidFile = null, logFile = null;
        Pattern pattern = Pattern.compile("^--(?<name>.+?)=(?<value>.+)$");

        for (String arg : args) {
            Matcher matcher = pattern.matcher(arg);
            if (!matcher.matches()) {
                System.err.println("Invalid argument format: " + arg);
                System.exit(1);
            }
            String name = matcher.group("name");
            String value = matcher.group("value");

            switch (name) {
                case "population":
                    populationFile = value;
                    break;
                case "properties":
                    propertiesFile = value;
                    break;
                case "covid":
                    covidFile = value;
                    break;
                case "log":
                    logFile = value;
                    break;
                default:
                    System.err.println("Unknown argument name: " + name);
                    System.exit(1);
            }
        }

        Logger logger = Logger.getInstance();
        try {
            if(logFile != null){
                logger.setLogFile(logFile);
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }

        Logger.getInstance().log("Command line arguments: " + Arrays.toString(args));
        boolean hasPopulationData = populationFile != null;
        boolean hasPropertiesData = propertiesFile != null;
        boolean hasCovidData = covidFile != null;
        UserInterface ui = new UserInterface();
        PopulationProcessor populationProcessor = new PopulationProcessor(populationFile);
        PropertiesProcessor propertiesProcessor = new PropertiesProcessor(propertiesFile);
        VaccinationProcessor vaccinationProcessor = new VaccinationProcessor(covidFile);
        ApplicationProcessor processor = new ApplicationProcessor(hasPopulationData, hasPropertiesData, hasCovidData, populationProcessor, propertiesProcessor, vaccinationProcessor, logger, ui);

        processor.run();

        propertiesProcessor.clearCache();
        vaccinationProcessor.clearCache();
        processor.clearCache();
        logger.close();
        System.gc();
    }
}