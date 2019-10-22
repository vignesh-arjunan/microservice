package com.micro.integration.test;

import org.junit.Before;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.Assert.*;

public class TestAPIs {

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
    public void testAccountExists() throws IOException, InterruptedException {
        System.out.println(getAccountDetails(1));
        System.out.println(getAccountDetails(2));
        System.out.println(getAccountDetails(3));
        System.out.println(getAccountDetails(4));
        assertNotNull(getAccountDetails(1));
        assertNotNull(getAccountDetails(2));
        assertNotNull(getAccountDetails(3));
        assertNotNull(getAccountDetails(4));
    }

    @Test
    public void testAccountDoesNotExists() throws IOException, InterruptedException {
        assertNull(getAccountDetails(5));
        assertNull(getAccountDetails(6));
        assertNull(getAccountDetails(7));
    }

    @Test
    public void performInvalidAccountTransfer() throws IOException, InterruptedException {
        HttpResponse<String> response = performTransfer(3, 100, 1_000);
        assertEquals(response.statusCode(), 400);
        JsonReader jsonReader = Json.createReader(new StringReader(response.body()));
        assertEquals(jsonReader.readObject().getString("msg"), "To Account does not exist !!");

        response = performTransfer(390, 2, 1_000);
        assertEquals(response.statusCode(), 400);
        jsonReader = Json.createReader(new StringReader(response.body()));
        assertEquals(jsonReader.readObject().getString("msg"), "From Account does not exist !!");
    }

    @Test
    public void performInvalidAmountTransfer() throws IOException, InterruptedException {
        HttpResponse<String> response = performTransfer(3, 4, 1_000_000);
        assertEquals(response.statusCode(), 400);
        JsonReader jsonReader = Json.createReader(new StringReader(response.body()));
        assertEquals(jsonReader.readObject().getString("msg"), "Insufficient Balance !!");
    }

    @Test
    public void performTransfer() throws IOException, InterruptedException {
        for (int iteration = 1; iteration <= 100; iteration++) {
            JsonObject firstAccountDetails = getAccountDetails(1);
            assertNotNull(firstAccountDetails);
            JsonObject secondAccountDetails = getAccountDetails(2);
            assertNotNull(secondAccountDetails);
            double beforeFirstAccount = firstAccountDetails.getJsonNumber("BALANCE").doubleValue();
            double beforeSecondAccount = secondAccountDetails.getJsonNumber("BALANCE").doubleValue();
            System.out.println("performing transfer from 1 -> 2 for " + iteration);
            performTransfer(1, 2, iteration);
            System.out.println("getAccountDetails(1) = " + getAccountDetails(1));
            System.out.println("getAccountDetails(2) = " + getAccountDetails(2));
            firstAccountDetails = getAccountDetails(1);
            assertNotNull(firstAccountDetails);
            secondAccountDetails = getAccountDetails(2);
            assertNotNull(secondAccountDetails);
            assertEquals(firstAccountDetails.getJsonNumber("BALANCE").doubleValue(),
                    beforeFirstAccount - iteration,
                    0);
            assertEquals(secondAccountDetails.getJsonNumber("BALANCE").doubleValue(),
                    beforeSecondAccount + iteration,
                    0);
        }
    }

    private JsonObject getAccountDetails(int id) throws IOException, InterruptedException {
        HttpRequest mainRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:9080/api/v1/accounts/" + id))
                .GET()
                .build();
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)  // this is the default
                .build();
        HttpResponse<String> response = httpClient.send(mainRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return null;
        }
//        System.out.println("Response status code: " + response.statusCode());
//        System.out.println("Response headers: " + response.headers());
//        System.out.println("Response body: " + response.body());
        JsonReader jsonReader = Json.createReader(new StringReader(response.body()));
        return jsonReader.readObject();
    }


    private HttpResponse<String> performTransfer(int fromId, int toId, double amount) throws IOException, InterruptedException {
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
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        return httpClient.send(mainRequest, HttpResponse.BodyHandlers.ofString());
    }
}
