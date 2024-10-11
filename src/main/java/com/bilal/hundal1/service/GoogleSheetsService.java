package com.bilal.hundal1.service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class GoogleSheetsService {

    private static final String APPLICATION_NAME = "googlesheetstoegang";
    private static final String SPREADSHEET_ID = "Your google sheet Id";  // Replace with your Google Sheets ID
    private static final String RANGE = "Blad1!A1:D1";
    @Autowired
    private  ObjectMapper objectMapper;
    private Sheets sheetsService;
  
    public GoogleSheetsService() throws GeneralSecurityException, IOException {
        this.sheetsService = createSheetsService();
    }

    private Sheets createSheetsService() throws GeneralSecurityException, IOException {

        InputStream in = getClass().getResourceAsStream("/path/your_keyforgooglesheet.json");

        GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/spreadsheets"));

        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

 // Method to write data to Google Sheets
    public void writeDataToSheet(List<String> jsonMessages) throws IOException {
    	List<List<Object>> rows = new ArrayList<>();

        // Extract field values (rows) from each message
        for (String messageBody : jsonMessages) {
            JsonNode messageNode = objectMapper.readTree(messageBody);
            List<Object> row = extractValues(messageNode);
            rows.add(row);  // Add each row of values
        }

        // Prepare the data for Google Sheets
        ValueRange body = new ValueRange().setValues(rows);

        // Write the data (only values) to Google Sheets
        sheetsService.spreadsheets().values()
                .append(SPREADSHEET_ID, RANGE, body)
                .setValueInputOption("RAW")
                .execute();
    }

    // Helper method to extract field values as row data
    private List<Object> extractValues(JsonNode jsonNode) {
    	List<Object> row = new ArrayList<>();
        jsonNode.fields().forEachRemaining(field -> {
            JsonNode value = field.getValue();
            if (value.isObject()) {
                // If the value is a nested object, extract its fields recursively
                row.addAll(extractValues(value));
            } else {
                row.add(value.asText());  // Convert each field value to text and add to the row
            }
        });
        return row;
    }
}
