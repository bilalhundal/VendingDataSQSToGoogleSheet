package com.bilal.hundal1.functions;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.bilal.hundal1.service.GoogleSheetsService;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
@Component
public class SqsToGoogleSheetsFunction implements Function<SQSEvent, String> {
	@Autowired
	private  GoogleSheetsService googleSheetsService;

	    @Override
	    public String apply(SQSEvent event) {
	        try {
	            // Convert SQS messages to JSON string list
	            List<String> jsonMessages = event.getRecords().stream()
	                    .map(record -> record.getBody())  // Extract message body from SQSEvent.SQSMessage
	                    .collect(Collectors.toList());
	            if (!jsonMessages.isEmpty()) {
	                System.out.println("First Message Body: " + jsonMessages.get(0));
	            }

	            // Write data to Google Sheets
	            googleSheetsService.writeDataToSheet(jsonMessages);
	            return "Successfully wrote to Google Sheets";
	        } catch (Exception e) {
	            e.printStackTrace();
	            return "Error writing to Google Sheets: " + e.getMessage();
	        }
	    }

}





