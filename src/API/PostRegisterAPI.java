package API;

import DTO.ErrorResponseDTO;
import DTO.PostRegisterDTO;
import DTO.RegisterIdDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostRegisterAPI {
    final  String REGISTER_URL = "https://api.hsa.edu.vn/exam/register";
    public Boolean postRegister(String token, String batchId,
                                    String locationId, String slotId,
                                    String periodId, String accountId) throws Exception{

        URL url = new URL(REGISTER_URL + "?accountId=" + accountId);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        ObjectMapper objectMapper = new ObjectMapper();

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();


          RegisterIdDTO registerIdDTO = new RegisterIdDTO();
          registerIdDTO.setBatchId(batchId);
          registerIdDTO.setLocationId(locationId);
          registerIdDTO.setSlotId(slotId);
          registerIdDTO.setPeriodId(periodId);

          PostRegisterDTO postRegisterDTO = new PostRegisterDTO();
          postRegisterDTO.setRecord(registerIdDTO);

          String postRegisterJSON = ow.writeValueAsString(postRegisterDTO);

          HttpURLConnection connection = (HttpURLConnection) url.openConnection();

          connection.setRequestMethod("POST");
          connection.setRequestProperty("content-type", "application/json");
          connection.setRequestProperty("Accept", "application/json, text/plain, */*");
          connection.setRequestProperty("Accept-Language", "vi-VN,vi;q=0.9,en-US;q=0.8,en;q=0.7");
          connection.setRequestProperty("Connection", "keep-alive");
          connection.setRequestProperty("Authorization", "Bearer " + token);
          connection.setDoOutput(true);


          DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
          outputStream.writeBytes(postRegisterJSON);
          outputStream.flush();
          outputStream.close();


          int responseCode = connection.getResponseCode();

          if(responseCode >=200 && responseCode < 300){
              BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
              String line;
              StringBuilder response = new StringBuilder();
              while ((line = reader.readLine()) != null) {
                  response.append(line);
              }
              reader.close();
              System.out.println("Response Body: " + response.toString());
              return true;

          } else if(responseCode >= 400 && responseCode < 600){

              BufferedReader readerError = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
              String lineError;
              StringBuilder responseError = new StringBuilder();
              while ((lineError = readerError.readLine()) != null) {
                  responseError.append(lineError);
              }
              System.out.println("Response Error: " + responseError.toString());
              readerError.close();
              errorResponseDTO = objectMapper.readValue(responseError.toString(), ErrorResponseDTO.class);
              return false;
          }



          connection.disconnect();
          System.out.println("Response Code: " + responseCode);
          return false;
    }
}
