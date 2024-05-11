package ai.j0b.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnthropicAPI {
    private String apiKey;
    private String apiVersion;
    private String model;

    public AnthropicAPI(String apiKey, String apiVersion, String model) {
        this.apiKey = apiKey;
        this.apiVersion = apiVersion;
        this.model = model;
    }

    public String generateText(List<Map<String, String>> messages, int maxTokens) {
        int maxRetries = 2;
        int retryDelay = 1000; // Delay in milliseconds

        for (int retry = 0; retry < maxRetries; retry++) {
            try {
                String url = "https://api.anthropic.com/v1/messages";
                Map<String, Object> data = new HashMap<>();
                data.put("model", model);
                data.put("messages", messages);
                data.put("max_tokens", maxTokens);
                String jsonData = new Gson().toJson(data);

                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("x-api-key", apiKey);
                con.setRequestProperty("anthropic-version", apiVersion);
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);

                OutputStream os = con.getOutputStream();
                os.write(jsonData.getBytes());
                os.flush();
                os.close();

                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    String responseContent = response.toString();

                    JsonObject jsonResponse = JsonParser.parseString(responseContent).getAsJsonObject();
                    if (jsonResponse.has("content")) {
                        JsonArray contentArray = jsonResponse.getAsJsonArray("content");
                        if (contentArray != null && contentArray.size() > 0) {
                            JsonObject firstElement = contentArray.get(0).getAsJsonObject();
                            if (firstElement.has("text")) {
                                return firstElement.get("text").getAsString();
                            }
                        }
                    }
                    return null;
                } else {
                    // Handle error case
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    String errorLine;
                    StringBuilder errorResponse = new StringBuilder();
                    while ((errorLine = errorReader.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }
                    errorReader.close();
                    Log.e("AnthropicAPI", "API request failed with response code: " + responseCode);
                    Log.e("AnthropicAPI", "Error response: " + errorResponse.toString());
                    throw new IOException("API request failed with response code: " + responseCode);
                }
            } catch (IOException e) {
                if (retry < maxRetries - 1) {
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ex) {
                        Log.e("AnthropicAPI", "Thread interrupted", ex);
                        Thread.currentThread().interrupt();
                    }
                } else {
                    Log.e("AnthropicAPI", "Error occurred during API request", e);
                    return null;
                }
            } catch (Exception e) {
                Log.e("AnthropicAPI", "Unexpected error occurred", e);
                return null;
            }
        }
        return null;
    }
}