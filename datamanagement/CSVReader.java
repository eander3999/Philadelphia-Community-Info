package philadelphia_info_calculator.datamanagement;

import philadelphia_info_calculator.logging.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVReader {
    String filePath;
    private Map<String, Integer> headerMap = new HashMap<>();

    public CSVReader(String filePath) throws IOException {
        this.filePath = filePath;
        Logger.getInstance().log("Opening CSV file: " + filePath);
        parseHeader();
    }

    private void parseHeader() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String[] headers = readRow(reader);
            if (headers != null) {
                for (int i = 0; i < headers.length; i++) {
                    headerMap.put(headers[i], i);
                }
            }
        }
    }

    public List<Map<String, String>> readCSV() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            List<Map<String, String>> records = new ArrayList<>();
            String[] row;
            while ((row = readRow(reader)) != null) {
                Map<String, String> record = new HashMap<>();
                for (String key : headerMap.keySet()) {
                    int index = headerMap.get(key);
                    if (index < row.length) {
                        record.put(key, row[index]);
                    } else {
                        record.put(key, null);
                    }
                }
                records.add(record);
            }
            return records;
        }
    }

    public String[] readRow(BufferedReader reader) throws IOException{
        List<String> fields = new ArrayList<>();
        StringBuilder fieldBuilder = new StringBuilder();
        boolean inQuotes = false;
        int c = reader.read();
        while (c != -1) {
            char currentChar = (char) c;

            if (inQuotes) {
                if (currentChar == '"') {
                    c = reader.read();
                    if (c == '"') {
                        fieldBuilder.append('"');
                    } else {
                        inQuotes = false;
                        continue;
                    }
                } else {
                    fieldBuilder.append(currentChar);
                }
            } else {
                if (currentChar == '"') {
                    if (fieldBuilder.length() == 0) {
                        inQuotes = true;
                    }
                } else if (currentChar == ',') {
                    fields.add(fieldBuilder.toString());
                    fieldBuilder.setLength(0);
                } else if (currentChar == '\r' || currentChar == '\n') {
                    fields.add(fieldBuilder.toString());
                    fieldBuilder.setLength(0);

                    if (currentChar == '\r' && ((char) reader.read()) != '\n') {
                        continue;
                    }
                    return fields.toArray(new String[0]);
                } else {
                    fieldBuilder.append(currentChar);
                }
            }
            c = reader.read();
        }

        if (fieldBuilder.length() > 0) {
            fields.add(fieldBuilder.toString());
        }

        return fields.isEmpty() ? null : fields.toArray(new String[0]);
    }
}