package ir.taxi1880.operatormanagement.model;

public class AllComplaintModel {
    private int id;
    private int serviceCode;
    private int userCode;
    private String date;
    private String time;
    private String description;
    private String tell;
    private int userCodeContact;
    private int typeResult;
    private int inspectorUser;
    private String address;
    private String customerName;
    private String conDate;
    private String conTime;
    private String sendTime;
    private String VoipId;
    private String result;
    private boolean ischeck;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(int serviceCode) {
        this.serviceCode = serviceCode;
    }

    public int getUserCode() {
        return userCode;
    }

    public void setUserCode(int userCode) {
        this.userCode = userCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTell() {
        return tell;
    }

    public void setTell(String tell) {
        this.tell = tell;
    }

    public int getUserCodeContact() {
        return userCodeContact;
    }

    public void setUserCodeContact(int userCodeContact) {
        this.userCodeContact = userCodeContact;
    }

    public int getTypeResult() {
        return typeResult;
    }

    public void setTypeResult(int typeResult) {
        this.typeResult = typeResult;
    }

    public int getInspectorUser() {
        return inspectorUser;
    }

    public void setInspectorUser(int inspectorUser) {
        this.inspectorUser = inspectorUser;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getConDate() {
        return conDate;
    }

    public void setConDate(String conDate) {
        this.conDate = conDate;
    }

    public String getConTime() {
        return conTime;
    }

    public void setConTime(String conTime) {
        this.conTime = conTime;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getVoipId() {
        return VoipId;
    }

    public void setVoipId(String voipId) {
        VoipId = voipId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isIscheck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
