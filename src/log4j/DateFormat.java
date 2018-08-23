package log4j;



/**
 *
 * <p>Title: Sms Prepaid Roaming solution</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: Elcom - JSC</p>
 * @author HungDM - Mobile ONE group
 * @version 1.0
 */


import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Hashtable;

public class DateFormat {
  public static final String FMT_TIME24_DEFAULT = "yyyyMMdd HH:mm:ss";

  static Date today = null;
  static Hashtable mapDateFormat = new Hashtable();
  public DateFormat() {
  }

  /**
   * This methead create new dateformat object if format is new
   * or find suiltable dateformat object to minimize memroy in use.
   * @param format String
   * @return String
   */
  protected static synchronized String easyDateFormat (String format) {

    SimpleDateFormat df;
    String result = "";
    boolean exist = mapDateFormat.containsKey(format);
    if(exist == false){
      try {
        df = new SimpleDateFormat(format);
      }
      catch (Exception ex) {
        df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss ");
      }
//      if (GeneralDef.platform == GeneralDef.PLATFORM_SOLARIS) {
//        if (GlobalVariables.timezone.indexOf("-") >= 0)
//          df.setTimeZone(TimeZone.getTimeZone("GMT-07:00"));
//        else
//          df.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));
//      }
//      else {
//        df.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));
//      }
      mapDateFormat.put(format,df);
    }else{
      //neu da dang ky format dang nay roi thi tim lai la OK
      df = (SimpleDateFormat) mapDateFormat.get(format);
    }

    today = new Date();
    result = df.format(today);
    today  = null;
    df = null;
    return result;
  }

  /**
   * Get date string with specified format and Date object
   * @param format String
   * @param date Date
   * @return String
   */
  protected static synchronized String easyDateFormat (String format,Date date) {
    if(date == null) return "";

    SimpleDateFormat df;
    String result = "";
    boolean exist = mapDateFormat.containsKey(format);
    if (exist == false) {
      try {
        df = new SimpleDateFormat(format);
      }
      catch (Exception ex) {
        df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss ");
      }
//      if (GeneralDef.platform == GeneralDef.PLATFORM_SOLARIS) {
//        if (GlobalVariables.timezone.indexOf("-") >= 0)
//          df.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));
//        else
//          df.setTimeZone(TimeZone.getTimeZone("GMT-07:00"));
//      }
//      else {
//        df.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));
//      }
      mapDateFormat.put(format, df);
    }
    else {
      //neu da dang ky format dang nay roi thi tim lai la OK
      df = (SimpleDateFormat) mapDateFormat.get(format);
    }

    result = df.format(date);
    date  = null;
    df  = null;
    return result;
  }

  /**
   *
   * @return String
   */
  public static synchronized String formatFileName(){
    return easyDateFormat("dd_MM_yyyy HH_mm_ss");
  }

  /**
   * return ddMMYYYY
   * @return String
   */
  public static synchronized String formatFolderName(){
    return easyDateFormat("ddMMyyyy");
  }

  /**
   * return yyyyMMdd HH:mm:ss
   * @return String
   */
  public static synchronized String formatTime24(){
    return easyDateFormat(FMT_TIME24_DEFAULT);
  }

  /**
   * return yyyyMMdd HH:mm:ss with specified Date object
   * @param date Date
   * @return String
   */
  public static synchronized String formatTime24(Date date){
    return easyDateFormat("yyyyMMdd HH:mm:ss",date);
  }

  /**
   * return yyMMddHHmm
   * @param date Date
   * @return String
   */
  public static synchronized String formatReportDate(Date date){
    if(date == null)
      return formatReportDate();
    return easyDateFormat("yyMMddHHmm", date);
  }


  /**
   * return yyMMddHHmm
   * @return String
   */
  public static synchronized String formatReportDate(){
    return easyDateFormat("yyMMddHHmm");
  }

  public static void main(String arg[]){
  }
}
