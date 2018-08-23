package log4j;



public class GeneralDef {

  public static class OS {

    /**

     * Define value for operating system

     */

    public static final byte PLATFORM_SOLARIS = (byte) 1;

    public static final byte PLATFORM_LINUX = (byte) 2;

    public static final byte PLATFORM_WINDOW = (byte) 3;

    public static final String LINE_SEPERATOR_WINDOW = "\r\n";

    public static final String LINE_SEPERATOR_UNIX = "\n";

    public static final String FILE_SEPERATOR_WINDOW = "\\";

    public static final String FILE_SEPERATOR_UNIX = "/";

    public static final String timezone = "GMT+7";



    /**

     * Variables to hold OS info.

     */

    public static String csOSName = null; // OS name

    public static byte nPlatform = PLATFORM_SOLARIS; // OS code

    public static String lineSeperator = "";

    public static String fileSeperator = "";



    /**

     * Check the operating system

     */

    static {

//    //\\System.out.print(System.getProperty("os.name"));

      csOSName = System.getProperty("os.name");

      csOSName = csOSName.toLowerCase();

      if (csOSName.indexOf("linux") >= 0) {

        nPlatform = PLATFORM_LINUX;

        lineSeperator = LINE_SEPERATOR_UNIX;

        fileSeperator = FILE_SEPERATOR_UNIX;

      }

      else if (csOSName.indexOf("windows") >= 0) {

        nPlatform = PLATFORM_WINDOW;

        lineSeperator = LINE_SEPERATOR_WINDOW;

        fileSeperator = FILE_SEPERATOR_WINDOW;

      }

      else if (csOSName.indexOf("solaris") >= 0) {

        nPlatform = PLATFORM_SOLARIS;

        lineSeperator = LINE_SEPERATOR_UNIX;

        fileSeperator = FILE_SEPERATOR_UNIX;

      }

      else if (csOSName.indexOf("sun") >= 0) {

        nPlatform = PLATFORM_SOLARIS;

        lineSeperator = LINE_SEPERATOR_UNIX;

        fileSeperator = FILE_SEPERATOR_UNIX;

      }

      else {

//      //\\System.out.println("Unsuppoerted OS");

        fileSeperator = "?";

      }

    }



  }





  /**

   * Define all log level here

   */



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



  /**

   * Copyright definition

   */

  public static final String copyright =

      "# Copyright (c) 2006 Elcom Company JSC " + OS.lineSeperator +

      "# This product includes software developed by Elcom by whom copyright " +

      OS.lineSeperator +

      "# and know-how are retained, all rights reserved.";



  public static final String projectName = "# ProjectName";

  public static final String version = "#1.0";



}
