package ir.taxi1880.operatormanagement.model;

public class ComplaintDetailsModel {
    private String saveDate;
    private String saveTime;
    private int complaintId;
    private String customerName;
    private String complaintType;
    private int status;
    private int price;
    private String serviceDate;
    private String customerPhoneNumber;
    private String customerMobileNumber;
    private String driverName;
    private String driverLastName;
    private String driverMobile;
    private String driverMobile2;
    private String address;
    private int serviceId;
    private int taxicode;
    private String serviceVoipId;
    private String complaintVoipId;
    int cityCode;
    int CarClass;
    int countCallCustomer;

    public int getCountCallCustomer() {
        return countCallCustomer;
    }

    public void setCountCallCustomer(int countCallCustomer) {
        this.countCallCustomer = countCallCustomer;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getCarClass() {
        return CarClass;
    }

    public void setCarClass(int carClass) {
        CarClass = carClass;
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

    public int getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(int complaintId) {
        this.complaintId = complaintId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getComplaintType() {
        return complaintType;
    }

    public void setComplaintType(String complaintType) {
        this.complaintType = complaintType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(String serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getCustomerMobileNumber() {
        return customerMobileNumber;
    }

    public void setCustomerMobileNumber(String customerMobileNumber) {
        this.customerMobileNumber = customerMobileNumber;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverLastName() {
        return driverLastName;
    }

    public void setDriverLastName(String driverLastName) {
        this.driverLastName = driverLastName;
    }

    public String getDriverMobile() {
        return driverMobile;
    }

    public void setDriverMobile(String driverMobile) {
        this.driverMobile = driverMobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getDriverMobile2() {
        return driverMobile2;
    }

    public void setDriverMobile2(String driverMobile2) {
        this.driverMobile2 = driverMobile2;
    }

    public int getTaxicode() {
        return taxicode;
    }

    public void setTaxicode(int taxicode) {
        this.taxicode = taxicode;
    }

    public String getServiceVoipId() {
        return serviceVoipId;
    }

    public void setServiceVoipId(String serviceVoipId) {
        this.serviceVoipId = serviceVoipId;
    }

    public String getComplaintVoipId() {
        return complaintVoipId;
    }

    public void setComplaintVoipId(String complaintVoipId) {
        this.complaintVoipId = complaintVoipId;
    }
}
