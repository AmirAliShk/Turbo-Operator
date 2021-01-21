package ir.taxi1880.operatormanagement.model;

public class PassengerAddressModel {
  private String phoneNumber;
  private String mobile;
  private String address;
  private int station;
  private int status;

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public int getStation() {
    return station;
  }

  public void setStation(int station) {
    this.station = station;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }
}
