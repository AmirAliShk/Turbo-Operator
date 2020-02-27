package ir.taxi1880.operatormanagement.model;

public class LastAddressModel {

 private String station;
 private String address;
 private String phoneNumbr;
 private String status;

  public String getStation() {
    return station;
  }

  public void setStation(String code) {
    this.station = code;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getPhoneNumbr() {
    return phoneNumbr;
  }

  public void setPhoneNumbr(String phoneNumbr) {
    this.phoneNumbr = phoneNumbr;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
