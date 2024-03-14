package DTO;

public class ConfigDTO {
    Long fee;
    Long limitedNoRegistrations;
    Long dateBetweenExams;

    PaymentSessionDurationDTO paymentSessionDuration;

    public Long getFee() {
        return fee;
    }

    public void setFee(Long fee) {
        this.fee = fee;
    }

    public Long getLimitedNoRegistrations() {
        return limitedNoRegistrations;
    }

    public void setLimitedNoRegistrations(Long limitedNoRegistrations) {
        this.limitedNoRegistrations = limitedNoRegistrations;
    }

    public Long getDateBetweenExams() {
        return dateBetweenExams;
    }

    public void setDateBetweenExams(Long dateBetweenExams) {
        this.dateBetweenExams = dateBetweenExams;
    }

    public PaymentSessionDurationDTO getPaymentSessionDuration() {
        return paymentSessionDuration;
    }

    public void setPaymentSessionDuration(PaymentSessionDurationDTO paymentSessionDuration) {
        this.paymentSessionDuration = paymentSessionDuration;
    }
}
