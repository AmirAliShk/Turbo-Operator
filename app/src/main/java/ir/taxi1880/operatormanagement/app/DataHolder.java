package ir.taxi1880.operatormanagement.app;

public class DataHolder {
  private static DataHolder ourInstance;

  private boolean isEndCall = false;
  private boolean isConnectedCall = false;
  private boolean activeMainActivity = false;

  public boolean isEndCall() {
    return ourInstance.isEndCall;
  }

  public void setEndCall(boolean receivedCall) {
    ourInstance.isEndCall = receivedCall;
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
