package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.example.archiving.Modules.entity.WeatherData;

import java.io.IOException;

public class ElasticSearchProducer {
    private long expectedSequenceNumber;
    private boolean validSeqNumber;
    private RestClient client;
    private ObjectMapper objectMapper;
    public ElasticSearchProducer() {
        validSeqNumber = false;
        objectMapper = new ObjectMapper();
        RestClientBuilder builder = RestClient
                .builder(new HttpHost("elastic-service", 9200, "http"));
        this.client = builder.build();
    }

    private void processMessage(WeatherData weatherData) {
        if (!validSeqNumber) {
            expectedSequenceNumber = weatherData.getS_no();
            validSeqNumber = true;
        }
        long sequenceNumber = weatherData.getS_no();

        if (sequenceNumber == expectedSequenceNumber) {
            // Send to elastic-search
            String index = "station-" + weatherData.getStation_id();
            send(weatherData, index);
            // Update the expected sequence number
            expectedSequenceNumber++;
        }
        else if (sequenceNumber > expectedSequenceNumber) {
            // Calculate the number of dropped messages
            long droppedMessages = sequenceNumber - expectedSequenceNumber;

            String index = "dropped-station-" + weatherData.getStation_id();
            handleDroppedMessages(droppedMessages, index);

            // Update the expected sequence number to the current message
            expectedSequenceNumber = sequenceNumber + 1;
        }
    }

    private void handleDroppedMessages(long count, String index) {
        try {
            Request request = new Request("POST", "/" + index + "/_doc");
            String jsonPayload = "{\"count\": " + count + "}";
            request.setJsonEntity(jsonPayload);
            Response response = client.performRequest(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 201)
                System.out.println("dropped messages notification sent!");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(WeatherData weatherData, String index) {
        RestClientBuilder builder = RestClient
                .builder(new HttpHost("elastic-service", 9200, "http"));
        try (RestClient client = builder.build()) {
            Request request = new Request("POST", "/" + index + "/_doc");
            request.setJsonEntity(convertToJson(weatherData));
            Response response = client.performRequest(request);
            int statusCode = response.getStatusLine().getStatusCode();
            checkStatusCode(statusCode);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String convertToJson(WeatherData weatherData) throws JsonProcessingException {
        return objectMapper.writeValueAsString(weatherData);
    }

    private void checkStatusCode(int statusCode) {
        if (statusCode == 201) {
            System.out.println("Message sent to Elasticsearch successfully");
        } else {
            System.out.println("Failed to send message to Elasticsearch. Status code: " + statusCode);
        }
    }
}
