package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        JsonObject result = new JsonObject();

        try {
            
            // Create JSON arrays that will hold our data
            
            JsonArray prodNums = new JsonArray();
            JsonArray colHeadings = new JsonArray();
            JsonArray data = new JsonArray();

            // Store all the rows in a String
            final String[] rows = csvString.split("\n");
            
            // Adding all the column headings to their JSON array. Being sure to remove the double quotes and trimming it
            for ( String x : rows[0].split("\",") ) {
                colHeadings.add(x.replace("\"", "").trim());
            }
            
            // Loop through the leftover rows 
            for ( int i = 1; i < rows.length; i++) {
                // Store the column in a String array (removing doulbe quotes)
                // Create JSON array to store the inner data that will be placed within the data JSON array
                final String[] col = rows[i].split("\",");
                JsonArray innerData = new JsonArray();
                
                prodNums.add(col[0].replace("\"", "").trim());
                
                // Looping through the leftover columns within the row
                for(int j = 1; j < col.length; j++ ) {
                    // Store the column String being sure to remove the double quotes and trimming it
                    final String x = col[j].replace("\"", "").trim();
                    
                    // Try/catch to check if String is an int. If so, add it to the innerData as an int. Otherwise, add the String to the innerData
                    try {
                        
                        final int num = Integer.parseInt(x);
                        innerData.add(num);
                        
                    } catch(NumberFormatException e) {
                        
                        innerData.add(x);
                        
                    }
                }
                
                // Adding the innerData JSON array to the data JSON array
                data.add(innerData);
            }
            
            // Adding all the other JSON arrays to the result JSON object
            result.put("ProdNums", prodNums);
            result.put("ColHeadings", colHeadings);
            result.put("Data", data);
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return Jsoner.serialize(result);
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = "";
        
        try {
            
            // INSERT YOUR CODE HERE
            JsonObject json = Jsoner.deserialize(jsonString, new JsonObject());
            
            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer, ',', '"', '\\', "\n");
            
            JsonArray colHeadings = (JsonArray) json.get("ColHeadings");
            JsonArray prodNum = (JsonArray) json.get("ProdNums");
            JsonArray data = (JsonArray) json.get("Data");

            csvWriter.writeNext(jsonArrayToStringArray(colHeadings));
            
            for(int i = 0; i < data.size(); i++) {
                String[] arr = new String[colHeadings.size()];
                
                arr[0] = (String) prodNum.get(i);
                
                JsonArray innerData = ((JsonArray) data.get(i));
                
                for(int j = 0; j < innerData.size(); j++) {
                    String str = ((JsonArray) data.get(i)).get(j) + "";
                    
                    if( j == 2 ) {
                        arr[j+1] = String.format("%02d", Integer.parseInt(str));
                    } else {
                        arr[j+1] = str;
                    }
                }
                csvWriter.writeNext(arr);
            }
            
            result = writer.toString();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    private static String[] jsonArrayToStringArray(JsonArray jsonArray) {
        Object[] arr = jsonArray.toArray();
        
        String[] stringArray = new String[arr.length];
        
        for(int i = 0; i < arr.length; i++) {
            stringArray[i] = (String) arr[i];
        }

        return stringArray;
    }
    
}
