package API;

import DTO.BatchDTO;
import DTO.LocationDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class GetLocationAPI {
    final  String LOCATION_URL = "https://api.hsa.edu.vn/exam/views/registration/available-location";

    public  List<LocationDTO> getLocation(String batchId, String token){
        try {
            URL url = new URL(LOCATION_URL + "?batchId=" + batchId);
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

            List<LocationDTO> locationDTOList = objectMapper.readValue(response.toString(), new TypeReference<List<LocationDTO>>(){});
            return locationDTOList;

        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
            return null;
        }

    }

}
