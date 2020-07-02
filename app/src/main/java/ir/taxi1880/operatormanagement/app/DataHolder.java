package ir.taxi1880.operatormanagement.app;

public class DataHolder {
  private static DataHolder ourInstance;
  public String pushType = null;

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
