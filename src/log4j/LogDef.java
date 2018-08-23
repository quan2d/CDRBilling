package log4j;




/**
 * This class is a struct that store all configuration parameter
 */

public class LogDef {

  //Define all condition to change log-file
  public static class FILE{
    public static final byte CHANGE_ON_SZIE = 1;
    public static final byte CHANGE_ON_INDEX = 2;
    public static final byte CHANGE_ON_DATE = 3;
    public static final int MAX_FILE_SIZE           = 1024 * 1024 * 10; //10 Mb
    public static int MAX_FILE_BACKUP_INDEX = 1000;//Moi ngay chi luu 1000 file thoi.

  }
  /**

   * Casc log level duoc dat theo thu tu nhu sau

   * ALL < DEBUG < INFO < WARN < ERROR < FATAL < OFF

   * Mac dinh he thong se la log_all

   * Chi nhung logstring co level cao hon hoac bang log_lelvel cua he thong

   * thi moi duoc log ra file hoac syslog.

   */

  public static class LOG_LEVEL {

    public static final byte LOG_ALL = 0; // log all

    public static final byte LOG_DEBUG = 1;

    public static final byte LOG_INFO = 2;

    public static final byte LOG_WARNING = 3;

    public static final byte LOG_ERROR = 4;

    public static final byte LOG_FATAL = 5;

    public static final byte LOG_OFF = 6;

  }

  public static class SYSLOG{
    public static final int DFLT_PORT = 513;
  }

}
