package DTO;

import java.util.Date;

public class ConfigBatchDTO {
    Date endDate;
    Date startDate;
    Date registrationBeginDateTime;
    Date  registrationEndDateTime;
    Boolean autoOpenClose;

    public Boolean getAutoOpenClose() {
        return autoOpenClose;
    }

    public void setAutoOpenClose(Boolean autoOpenClose) {
        this.autoOpenClose = autoOpenClose;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getRegistrationBeginDateTime() {
        return registrationBeginDateTime;
    }

    public void setRegistrationBeginDateTime(Date registrationBeginDateTime) {
        this.registrationBeginDateTime = registrationBeginDateTime;
    }

    public Date getRegistrationEndDateTime() {
        return registrationEndDateTime;
    }

    public void setRegistrationEndDateTime(Date registrationEndDateTime) {
        this.registrationEndDateTime = registrationEndDateTime;
    }
}
