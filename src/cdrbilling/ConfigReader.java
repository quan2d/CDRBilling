/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cdrbilling;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Trung-Tran
 */
public class ConfigReader {

    public static String DB_URL = "";
    public static String DB_USER = "";
    public static String DB_PASS = "";
    public static String LOG_PATH = "";
//    public static String LINK_SMS = "";
//    public static String PHONE_NUMBER = "";
    public static String SERVER_NAME = "";
//    public static String ENABLE_SEND_SMS = "";
    public static String FOLDER_NAME = "";
    public static String CMD_MONEY = "";
    public static String CP_NAME = "";
    public static String FTP_HOST = "";
    public static String FTP_PORT = "";
    public static String FTP_USER = "";
    public static String FTP_PASS = "";
    public static String FOLDER_BACKUP_PATH = "";
    public static String IS_DB = "";
    public static String HOUR = "";
    public static String MINUTE = "";
    public static String TIME_DELAY = "";

    public boolean readConfig(String config) {
        FileInputStream input = null;
        try {
            Properties properties = new Properties();
            input = new FileInputStream(config);
            properties.load(input);

            DB_URL = properties.getProperty("DB_URL");
            DB_USER = properties.getProperty("DB_USER");
            DB_PASS = properties.getProperty("DB_PASS");
            LOG_PATH = properties.getProperty("LOG_PATH");
//            SPEED = properties.getProperty("SPEED");
//            ENABLE_SEND_SMS = properties.getProperty("ENABLE_SEND_SMS");
//            LINK_SMS = properties.getProperty("LINK_SMS");
//            PHONE_NUMBER = properties.getProperty("PHONE_NUMBER");
            SERVER_NAME = properties.getProperty("SERVER_NAME");
            FOLDER_NAME = properties.getProperty("FOLDER_NAME");
            CMD_MONEY = properties.getProperty("CMD_MONEY");
            CP_NAME = properties.getProperty("CP_NAME");
            FTP_HOST = properties.getProperty("FTP_HOST");
            FTP_PORT = properties.getProperty("FTP_PORT");
            FTP_USER = properties.getProperty("FTP_USER");
            FTP_PASS = properties.getProperty("FTP_PASS");
            FOLDER_BACKUP_PATH = properties.getProperty("FOLDER_BACKUP_PATH");
            IS_DB = properties.getProperty("IS_DB");
            HOUR = properties.getProperty("HOUR");
            MINUTE = properties.getProperty("MINUTE");
            TIME_DELAY = properties.getProperty("TIME_DELAY");


            input.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                input.close();
            } catch (IOException ex) {
                System.out.println(ex.toString());
            }
        }
    }
}
