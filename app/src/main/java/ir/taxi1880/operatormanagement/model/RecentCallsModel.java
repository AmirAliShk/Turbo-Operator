package ir.taxi1880.operatormanagement.model;

public class RecentCallsModel {

    String txtDate;
    String txtTime;
    String voipId;
    String phone;
    String destinationOperator;
    int txtTimeRemaining;

    public String getDestinationOperator() {
        return destinationOperator;
    }

    public void setDestinationOperator(String destinationOperator) {
        this.destinationOperator = destinationOperator;
    }

    public String getTxtDate() {
        return txtDate;
    }

    public void setTxtDate(String txtDate) {
        this.txtDate = txtDate;
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
