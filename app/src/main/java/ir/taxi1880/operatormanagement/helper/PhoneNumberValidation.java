package ir.taxi1880.operatormanagement.helper;

/**
 * Created by mohsen on 16/10/2016.
 */
public class PhoneNumberValidation {

  public static boolean isValid(String mobile) {
    if (mobile == null) return false;
    if (mobile.isEmpty()) return false;
//    if (mobile.length()<10) return false;
    if (mobile.substring(0, 1).equals("0")) {
      mobile = mobile.substring(1);
    }
      String regEx = "9[0-9]{9}$";
    return mobile.matches(regEx);
  }

  public static String addZeroFirst(String mobile) {
    if (mobile.substring(0, 1).equals("0")) {
      return mobile;
    } else {
      return "0" + mobile;
    }
  }

}
