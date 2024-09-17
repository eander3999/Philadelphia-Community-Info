package philadelphia_info_calculator.datamanagement;

import philadelphia_info_calculator.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONReader {

    private String filePath;

    public JSONReader(String filePath) {

        this.filePath = filePath;
    }

    public List<Map<String, String>> readJSON() throws IOException, ParseException {
        List<Map<String, String>> records = new ArrayList<>();
        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader(filePath)) {
            Logger.getInstance().log("Opening CSV file: " + filePath);
            Object obj = parser.parse(reader);
            JSONArray jsonArray = (JSONArray) obj;

            for (Object element : jsonArray) {
                JSONObject jsonObject = (JSONObject) element;
                Map<String, String> record = jsonToMap(jsonObject);
                records.add(record);
            }
        }

        return records;
    }

    private Map<String, String> jsonToMap(JSONObject jsonObject) {
        Map<String, String> map = new HashMap<>();
        for (Object keyObj : jsonObject.keySet()) {
            String key = (String) keyObj;
            Object value = jsonObject.get(key);
            map.put(key, value.toString());
        }
        return map;
    }
}