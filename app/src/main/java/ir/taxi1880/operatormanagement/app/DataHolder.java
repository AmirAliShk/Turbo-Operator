package ir.taxi1880.operatormanagement.app;

public class DataHolder {
    private static DataHolder ourInstance;
    public String pushType = null;
    byte complaintResult = 0;
    boolean lockDriver = false;
    String lockDay = "";
    boolean unlockDriver = false;
    boolean fined = false;
    boolean customerLock = false;
    boolean outDriver = false;
    String voipId = "0";

    public String getVoipId() {
        return ourInstance.voipId;
    }

    public void setVoipId(String voipId) {
        ourInstance.voipId = voipId;
    }

    public String getLockDay() {
        return ourInstance.lockDay;
    }

    public void setLockDay(String lockDay) {
        ourInstance.lockDay = lockDay;
    }

    public boolean isUnlockDriver() {
        return ourInstance.unlockDriver;
    }

    public void setUnlockDriver(boolean unlockDriver) {
        ourInstance.unlockDriver = unlockDriver;
    }

    public boolean isFined() {
        return ourInstance.fined;
    }

    public void setFined(boolean fined) {
        ourInstance.fined = fined;
    }

    public boolean isCustomerLock() {
        return ourInstance.customerLock;
    }

    public void setCustomerLock(boolean customerLock) {
        ourInstance.customerLock = customerLock;
    }

    public boolean isOutDriver() {
        return ourInstance.outDriver;
    }

    public void setOutDriver(boolean outDriver) {
        ourInstance.outDriver = outDriver;
    }

    public boolean isLockDriver() {
        return ourInstance.lockDriver;
    }

    public void setLockDriver(boolean lockDriver) {
        ourInstance.lockDriver = lockDriver;
    }

    public byte getComplaintResult() {
        return ourInstance.complaintResult;
    }

    public void setComplaintResult(byte complaintResult) {
        ourInstance.complaintResult = complaintResult;
    }

    public String getPushType() {
        return ourInstance.pushType;
    }

    public void setPushType(String pushType) {
        ourInstance.pushType = pushType;
    }

    public static DataHolder getInstance() {
        if (ourInstance == null) {
            ourInstance = new DataHolder();
            return ourInstance;
        } else {
            return ourInstance;
        }
    }
}
