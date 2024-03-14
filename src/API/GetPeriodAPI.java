package API;

import DTO.AvailablePeriodDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class GetPeriodAPI {
    final  String URL = "https://api.hsa.edu.vn/exam/views/registration/available-period";

    public  AvailablePeriodDTO getBatch(String token) {
        try {
            URL url = new URL(URL);
            ObjectMapper objectMapper = new ObjectMapper();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("content-type", "application/json");
            connection.setRequestProperty("Accept", "application/json, text/plain, */*");
            connection.setRequestProperty("Accept-Language", "vi-VN,vi;q=0.9,en-US;q=0.8,en;q=0.7");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Authorization", "Bearer " + token);


            int responseCode = connection.getResponseCode();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            connection.disconnect();

            System.out.println("Response Code: " + responseCode);
            System.out.println("Response Body: " + response.toString());

            List<AvailablePeriodDTO> availablePeriodDTOS = objectMapper.readValue(response.toString(), new TypeReference<List<AvailablePeriodDTO>>(){});

            System.out.println(availablePeriodDTOS.get(0));

            return availablePeriodDTOS.get(0);
        } catch (Exception e){
            System.out.println();
            System.out.println("Error: " + e.getMessage());

            return null;
        }
    }
}
