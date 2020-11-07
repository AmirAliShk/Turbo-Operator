package ir.taxi1880.operatormanagement.model;

public class TripModel {
  String callTime;
  String sendTime;
  String customerName;
  String customerTell;
  String customerMob;
  String address;
  String carType;
  String driverMobile;
  String city;

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
