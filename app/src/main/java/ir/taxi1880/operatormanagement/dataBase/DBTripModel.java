package ir.taxi1880.operatormanagement.dataBase;

public class DBTripModel {
  int id;// the unique id for each trip
  int operatorId;// ID of the person who registered the service
  String originText;
  String saveDate;
  String sendDate;
  int originStation;
  int city;
  String tell;
  String mobile;
  String customerName;
  String voipId;

  public int getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(int operatorId) {
    this.operatorId = operatorId;
  }

  public String getTell() {
    return tell;
  }

  public void setTell(String tell) {
    this.tell = tell;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getVoipId() {
    return voipId;
  }

  public void setVoipId(String voipId) {
    this.voipId = voipId;
  }

  public int getCity() {
    return city;
  }

  public void setCity(int city) {
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
