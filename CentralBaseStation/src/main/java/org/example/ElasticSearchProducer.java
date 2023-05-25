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
    public void send(WeatherData weatherData, String index) {
        RestClientBuilder builder = RestClient
                .builder(new HttpHost("localhost", 9200, "http"));
        try (RestClient client = builder.build()) {
            Request request = new Request("POST", "/" + index + "/_doc");
            request.setJsonEntity(convertToJson(weatherData));
            Response response = client.performRequest(request);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 201) {
                System.out.println("Message sent to Elasticsearch successfully");
            } else {
                System.out.println("Failed to send message to Elasticsearch. Status code: " + statusCode);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String convertToJson(WeatherData weatherData) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(weatherData);
    }
}
