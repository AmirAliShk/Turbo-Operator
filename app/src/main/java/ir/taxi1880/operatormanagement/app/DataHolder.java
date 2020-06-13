package ir.taxi1880.operatormanagement.app;

public class DataHolder {
  private static DataHolder ourInstance;

  public static DataHolder getInstance() {
    if (ourInstance == null) {
      ourInstance = new DataHolder();
      return ourInstance;
    } else {
      return ourInstance;
    }
  }
}
