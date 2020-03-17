package ir.taxi1880.operatormanagement.model;

public class CallModel {
  String type;
  int exten;
  String participant;
  String queue;
  String voipId;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getExten() {
    return exten;
  }

  public void setExten(int exten) {
    this.exten = exten;
  }

  public String getParticipant() {
    return participant;
  }

  public void setParticipant(String participant) {
    this.participant = participant;
  }

  public String getQueue() {
    return queue;
  }

  public void setQueue(String queue) {
    this.queue = queue;
  }

  public String getVoipId() {
    return voipId;
  }

  public void setVoipId(String voipId) {
    this.voipId = voipId;
  }
}
