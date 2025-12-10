package collab;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {

    private final String baseUrl;
    private final ObjectMapper mapper;

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;

        ObjectMapper m = new ObjectMapper();
        m.registerModule(new JavaTimeModule());
        m.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        this.mapper = m;
    }

    // ============= GET with query params =============
    public <T> T getList(String path, Class<T> clazz, Object... queryParams) throws Exception {

        // Build URL with query parameters
        StringBuilder sb = new StringBuilder(baseUrl).append(path);

        if (queryParams.length > 0) {
            sb.append("?");
            for (int i = 0; i < queryParams.length; i += 2) {
                String key = queryParams[i].toString();
                String value = queryParams[i + 1].toString();
                sb.append(key).append("=").append(value);
                if (i + 2 < queryParams.length) sb.append("&");
            }
        }

        URL url = new URL(sb.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int status = conn.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("HTTP " + status);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        T result = mapper.readValue(reader, clazz);
        reader.close();
        return result;
    }

    // ============= POST with response =============
    public <T> T post(String path, Object body, Class<T> responseClass) throws Exception {
        URL url = new URL(baseUrl + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        String json = mapper.writeValueAsString(body);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }

        int status = conn.getResponseCode();
        if (status != 200) {
            BufferedReader err = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            String error = err.readLine();
            throw new RuntimeException("HTTP " + status + ": " + error);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        T result = mapper.readValue(reader, responseClass);
        reader.close();
        return result;
    }

    // ============= POST no response =============
    public void postNoResponse(String path, Object body) throws Exception {
        URL url = new URL(baseUrl + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        if (body != null) {
            String json = mapper.writeValueAsString(body);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }
        }

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("HTTP " + conn.getResponseCode());
        }
    }

    // ============= PUT no response =============
    public void putNoResponse(String path, Object body) throws Exception {
        URL url = new URL(baseUrl + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        if (body != null) {
            String json = mapper.writeValueAsString(body);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }
        }

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("HTTP " + conn.getResponseCode());
        }
    }
}
