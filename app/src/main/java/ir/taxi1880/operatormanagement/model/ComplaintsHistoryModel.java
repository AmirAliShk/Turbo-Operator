package ir.taxi1880.operatormanagement.model;

public class ComplaintsHistoryModel {

    int countCulprit;
    int countComplaint;
    String saveDate;
    String saveTime;
    String voipId;
    String complaintType;
    int serviceId;
    int customerId;
    int complaintId;
    String typeResultDes;
    int statusDes;
    String customerName;

    public String getTypeResultDes() {
        return typeResultDes;
    }

    public void setTypeResultDes(String typeResultDes) {
        this.typeResultDes = typeResultDes;
    }

    public int getCountCulprit() {
        return countCulprit;
    }

    public void setCountCulprit(int countCulprit) {
        this.countCulprit = countCulprit;
    }

    public int getCountComplaint() {
        return countComplaint;
    }

    public void setCountComplaint(int countComplaint) {
        this.countComplaint = countComplaint;
    }

    public String getSaveDate() {
        return saveDate;
    }

    public void setSaveDate(String saveDate) {
        this.saveDate = saveDate;
    }

    public String getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(String saveTime) {
        this.saveTime = saveTime;
    }

    public String getVoipId() {
        return voipId;
    }

    public void setVoipId(String voipId) {
        this.voipId = voipId;
    }

    public String getComplaintType() {
        return complaintType;
    }

    public void setComplaintType(String complaintType) {
        this.complaintType = complaintType;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(int complaintId) {
        this.complaintId = complaintId;
    }

    public int getStatusDes() {
        return statusDes;
    }

    public void setStatusDes(int statusDes) {
        this.statusDes = statusDes;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
