package log4j;



/**
 * <p>Title: HTC charging gateway solution</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: Elcom - JSC</p>
 * @author HungDM-Mobile ONE group
 * @version 1.0
 */


public class LogWriterDemo {
  LogWriter objWriter = null;
  String csPath = "c:\\";
  String csModuleName = "TestingLog4j";
  byte nLogLevel = LogDef.LOG_LEVEL.LOG_DEBUG;
  public LogWriterDemo() {
    // Tao doi tuong log4j voi moduleName vaf level cua module.
    objWriter = new LogWriter( csModuleName, nLogLevel);

    objWriter.setFileLogInfo(true,csPath, csModuleName,
                             LogDef.FILE.CHANGE_ON_SZIE, //Change file when maximum size reached
                             LogDef.FILE.MAX_FILE_SIZE /100, //3M
                             LogDef.FILE.MAX_FILE_BACKUP_INDEX,// 1000 file per day
                             false //flush immediately
                             );
    objWriter.setSysLogInfo(true,"localhost",513);
    objWriter.setConsoleInfo(true);

    objWriter.open();
  }

//Write a string with at nLevel
  //Filer will be done inside LogWriter
  public void WriteLog(String logString, byte nLevel){
    if( objWriter != null){
      objWriter.writeLog( logString, nLevel);
    }else{
      System.out.println("[" + nLevel + "] " + logString );
    }
  }

  public static void main(String[] args) {
    LogWriterDemo logWriterDemo1 = new LogWriterDemo();
    int nLine = 0;
    System.out.println("Start.");
    do{
      nLine ++;
      logWriterDemo1.WriteLog("Testing " + nLine, LogDef.LOG_LEVEL.LOG_INFO);
    }while( nLine < 100);
    System.out.println("End.");
  }

}
