import API.*;
import DTO.AccountRegisterDTO;
import DTO.BatchDTO;
import DTO.LocationDTO;
import DTO.LoginBody;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Main {


    public static void main(String[] args) throws Exception {
        Main main = new Main();
        List<AccountRegisterDTO> accountRegisterDTOS = main.getAccountRegister();
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);

        for(AccountRegisterDTO accountRegisterDTO : accountRegisterDTOS){
            executorService.scheduleAtFixedRate(() -> {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("code.txt"))){
                    Boolean result = main.normalRegister(accountRegisterDTO.getUsername(), accountRegisterDTO.getPassword(),
                            accountRegisterDTO.getBatchId(), accountRegisterDTO.getPeriodId(),
                            accountRegisterDTO.getLocationId(), accountRegisterDTO.getSlotId());
                    if(result){
                        writer.write(accountRegisterDTO.getUsername() + "Đăng ký thành công");
                        writer.newLine();
                        executorService.shutdown();
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }, 0, 2, TimeUnit.SECONDS);
        }
    }


    private List<AccountRegisterDTO> getAccountRegister(){

        try (FileInputStream excelFile = new FileInputStream("C:\\Users\\MyPC\\OneDrive\\Desktop\\HSA_REGISTER\\src\\Excel_info.xlsx")) {
            XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
            XSSFSheet sheet = workbook.getSheetAt(0);

            List<AccountRegisterDTO> accountRegisterDTOS = new ArrayList<>();
            for(int i = 1; i<= sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);
                AccountRegisterDTO dto = new AccountRegisterDTO();

                String username = row.getCell(0).getStringCellValue();
                String password = row.getCell(1).getStringCellValue();
                String periodId = row.getCell(2).getStringCellValue();
                String batchId = row.getCell(3).getStringCellValue();
                String locationId = row.getCell(4).getStringCellValue();
                String slotId = row.getCell(5).getStringCellValue();

                dto.setUsername(username);
                dto.setPassword(password);
                dto.setBatchId(batchId);
                dto.setPeriodId(periodId);
                dto.setLocationId(locationId);
                dto.setSlotId(slotId);
                accountRegisterDTOS.add(dto);
            }

            return accountRegisterDTOS;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
    private static void randomRegister(String username, String password){
        LoginAPI loginAPI = new LoginAPI();
        GetBatchAPI getBatchAPI = new GetBatchAPI();
        List<String> currentBatchId = new ArrayList<>();
        GetLocationAPI getLocationAPI = new GetLocationAPI();
        HashMap<String, List<String>> batchContainLocation = new HashMap<>();
        HashMap<String, List<HashMap<String, List<String>>>> batchContainLocationSlot = new HashMap<>();

        LoginBody loginBody = loginAPI.login(username, password);
        if(loginBody == null){
            System.out.println("Login failed");
            return;
        }

        List<BatchDTO> batchDTOList = getBatchAPI.getBatch("35", loginBody.getToken());
        if(batchDTOList == null){
            System.out.println("Get batch failed");
            return;
        }

        for (BatchDTO batchDTO : batchDTOList){
            if(batchDTO == null){
                continue;
            }
            Date currentDate = new Date();
            if(batchDTO.getConfig().getRegistrationBeginDateTime().before(currentDate) &&
                    batchDTO.getConfig().getRegistrationEndDateTime().after(currentDate) &&
            batchDTO.getStatus().equals("OPENING")){
                currentBatchId.add(batchDTO.getId());
            }
        }
        for (String batchId: currentBatchId){
            List<String> locationIdList = new ArrayList<>();
            List<LocationDTO> locationList = getLocationAPI.getLocation(batchId, loginBody.getToken());
            if(locationList == null){
                System.out.println("Get location failed");
                return;
            }
            for (LocationDTO locationDTO: locationList){
                locationIdList.add(locationDTO.getId());
            }
            batchContainLocation.put(batchId, locationIdList);
        }







    }

    private Boolean normalRegister(String username, String password,
                                   String batchId, String periodId,
                                   String locationId, String slotId) throws Exception{
        LoginAPI loginAPI = new LoginAPI();
        LoginBody loginBody = loginAPI.login(username, password);

        PostRegisterAPI postRegisterAPI = new PostRegisterAPI();

        String token = loginBody.getToken();

        if(loginBody == null){
            System.out.println("Login failed");
            return false;
        }

        return postRegisterAPI.postRegister(token, batchId, locationId, slotId, periodId, loginBody.getAccountInfo().getId());
    }


}