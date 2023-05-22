package com.example.Adapter;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class APICaller {
    String uri;
    APICaller(String Uri){
        this.uri = Uri; 
    }
    public String sendRequest() throws ClientProtocolException, IOException{
        HttpClient httpClient = HttpClients.createDefault();

        // Create the HTTP GET request
        HttpGet request = new HttpGet(uri);

        // Execute the request
        HttpResponse response;
        response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        // Convert the response entity to a string
        String  responseString= EntityUtils.toString(entity);
        return responseString;
       
    }
}
