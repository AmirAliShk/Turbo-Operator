package ir.taxi1880.operatormanagement.model;

public class PassengerCallsModel {

    String txtDate;
    String txtTime;
    String voipId;
    String phone;
    int txtTimeRemaining;


    public String getTxtDate() {
        return txtDate;
    }

    public void setTxtDate(String txtDate) {
        this.txtDate = txtDate;
    }

    public String getTxtTime() {
        return txtTime;
    }

    public void setTxtTime(String txtTime) {
        this.txtTime = txtTime;
    }

    public int getTxtTimeRemaining() {
        return txtTimeRemaining;
    }

    public void setTxtTimeRemaining(int txtTimeRemaining) {
        this.txtTimeRemaining = txtTimeRemaining;
    }

    public String getVoipId() {
        return voipId;
    }

    public void setVoipId(String voipId) {
        this.voipId = voipId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
