package philadelphia_info_calculator.ui;

import java.util.Map;
import java.util.Scanner;

public class UserInterface {
    private Scanner scanner;

    public UserInterface() {
        this.scanner = new Scanner(System.in);
    }


    public int getUserChoice() {
        while (true) {
            String input = scanner.nextLine();

            try {
                int choice = Integer.parseInt(input);
                if (choice >= 0 && choice <= 7) {
                    return choice;
                } else {
                    System.out.println("Invalid choice. Please enter a number between 0 and 7.");
                    showInputPrompt();
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer between 0 and 7.");
                showInputPrompt();
            }
        }
    }

    public static void displayMessage(String message) {
        System.out.println(message);
    }

    public static void showError(String error) {
        System.err.println("Error: " + error);
    }

    public static void displayVaccinationsPerCapita(Map<String, Double> vaccinationsPerCapita) {
        for (Map.Entry<String, Double> entry : vaccinationsPerCapita.entrySet()) {
            System.out.println(entry.getKey() + " " + String.format("%.4f", entry.getValue()));
        }
    }

    public String promptForDate() {
        String datePattern = "\\d{4}-\\d{2}-\\d{2}";
        while (true) {
            System.out.print("Enter the date (YYYY-MM-DD): ");
            String input = scanner.nextLine();
            if (input.matches(datePattern)) {
                return input;
            } else {
                System.out.println("Invalid date format. Please enter a date in the format YYYY-MM-DD.");
            }
        }
    }

    public String promptForZipCode() {
        String zipPattern = "\\d{5}";
        while (true) {
            System.out.print("Enter the 5-digit ZIP code: ");
            String input = scanner.nextLine();
            if (input.matches(zipPattern)) {
                return input;
            } else {
                System.out.println("Invalid ZIP code. Please enter a 5-digit ZIP code.");
            }
        }
    }

    public String promptForVaccinationType() {
        System.out.println("Please choose the type of vaccination data:");
        System.out.println("1. partial");
        System.out.println("2. full");
        System.out.print("> ");

        String type = "";
        while (true) {
            System.out.flush();
            String choice = scanner.nextLine().trim();
            if (choice.equals("1") || choice.equalsIgnoreCase("partial")) {
                return "partial";
            } else if (choice.equals("2") || choice.equalsIgnoreCase("full")) {
                return "full";
            } else {
                System.out.println("Invalid choice. Please enter \"partial\" or \"full\":");
                System.out.print("> ");
            }
        }
    }

    public static void showInputPrompt(){
        System.out.print("> ");
    }

    public void showAvailableActions(boolean hasPopulationData, boolean hasCovidData, boolean hasPropertiesData) {
        displayMessage("");
        displayMessage("0. Exit the program.");
        displayMessage("1. Show the available actions.");

        if (hasPopulationData) {
            displayMessage("2. Show the total population for all ZIP Codes.");
        }
        if (hasCovidData && hasPopulationData) {
            displayMessage("3. Show the total vaccinations per capita for each ZIP Code for the specified date.");
        }
        if (hasPropertiesData) {
            displayMessage("4. Show the average market value for properties in a specified ZIP Code.");
            displayMessage("5. Show the average total livable area for properties in a specified ZIP Code.");
            displayMessage("6. Show the total market value of properties, per capita, for a specified ZIP Code.");
        }
        displayMessage("7. Show resilience scores for each ZIP Code.");
    }


    public void close() {
        scanner.close();
    }
}