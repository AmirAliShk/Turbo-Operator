package ir.taxi1880.operatormanagement.model;

public class CityModel {
  private int id;
  private String city;
  private String cityLatin;

  public String getCityLatin() {
    return cityLatin;
  }

  public void setCityLatin(String cityLatin) {
    this.cityLatin = cityLatin;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }
}
