package DTO;

import java.util.List;

public class AccountInfo {
    String id;
    String email;
    Long version;
    Boolean banned;
    List<Object> status;
    Boolean useEmailOtp;

    public AccountInfo(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }

    public List<Object> getStatus() {
        return status;
    }

    public void setStatus(List<Object> status) {
        this.status = status;
    }

    public Boolean getUseEmailOtp() {
        return useEmailOtp;
    }

    public void setUseEmailOtp(Boolean useEmailOtp) {
        this.useEmailOtp = useEmailOtp;
    }
}
