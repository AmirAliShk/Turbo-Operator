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

  public static String removePrefix(String mobileNumber){

    if (mobileNumber.startsWith("0")){
      mobileNumber.substring(1,10);
    }else if (mobileNumber.startsWith("+98")||mobileNumber.startsWith("098")){
      mobileNumber.substring(3,12);
    }else if (mobileNumber.startsWith("0098")){
      mobileNumber.substring(4,13);
    }else if (mobileNumber.startsWith("00")||mobileNumber.startsWith("98")){
      mobileNumber.substring(2,11);
    }

    return mobileNumber;
  }

  public static String addZeroFirst(String mobile) {
    if (mobile.substring(0, 1).equals("0")) {
      return mobile;
    } else {
      return "0" + mobile;
    }
  }

}
