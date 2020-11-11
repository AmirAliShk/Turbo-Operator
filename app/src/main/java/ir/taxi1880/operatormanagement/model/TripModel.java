package ir.taxi1880.operatormanagement.model;

public class TripModel {
  String serviceId;
  String callTime;
  String callDate;
  String sendTime;
  String sendDate;
  String customerName;
  String customerTell;
  String customerMob;
  String address;
  String carType;
  String driverMobile;
  String city;
  int finished;
  int stationCode;
  int status;

  public int getFinished() {
    return finished;
  }

  public void setFinished(int finished) {
    this.finished = finished;
  }

  public String getCallDate() {
    return callDate;
  }

  public void setCallDate(String callDate) {
    this.callDate = callDate;
  }

  public String getSendDate() {
    return sendDate;
  }

  public void setSendDate(String sendDate) {
    this.sendDate = sendDate;
  }

  public int getStationCode() {
    return stationCode;
  }

  public void setStationCode(int stationCode) {
    this.stationCode = stationCode;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getServiceId() {
    return serviceId;
  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCallTime() {
    return callTime;
  }

  public void setCallTime(String callTime) {
    this.callTime = callTime;
  }

  public String getSendTime() {
    return sendTime;
  }

  public void setSendTime(String sendTime) {
    this.sendTime = sendTime;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getCustomerTell() {
    return customerTell;
  }

  public void setCustomerTell(String customerTell) {
    this.customerTell = customerTell;
  }

  public String getCustomerMob() {
    return customerMob;
  }

  public void setCustomerMob(String customerMob) {
    this.customerMob = customerMob;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getCarType() {
    return carType;
  }

  public void setCarType(String carType) {
    this.carType = carType;
  }

  public String getDriverMobile() {
    return driverMobile;
  }

  public void setDriverMobile(String driverMobile) {
    this.driverMobile = driverMobile;
  }
}
