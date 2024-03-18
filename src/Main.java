import API.*;
import DTO.*;
import Model.Batch;
import Model.Location;
import Model.Slot;
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

        System.out.println("Nhap dia chi file log: ");
        Scanner scanner = new Scanner(System.in);
        String logPath = scanner.nextLine();

        System.out.println("Nhap dia chi file excel: ");
        String excelPath = scanner.nextLine();

        System.out.println("Nhap thoi gian moi lan gui request (giay): ");
        int time = scanner.nextInt();


        List<AccountRegisterDTO> accountRegisterDTOS = main.getAccountRegister(excelPath);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(15);



        for(AccountRegisterDTO accountRegisterDTO : accountRegisterDTOS){
             executorService.scheduleAtFixedRate(() -> {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(logPath))){
                    LoginAPI loginAPI = new LoginAPI();
                    GetSlotAPI getSlotAPI = new GetSlotAPI();
                    LoginBody loginBody = loginAPI.login(accountRegisterDTO.getUsername(), accountRegisterDTO.getPassword());

                    List<SlotDTO> slotDTOList = getSlotAPI.getSlot(accountRegisterDTO.getLocationId(), loginBody.getToken());
                    if(accountRegisterDTO.getStatus().equals("0")){
                        for (SlotDTO slotDTO: slotDTOList){
                            if(slotDTO.getId().equals(accountRegisterDTO.getSlotId())){

                                if(slotDTO.getRegisteredSlots() < slotDTO.getNumberOfSeats()){
                                    Boolean result = main.normalRegister(accountRegisterDTO.getUsername(), accountRegisterDTO.getPassword(),
                                            accountRegisterDTO.getBatchId(), accountRegisterDTO.getPeriodId(),
                                            accountRegisterDTO.getLocationId(), accountRegisterDTO.getSlotId());
                                    if(result){
                                        writer.write(accountRegisterDTO.getUsername() + " Dang ky thang cong");
                                        writer.newLine();
                                    } else {
                                        System.out.println(accountRegisterDTO.getUsername() + " Dang ky that bai");
                                    }
                                }
                                else{
                                    System.out.println("Slot cua " + accountRegisterDTO.getUsername() + " da day");
                                }
                            }
                        }
                    } else if(accountRegisterDTO.getStatus().equals("1")){
                        Boolean result = randomRegister2(accountRegisterDTO.getUsername(), accountRegisterDTO.getPassword());
                        if(result){
                            writer.write(accountRegisterDTO.getUsername() + " Dang ky thang cong");
                            writer.newLine();
                        } else {
                            System.out.println(accountRegisterDTO.getUsername() + " Dang ky that bai");
                        }
                    } else {
                        ResultDTO resultDTO = randomRegisterByLocation(accountRegisterDTO.getUsername(), accountRegisterDTO.getPassword(),
                                accountRegisterDTO.getLocationId(), accountRegisterDTO.getBatchId(), "35");
                        if(resultDTO.getSuccess()){
                            System.out.println(accountRegisterDTO.getUsername() + " " + resultDTO.getMessage());
                            writer.write(accountRegisterDTO.getUsername() + " Dang ky thang cong");
                            writer.newLine();
                        } else {
                            System.out.println(accountRegisterDTO.getUsername() + " " + resultDTO.getMessage());
                            System.out.println(accountRegisterDTO.getUsername() + " Dang ky that bai");

                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }, 0, time, TimeUnit.SECONDS);
        }
    }

    private List<AccountRegisterDTO> getAccountRegister(String path){

        try (FileInputStream excelFile = new FileInputStream(path)) {
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
                String status = row.getCell(6).getStringCellValue();

                dto.setUsername(username);
                dto.setPassword(password);
                dto.setBatchId(batchId);
                dto.setPeriodId(periodId);
                dto.setLocationId(locationId);
                dto.setSlotId(slotId);
                dto.setStatus(status);
                accountRegisterDTOS.add(dto);
            }

            return accountRegisterDTOS;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    private static Boolean randomRegister2(String username, String password) throws Exception{
        LoginAPI loginAPI = new LoginAPI();
        GetBatchAPI getBatchAPI = new GetBatchAPI();
        GetLocationAPI getLocationAPI = new GetLocationAPI();
        GetSlotAPI getSlotAPI = new GetSlotAPI();
        List<Batch> batchList = new ArrayList<>();
        PostRegisterAPI postRegisterAPI = new PostRegisterAPI();
        Random random = new Random();


        LoginBody loginBody = loginAPI.login(username, password);
        if(loginBody == null){
            System.out.println("Login failed");
            return false;
        }

        List<BatchDTO> batchDTOList = getBatchAPI.getBatch("35", loginBody.getToken());
        if(batchDTOList == null){
            System.out.println("Get batch failed");
            return false;
        }

        for (BatchDTO batchDTO : batchDTOList){
            if(batchDTO == null){
                continue;
            }

            Date currentDate = new Date();
            if(batchDTO.getConfig().getRegistrationBeginDateTime().before(currentDate) &&
                    batchDTO.getConfig().getRegistrationEndDateTime().after(currentDate) &&
                    batchDTO.getStatus().equals("OPENING")){
                Batch batch = new Batch();
                batch.setId(batchDTO.getId());
                batchList.add(batch);
            }
        }
        for (Batch batch: batchList){
            List<Location> locationIdList = new ArrayList<>();
            List<LocationDTO> locationList = getLocationAPI.getLocation(batch.getId(), loginBody.getToken());
            if(locationList == null){
                System.out.println("Get location failed");
                return false;
            }
            for (LocationDTO locationDTO: locationList){
                Location location = new Location();
                location.setId(locationDTO.getId());
                location.setSlotList(new ArrayList<>());
                locationIdList.add(location);
            }
            batch.setLocationList(locationIdList);
        }

        // get slot for each location
        for (Batch batch: batchList){
            for (Location location: batch.getLocationList()){
                List<SlotDTO> slotDTOList = getSlotAPI.getSlot(location.getId(), loginBody.getToken());
                if(slotDTOList == null){
                    System.out.println("Get slot failed");
                    return false;
                }
                for(SlotDTO slotDTO: slotDTOList){
                    if(slotDTO.getRegisteredSlots() < slotDTO.getNumberOfSeats()){
                        Slot slot = new Slot();
                        slot.setId(slotDTO.getId());
                        location.getSlotList().add(slot);
                    }
                }
            }
        }
        for (Batch batch: batchList){
            for (Location location: batch.getLocationList()){
                for (Slot slot: location.getSlotList()){
                    if(slot.getId() != null){
                        return postRegisterAPI.postRegister(loginBody.getToken(), batch.getId(), location.getId(), slot.getId(), "35", loginBody.getAccountInfo().getId());
                    }
                }
            }
        }
        return false;
    }

    private static ResultDTO randomRegisterByLocation(String username, String password, String locationId, String batchId, String periodId) throws Exception{
        LoginAPI loginAPI = new LoginAPI();
        LoginBody loginBody = loginAPI.login(username, password);
        GetSlotAPI getSlotAPI = new GetSlotAPI();
        ResultDTO resultDTO = new ResultDTO();
        SlotDTO slotDTORs = new SlotDTO();

        resultDTO.setSuccess(false);
        resultDTO.setMessage("Khong co slot nao con trong trong location nay");


        if(loginBody == null){
            System.out.println("Login failed");
            resultDTO.setSuccess(false);
            resultDTO.setMessage("Login failed");
            return resultDTO;
        }

        List<SlotDTO> slotDTOList = getSlotAPI.getSlot(locationId, loginBody.getToken());
        for (SlotDTO slotDTO: slotDTOList){
            if(slotDTO.getRegisteredSlots() < slotDTO.getNumberOfSeats()){
               resultDTO.setSuccess(true);
                resultDTO.setMessage("Con cho trong trong location nay");
                slotDTORs = slotDTO;
                break;
            }
        }
        if(resultDTO.getSuccess()){
            PostRegisterAPI postRegisterAPI = new PostRegisterAPI();
            resultDTO.setSuccess(postRegisterAPI.postRegister(loginBody.getToken(), batchId, locationId, slotDTORs.getId(), periodId, loginBody.getAccountInfo().getId()));
            return resultDTO;
        }
        return resultDTO;
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