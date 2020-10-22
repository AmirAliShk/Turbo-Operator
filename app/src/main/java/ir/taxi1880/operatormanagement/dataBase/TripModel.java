package ir.taxi1880.operatormanagement.dataBase;

public class TripModel {
  int id;
  String originText;
  String saveDate;
  String sendDate;
  int originStation;
  String city;

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getOriginText() {
    return originText;
  }

  public void setOriginText(String originText) {
    this.originText = originText;
  }

  public int getOriginStation() {
    return originStation;
  }

  public void setOriginStation(int originStation) {
    this.originStation = originStation;
  }

  public String getSaveDate() {
    return saveDate;
  }

  public void setSaveDate(String saveDate) {
    this.saveDate = saveDate;
  }

  public String getSendDate() {
    return sendDate;
  }

  public void setSendDate(String sendDate) {
    this.sendDate = sendDate;
  }
}
