package ir.taxi1880.operatormanagement.model;

public class StationInfoModel {

  private String street;
  private String odd;
  private String even;
  private String stationName;
  private int cityCode;
  private int countrySide;
  private int stcode;

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getOdd() {
    return odd;
  }

  public void setOdd(String odd) {
    this.odd = odd;
  }

  public String getEven() {
    return even;
  }

  public void setEven(String even) {
    this.even = even;
  }

  public String getStationName() {
    return stationName;
  }

  public void setStationName(String stationName) {
    this.stationName = stationName;
  }

  public int getCityCode() {
    return cityCode;
  }

  public void setCityCode(int cityCode) {
    this.cityCode = cityCode;
  }

  public int getCountrySide() {
    return countrySide;
  }

  public void setCountrySide(int countrySide) {
    this.countrySide = countrySide;
  }

  public int getStcode() {
    return stcode;
  }

  public void setStcode(int stcode) {
    this.stcode = stcode;
  }
}
