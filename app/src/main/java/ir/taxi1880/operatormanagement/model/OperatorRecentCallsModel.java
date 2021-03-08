package ir.taxi1880.operatormanagement.model;

public class OperatorRecentCallsModel {

    String txtDate;
    String txtTime;
    String txtDuration;
    String txtPassengerTell;

    public OperatorRecentCallsModel(String txtDate, String txtTime, String txtPassengerTell, String txtDuration) {
        this.txtDate = txtDate;
        this.txtTime = txtTime;
        this.txtDuration = txtDuration;
        this.txtPassengerTell = txtPassengerTell;
    }

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

    public String getTxtDuration() {
        return txtDuration;
    }

    public void setTxtDuration(String txtDuration) {
        this.txtDuration = txtDuration;
    }

    public String getTxtPassengerTell() {
        return txtPassengerTell;
    }

    public void setTxtPassengerTell(String txtPassengerTell) {
        this.txtPassengerTell = txtPassengerTell;
    }
}
