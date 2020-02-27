package ir.taxi1880.operatormanagement.model;

public class StationModel {

 private String name;
 private String code;
 private String address;
 private int countrySide;

  public int getCountrySide() {
    return countrySide;
  }

  public void setCountrySide(int countrySide) {
    this.countrySide = countrySide;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }
}
