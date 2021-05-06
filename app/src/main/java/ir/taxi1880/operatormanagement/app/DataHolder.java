package ir.taxi1880.operatormanagement.app;

public class DataHolder {
    private static DataHolder ourInstance;
    public String pushType = null;
    byte complaintResult = 0;
    boolean lockDriver = false;
    String lockDay = "0";
    boolean unlockDriver = false;
    boolean fined = false;
    boolean customerLock = false;
    boolean outDriver = false;

    public String getLockDay() {
        return lockDay;
    }

    public void setLockDay(String lockDay) {
        this.lockDay = lockDay;
    }

    public boolean isUnlockDriver() {
        return unlockDriver;
    }

    public void setUnlockDriver(boolean unlockDriver) {
        this.unlockDriver = unlockDriver;
    }

    public boolean isFined() {
        return fined;
    }

    public void setFined(boolean fined) {
        this.fined = fined;
    }

    public boolean isCustomerLock() {
        return customerLock;
    }

    public void setCustomerLock(boolean customerLock) {
        this.customerLock = customerLock;
    }

    public boolean isOutDriver() {
        return outDriver;
    }

    public void setOutDriver(boolean outDriver) {
        this.outDriver = outDriver;
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
