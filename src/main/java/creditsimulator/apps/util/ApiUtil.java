package creditsimulator.apps.util;

import creditsimulator.apps.exception.ApiException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiUtil {
    private final HttpClient httpClient;

    public ApiUtil() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public String callApi(String method, String requestBody, String endpoint) throws ApiException {
        System.out.println("call-api: " + endpoint + ", request: " + requestBody);

        try {
            URI uri = new URI(endpoint);

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", "application/json");

            // choose method
            HttpRequest request;
            if ("POST".equalsIgnoreCase(method)) {
                request = builder.POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
            } else if ("PUT".equalsIgnoreCase(method)) {
                request = builder.PUT(HttpRequest.BodyPublishers.ofString(requestBody)).build();
            } else if ("DELETE".equalsIgnoreCase(method)) {
                request = builder.DELETE().build();
            } else { // default GET
                request = builder.GET().build();
            }

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("call-api: " + endpoint + ", response: " + response.body());

            if (response.statusCode() >= 400) {
                throw new ApiException("HTTP error: " + response.statusCode() + " - " + response.body());
            }

            return response.body();

        } catch (Exception e) {
            throw new ApiException("Unexpected error: " + e.getMessage());
        }
    }
}
