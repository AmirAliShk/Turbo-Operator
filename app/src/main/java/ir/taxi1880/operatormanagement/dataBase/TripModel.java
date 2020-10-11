package ir.taxi1880.operatormanagement.dataBase;

public class TripModel {
  int id;
  String originText;
  String destinationText;
  int originStation;
  int destinationStation;
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

  public String getDestinationText() {
    return destinationText;
  }

  public void setDestinationText(String destinationText) {
    this.destinationText = destinationText;
  }

  public int getOriginStation() {
    return originStation;
  }

  public void setOriginStation(int originStation) {
    this.originStation = originStation;
  }

  public int getDestinationStation() {
    return destinationStation;
  }

  public void setDestinationStation(int destinationStation) {
    this.destinationStation = destinationStation;
  }
}
