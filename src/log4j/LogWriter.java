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

import org.apache.log4j.Logger;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.net.SyslogAppender;
/**
 *  Lop nay tap trung mot doi tuong log va cac thuoc tinh lien quan
 *   Mot doi tuong log co the log ra nhieu noi
 *   Moi noi se duoc dai dien boi mot logAppender
 *    hien tai ho tro 3 log appender : file, console, unix.
 *
 */

//import com.elcom.utils.GeneralDef;
import java.io.File;

public class LogWriter {

  boolean bLogToFile = true;
  boolean bLogToConsole = false;
  boolean bLogToSysLog = true;

  //File log properties
  public String csLogPath = null;
  public String csModuleName = null;
  int nChangeLogFlag = LogDef.FILE.CHANGE_ON_DATE;
  protected int nMaxFileSize = LogDef.FILE.MAX_FILE_SIZE;
  protected int nMaxFileIndex = LogDef.FILE.MAX_FILE_BACKUP_INDEX;
  protected boolean bFlushImmediately = true;
  static byte nGlobalSystemLogLevel = LogDef.LOG_LEVEL.LOG_ALL;
  static boolean bConfigured = false;
  public Logger loggerObj = null;
  public Appender fileAppender = null;
  public Appender consoleAppender = null;

  protected static String csDateTime = null;
//  public Appender syslogAppender = null;
  Appender arrSyslogAppender[] = null;
  private String loggerName = "";
  private String syslogHost = "";
  private int nSyslogPort = LogDef.SYSLOG.DFLT_PORT;

  public LogWriter(String loggerName, byte level) {
    createLogger(loggerName, level);
  }

  protected void createLogger(String loggerName, byte level) {

    if (loggerObj == null) {
      loggerObj = Logger.getLogger(loggerName);
      setLogLevel(level);
      this.loggerName = loggerName;
    }

  }

  public void setLogLevel(byte level) {
    if (loggerObj == null)return;
    switch (level) {
      case LogDef.LOG_LEVEL.LOG_ALL:
        loggerObj.setLevel(Level.ALL);
        break;
      case LogDef.LOG_LEVEL.LOG_DEBUG:
        loggerObj.setLevel(Level.DEBUG);
        break;
      case LogDef.LOG_LEVEL.LOG_INFO:
        loggerObj.setLevel(Level.INFO);
        break;
      case LogDef.LOG_LEVEL.LOG_WARNING:
        loggerObj.setLevel(Level.WARN);
        break;
      case LogDef.LOG_LEVEL.LOG_ERROR:
        loggerObj.setLevel(Level.ERROR);
        break;
      case LogDef.LOG_LEVEL.LOG_FATAL:
        loggerObj.setLevel(Level.FATAL);
        break;
      case LogDef.LOG_LEVEL.LOG_OFF:
        loggerObj.setLevel(Level.OFF);
        break;
      default:
        loggerObj.setLevel(Level.ERROR);
        break;
    }
  }

  protected static Level getLevelObject(byte level) {

    Level levelObj = null;
    switch (level) {
      case LogDef.LOG_LEVEL.LOG_ALL:
        levelObj = Level.ALL;
        break;
      case LogDef.LOG_LEVEL.LOG_DEBUG:
        levelObj = Level.DEBUG;
        break;
      case LogDef.LOG_LEVEL.LOG_INFO:
        levelObj = Level.INFO;
        break;
      case LogDef.LOG_LEVEL.LOG_WARNING:
        levelObj = Level.WARN;
        break;
      case LogDef.LOG_LEVEL.LOG_ERROR:
        levelObj = Level.ERROR;
        break;
      case LogDef.LOG_LEVEL.LOG_FATAL:
        levelObj = Level.FATAL;
        break;
      case LogDef.LOG_LEVEL.LOG_OFF:
        levelObj = Level.OFF;
        break;
      default:
        break;
    }

    return levelObj;
  }

  protected boolean IsReady() {
    return loggerObj != null;
  }

