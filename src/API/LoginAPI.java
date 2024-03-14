package API;

import DTO.LoginBody;
import DTO.LoginDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class LoginAPI {
    final  String LOGIN_URL = "https://api.hsa.edu.vn/accounts/sign-in";
    public LoginBody login(String username, String password) {
        System.out.println("Logging in with username: " + username + " and password: " + password);

        try {
            URL url = new URL(LOGIN_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            ObjectMapper objectMapper = new ObjectMapper();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            LoginDTO loginDTO = new LoginDTO();
            loginDTO.setEmail(username);
            loginDTO.setPassword(password);


            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

            outputStream.writeBytes(loginDTO.toJSON());
            outputStream.flush();
            outputStream.close();

            // Lấy mã trạng thái HTTP của phản hồi
            int responseCode = connection.getResponseCode();

            // Đọc phản hồi từ server
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // In kết quả phản hồi
            System.out.println("Response Code: " + responseCode);
            System.out.println("Response Body: " + response.toString());

            LoginBody loginBody = objectMapper.readValue(response.toString(), LoginBody.class);

            // Đóng kết nối
            connection.disconnect();

            return loginBody;

        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
            return null;
        }

    }
}
