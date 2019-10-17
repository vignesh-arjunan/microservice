package com.revolut.integration.test;

import org.junit.Before;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.Assert.assertEquals;

public class TestTransfer {

    @Before
    public void initDatabase() throws IOException, InterruptedException {
        HttpRequest mainRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:9080/api/v1/db/initialize"))
                .GET()
                .build();
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)  // this is the default
                .build();
        HttpResponse<String> response = httpClient.send(mainRequest, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void performTransfer() throws IOException, InterruptedException {
        for (int iteration = 1; iteration <= 100; iteration++) {
            double beforeFirstAccount = getAccountBalance(1);
            double beforeSecondAccount = getAccountBalance(2);
            System.out.println("getAccountBalance(1) = " + getAccountBalance(1));
            System.out.println("getAccountBalance(2) = " + getAccountBalance(2));
            performTransfer(1, 2, iteration);
            assertEquals(getAccountBalance(1), beforeFirstAccount - iteration, 0);
            assertEquals(getAccountBalance(2), beforeSecondAccount + iteration, 0);
        }
    }

    private double getAccountBalance(int id) throws IOException, InterruptedException {
        HttpRequest mainRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:9080/api/v1/accounts/" + id))
                .GET()
                .build();
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)  // this is the default
                .build();
        HttpResponse<String> response = httpClient.send(mainRequest, HttpResponse.BodyHandlers.ofString());
//        System.out.println("Response status code: " + response.statusCode());
//        System.out.println("Response headers: " + response.headers());
//        System.out.println("Response body: " + response.body());
        JsonReader jsonReader = Json.createReader(new StringReader(response.body()));
        return jsonReader.readObject().getJsonNumber("BALANCE").doubleValue();
    }

    private void performTransfer(int fromId, int toId, double amount) throws IOException, InterruptedException {
        JsonObjectBuilder payload = Json.createObjectBuilder();
        payload.add("fromAccount", fromId);
        payload.add("toAccount", toId);
        payload.add("amount", amount);
        payload.add("comment", "transferring amount " + amount);

        HttpRequest mainRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:9080/api/v1/transfers"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.build().toString()))
                .build();
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)  // this is the default
                .build();
        HttpResponse<String> response = httpClient.send(mainRequest, HttpResponse.BodyHandlers.ofString());
//        System.out.println("Response status code: " + response.statusCode());
//        System.out.println("Response headers: " + response.headers());
//        System.out.println("Response body: " + response.body());
    }
}