  /**
   *
   * @param filename String : file name
   * @param nChangeFileRule byte : type of rolling.
   * @param nMaxSize int : count in bytes
   * @param nMaxIndex int : number of file to reache before reset index to 0.
   */
  protected void createFileAppender(String filename, byte nChangeFileRule,
                                    int nMaxSize, int nMaxIndex) {

    if (fileAppender != null) {
      loggerObj.removeAppender(fileAppender);
    }

    //Valid date log path : must have / at the end
    if (!csLogPath.endsWith(GeneralDef.OS.fileSeperator))
      csLogPath = csLogPath + GeneralDef.OS.fileSeperator;

    switch (nChangeFileRule) {
      case LogDef.FILE.CHANGE_ON_SZIE:
        try {
          fileAppender = new RollingFileAppender(new SimpleLayout(),
                                                 this.csLogPath + filename +
                                                 ".log");
        }
        catch (java.io.IOException ex) {
          ex.printStackTrace();
        }
        ( (RollingFileAppender) fileAppender).setMaximumFileSize(nMaxSize);
        ( (RollingFileAppender) fileAppender).setMaxBackupIndex(nMaxIndex);
        ( (RollingFileAppender) fileAppender).setImmediateFlush(true);
        break;
      case LogDef.FILE.CHANGE_ON_INDEX:
        try {
          fileAppender = new RollingFileAppender(new SimpleLayout(),
                                                 csLogPath +
                                                 filename + ".log");
          ( (RollingFileAppender) fileAppender).setMaximumFileSize(nMaxSize);
          ( (RollingFileAppender) fileAppender).setMaxBackupIndex(nMaxIndex);
          ( (RollingFileAppender) fileAppender).setImmediateFlush(true);
        }
        catch (java.io.IOException ex) {
          ex.printStackTrace();
        }
        break;
      case LogDef.FILE.CHANGE_ON_DATE:
        try {
          //System.out.println("My log is update on 15th 06 2007");
          fileAppender = new DailyRollingFileAppender(
              new SimpleLayout(),
                csLogPath + filename + ".log", "'.'yyyyMMdd-HH");
//                                  csLogPath + filename + ".log.", "yyyyMMDDhhmmdd");
            ( (DailyRollingFileAppender) fileAppender).setImmediateFlush(true);
        }
        catch (java.io.IOException ex) {
          ex.printStackTrace();
        }
        break;
    }
    loggerObj.addAppender(fileAppender);
  }

  /**
   * create console appender to write to console
   */
  protected void createConsoleAppender() {
    this.loggerObj.setAdditivity( true);
//    consoleAppender = new ConsoleAppender();
//    consoleAppender.setName("consolelogger");
//    consoleAppender.clearFilters();
//    if (loggerObj != null) {
//      this.loggerObj.setAdditivity( true);
//      loggerObj.addAppender(consoleAppender);
//    }
  }

  /**
   * Create multi syslog appender. Default syslogIndex = 0;
   * @param syslogAddr String
   * @param nPort int
   * @param syslogIndex int
   */
  public void createSyslogAppender(String syslogAddr, int nPort,
                                   int syslogIndex) {

    //remove appender cu
    if (arrSyslogAppender == null) {
      arrSyslogAppender = new Appender[syslogIndex + 1];
    }
    if (syslogIndex >= arrSyslogAppender.length) {
      //recreate array syslog appender
      Appender newAppenderArr[] = new Appender[syslogIndex + 1];
      for (int oldSyslogIdx = 0; oldSyslogIdx < arrSyslogAppender.length;
           oldSyslogIdx++) {
        newAppenderArr[oldSyslogIdx] = arrSyslogAppender[oldSyslogIdx];
        arrSyslogAppender[oldSyslogIdx] = null;
      }
      arrSyslogAppender = newAppenderArr;
    }

    if (arrSyslogAppender[syslogIndex] != null) {
      loggerObj.removeAppender(arrSyslogAppender[syslogIndex]);
    }

    arrSyslogAppender[syslogIndex] = new SyslogAppender(new SimpleLayout(),
        SyslogAppender.LOG_SYSLOG);
    ( (SyslogAppender) arrSyslogAppender[syslogIndex]).setSyslogHost(syslogAddr);
    loggerObj.addAppender(arrSyslogAppender[syslogIndex]);
  }

  /**
   * The most common function. used to write log to all appenders : file, syslog, console.
   * Note : level must be greate or equal to level of LogWriter object
   * @param logMessage String
   * @param nLevel byte
   */
  public void writeLog(String logMessage, byte nLevel) {
    switch (nLevel) {
      case GeneralDef.LOG_LEVEL.LOG_ALL:
        csDateTime = DateFormat.formatTime24();
        logMessage = csDateTime + " ALL    " + logMessage;
        loggerObj.info(logMessage);
        if( bLogToConsole){  System.out.println(logMessage);   }        break;
      case GeneralDef.LOG_LEVEL.LOG_DEBUG:
        csDateTime = DateFormat.formatTime24();
        logMessage = csDateTime + " DEBUG  " + logMessage;
        loggerObj.debug(logMessage);
        if( bLogToConsole){  System.out.println(logMessage);   }

        break;
      case GeneralDef.LOG_LEVEL.LOG_INFO:
        csDateTime = DateFormat.formatTime24();
        logMessage = csDateTime + " INFO   " + logMessage;
        loggerObj.info(logMessage);
        if( bLogToConsole){  System.out.println(logMessage);   }
        break;
      case GeneralDef.LOG_LEVEL.LOG_WARNING:
        csDateTime = DateFormat.formatTime24();
        logMessage = csDateTime + " WARN   " + logMessage;
        loggerObj.warn(logMessage);
        if( bLogToConsole){  System.out.println(logMessage);   }
        break;
      case GeneralDef.LOG_LEVEL.LOG_ERROR:
        csDateTime = DateFormat.formatTime24();
        logMessage = csDateTime + " ERROR  " + logMessage;
        loggerObj.error(logMessage);
        if( bLogToConsole){  System.out.println(logMessage);   }
        break;
      case GeneralDef.LOG_LEVEL.LOG_FATAL:
        csDateTime = DateFormat.formatTime24();
        logMessage = csDateTime + " FATAL  " + logMessage;
        loggerObj.fatal(logMessage);
        if( bLogToConsole){  System.out.println(logMessage);   }
        break;
      case GeneralDef.LOG_LEVEL.LOG_OFF:
        break;
      default:
        loggerObj.setLevel(Level.ERROR);
        break;
    }

  }

