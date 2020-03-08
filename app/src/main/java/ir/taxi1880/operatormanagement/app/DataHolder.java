package ir.taxi1880.operatormanagement.app;

public class DataHolder {
  private static DataHolder ourInstance;

  private String name;
  private String cityCode;
  private String mobile;

  public void setHire(String name, String cityCode, String mobile) {
    ourInstance.name = name;
    ourInstance.cityCode = cityCode;
    ourInstance.mobile = mobile;
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