  public static void setGlobalLogLevel(byte level) {
    if (bConfigured == false) {
      bConfigured = true;
      BasicConfigurator.configure();
    }
    Logger logger = Logger.getLogger("com.elcom.smsroaming");
    LoggerRepository repository = logger.getLoggerRepository();
    repository.setThreshold(getLevelObject(level));
  }

  public static void resetLogDefucation() {
    BasicConfigurator.resetConfiguration();
  }

  /**
   * Remove File appender internally
   */
  protected void removeFileAppender() {
    if (this.fileAppender != null)
      loggerObj.removeAppender(fileAppender);
  }

  /**
   * Remove console appender.
   */
  public void removeConsoleAppender() {
    if (this.consoleAppender != null)
      loggerObj.removeAppender(consoleAppender);
  }

  /**
   * Remove all syslog appenders
   */
  public void removeSyslogAppender() {
    if (arrSyslogAppender != null) {
      for (int i = 0; i < arrSyslogAppender.length; i++) {
        if (this.arrSyslogAppender[i] != null)
          loggerObj.removeAppender(arrSyslogAppender[i]);
      }
    }
  }

  /**
   * Set file log info for log object.
   * csPath : directory to keep log files.
   * @param filename String : log filename
   * @param nChangeFileFlag byte
   * @param nMaxSize int
   * @param nMaxIndex int
   * @param flushImmediately boolean
   */
  public void setFileLogInfo(boolean bActive, String csPath, String filename, byte nChangeFileFlag,
                             int nMaxSize, int nMaxIndex,
                             boolean flushImmediately) {
    File logdir = new File(csPath);
    if(!logdir.exists()){
      logdir.mkdirs();
    }
    this.csLogPath = new String (csPath);
    this.csModuleName = new String(filename);

//    this.nChangeLogFlag = nChangeFileFlag;
    this.nChangeLogFlag = LogDef.FILE.CHANGE_ON_DATE;
//    this.nMaxFileSize = nMaxSize;
    this.nMaxFileSize = 2 * 1024 * 1024;//ThanhTT fix cung
//    this.nMaxFileIndex = nMaxIndex;
    this.nMaxFileIndex = 2000;
    bFlushImmediately = flushImmediately;

    bLogToFile = bActive;
  }

  /**
   *
   * @param bActive boolean
   * @param syslogHost String
   * @param nPort int
   */
  public void setSysLogInfo(boolean bActive, String syslogHost, int nPort) {
    this.syslogHost = new String(syslogHost);
    nSyslogPort = nPort;

    this.bLogToSysLog = bActive;
  }

  /**
   *
   * @param bActive boolean
   */
  public void setConsoleInfo( boolean bActive){
    this.bLogToConsole = bActive;
  }
  /**
   * Close before destroy this writer object
   */
  public void close() {
    if (this.fileAppender != null) {
      fileAppender.close();
      fileAppender = null;
    }

    if (arrSyslogAppender != null) {
      for (int i = 0; i < this.arrSyslogAppender.length; i++) {
        if (this.arrSyslogAppender[i] != null) {
          arrSyslogAppender[i].clearFilters();
          arrSyslogAppender[i].close();
          arrSyslogAppender[i] = null;
        }
      }
    }

    if (this.consoleAppender != null) {
      consoleAppender.close();
      consoleAppender = null;
    }

    loggerObj.removeAllAppenders();
  }

  /**
   * Open a logger object to write.
   * Note : must call SetFileInfo..., SetSyslogInfo before call this method.
   */
  public void open() {
    if (this.bLogToConsole) {
      this.createConsoleAppender();
    }
    else {
      this.loggerObj.setAdditivity( false);
      removeConsoleAppender();
    }
    if (this.bLogToFile) {
      this.createFileAppender(this.loggerName, (byte)this.nChangeLogFlag,
                              this.nMaxFileSize, this.nMaxFileIndex);
    }
    else {
      this.removeFileAppender();
    }
    if (this.bLogToSysLog) {
      this.createSyslogAppender(this.syslogHost, LogDef.SYSLOG.DFLT_PORT, 0);
    }
    else {
      this.removeSyslogAppender();
    }

  }
}
