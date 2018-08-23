/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cdrbilling;

import common.jdbc.JdbcConnectionPool;
import common.jdbc.JdbcParameter;
import dao.SmsOutDao;
import dao.SmsOutModel;
import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import log4j.LogDef;
import log4j.LogWriter;

/**
 *
 * @author TrungTD - QuanDD update
 */
public class CDRBilling extends TimerTask {

    //Timer
    private Timer _timer;
    //DB
    private String db_url = "";
    private String db_username = "";
    private String db_password = "";
    private String configPath = "";
    private JdbcConnectionPool pool;
    private JdbcParameter parameter;
    private String logPath = "";
    //Log4J
    private LogWriter objWriter = null;
    private String csPath = "";
    private String csModuleName = "CDRBilling";
    private byte nLogLevel = LogDef.LOG_LEVEL.LOG_INFO;
//    private int speed = 10;//second
    private String server_name = "";
    private boolean ready = false;
//    Dao
    private SmsOutDao smsOutDao;
    // Other
//    private boolean enable_send_sms = false;
//    private String link_sms = "";
//    private String phonenumber = "";
    private String folder_name = "";
    private String cmdMoney = "";
    private String cpName = "";
    private Hashtable<String, String> cmd_money = new Hashtable<String, String>();
    private boolean is_db = false;
    public String folder_backup_path = "";
    public int hour = 0;
    public int minute = 0;
    public int time_delay = 0;
    // FTP
    private String ftp_host = "";
    private int ftp_port = 0;
    private String ftp_user = "";
    private String ftp_pass = "";
    FTPClient client = new FTPClient();
    // check cmd
    private Hashtable<String, String> lscmd_fail = new Hashtable();

    public CDRBilling() {
        _timer = new Timer();
    }

    public void initCmdMoney() {
        if (cmdMoney != null && !cmdMoney.equals("")) {
            String[] split_cmd = cmdMoney.split(";");
            if (split_cmd.length > 0) {
                String[] spl_2;
                for (int i = 0; i < split_cmd.length; i++) {
                    spl_2 = split_cmd[i].split(":");
                    if (spl_2.length > 1) {
                        cmd_money.put(spl_2[0], spl_2[1]);
                    }
                }
            }
        }
    }

    public void initCmdFail() {
        File cmd_fail_file = new File("");
        String path_cmd_fail = "";
        path_cmd_fail = cmd_fail_file.getAbsoluteFile() + "/config/command.txt";
        cmd_fail_file = new File(path_cmd_fail);
        if (cmd_fail_file.isFile()) {
            String str = "";
            String rs = "";
            try {
                FileInputStream fstream = null;
                DataInputStream in = null;
                BufferedReader br = null;
                fstream = new FileInputStream(cmd_fail_file);
                in = new DataInputStream(fstream);
                br = new BufferedReader(new InputStreamReader(in));
                while ((str = br.readLine()) != null) {
                    rs = str.toUpperCase().trim();
//                    System.out.println(rs);
                    if (rs != null && !rs.equals("")) {
                        lscmd_fail.put(rs, rs);
//                        System.out.println(cdr.lstOperator_vt.get(rs));
                    }
                }
                br.close();
                in.close();
                fstream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                cmd_fail_file.createNewFile();
            } catch (Exception e) {
                System.out.println("can not create file");
                e.printStackTrace();
            }
        }

    }

    public void start() {
        loadConfig();
        initLogger();
        initCmdMoney();
        initCmdFail();

        Date timeAction = new Date();
        timeAction.setHours(hour);
        timeAction.setMinutes(minute);
        if (_timer != null) {
//            _timer.scheduleAtFixedRate(this, timeAction, 86400000);
            _timer.scheduleAtFixedRate(this, timeAction, time_delay * 60000);
            System.out.println("---------------------");
            objWriter.writeLog("Time Start:  " + timeAction, LogDef.LOG_LEVEL.LOG_INFO);
            objWriter.writeLog("Time Delay:  " + time_delay, LogDef.LOG_LEVEL.LOG_INFO);
            //_timer.schedule(this, timeAction, 300000);
        }

    }

    public void stop() {
        objWriter.writeLog("Stoping Service ....", nLogLevel);
        ready = false;
        pool.closeConnections();

        if (_timer != null) {
            _timer.cancel();
        }
    }

    @Override
    public void run() {
        processSmsOutCMDFail();
    }

    private void initLogger() {
        objWriter = new LogWriter(logPath, nLogLevel);
        objWriter.setFileLogInfo(true, csPath, csModuleName,
                LogDef.FILE.CHANGE_ON_SZIE, //Change file when maximum size reached
                LogDef.FILE.MAX_FILE_SIZE / 10, //1M,
                LogDef.FILE.MAX_FILE_BACKUP_INDEX,// 1000 file per day,
                false //flush immediately
                );
        objWriter.setSysLogInfo(true, "localhost", 513);
        objWriter.setConsoleInfo(true);
        objWriter.open();
    }

    private void loadConfig() {
        File file = new File("");
        file = file.getAbsoluteFile();
        configPath = System.getProperty("user.dir");
        this.csPath = configPath;
        configPath += "\\config\\config.cfg";
        System.out.println("PATH CONFIG: " + configPath);
        ConfigReader conf = new ConfigReader();
        conf.readConfig(configPath);

        db_url = conf.DB_URL.toString();
        db_username = conf.DB_USER.toString();
        db_password = conf.DB_PASS.toString();
        logPath = "\\" + conf.LOG_PATH.toString();
        this.csPath += this.logPath;
//        System.out.println("csPath: " + csPath);
//        numdate = Integer.parseInt(conf.NUMDATE.toString());
//        speed = Integer.parseInt(conf.SPEED.toString());
//        link_sms = conf.LINK_SMS.toString();
//        phonenumber = conf.PHONE_NUMBER.toString();
        server_name = conf.SERVER_NAME.toString();
        folder_name = conf.FOLDER_NAME.toString();
        cmdMoney = conf.CMD_MONEY.toString();
        cpName = conf.CP_NAME.toString();
        ftp_host = conf.FTP_HOST.toString();
        try {
            ftp_port = Integer.parseInt(conf.FTP_PORT.toString());
        } catch (Exception e) {
            ftp_port = 21;
        }
        ftp_user = conf.FTP_USER.toString();
        ftp_pass = conf.FTP_PASS.toString();
        folder_backup_path = conf.FOLDER_BACKUP_PATH.toString();
        if (folder_name.trim().equals("")) {
            folder_name = "VINA_CMD_FAIL";
        }
        try {
            hour = Integer.parseInt(conf.HOUR.toString());
            minute = Integer.parseInt(conf.MINUTE.toString());
            time_delay = Integer.parseInt(conf.TIME_DELAY.toString());
            is_db = Boolean.parseBoolean(conf.IS_DB.toString());
        } catch (Exception e) {
            is_db = false;
            hour = 0;
            minute = 5;
            time_delay = 1440;
        }
    }

    private void loadDB() {
        try {
            parameter = new JdbcParameter();
            parameter.setUrl(db_url);
            parameter.setUsername(db_username);
            parameter.setPassword(db_password);
            parameter.setMaxConn(1);
            parameter.setClearPeriod(30000);
            if (pool != null) {
                pool.closeConnections();
            }
            pool = new JdbcConnectionPool(parameter);
            if (pool != null) {
                ready = true;
                this.smsOutDao = new SmsOutDao(pool);
//                System.out.println("Connect DB ok");
                objWriter.writeLog("Connected to DB OK ", nLogLevel);
            } else {
                ready = false;
                System.out.println("Connect DB fail");
                objWriter.writeLog("Connect DB Fail", LogDef.LOG_LEVEL.LOG_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Connect DB fail");
            objWriter.writeLog("Connect DB Fail", LogDef.LOG_LEVEL.LOG_ERROR);
        }
    }

    private void releaseDB() {
        this.smsOutDao = null;
        if (pool != null) {
            pool.closeConnections();
            pool = null;
        }
    }

    private void processSmsOutCMDFail() {
        try {
            String file_name = "";
            Date d = new Date();
            SimpleDateFormat f_date = new SimpleDateFormat("yyyy-MM-dd");

            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.DATE, -1);
            String begin_date = f_date.format(cal.getTime()) + " 00:00:00";
            String end_date = f_date.format(cal.getTime()) + " 23:59:59";
            file_name = cpName + "." + f_date.format(cal.getTime()).replaceAll("-", "") + ".txt";
            SimpleDateFormat f_date_time = new SimpleDateFormat("ddMMyyyy HH:mm:SS");
            System.out.println("File Name: " + file_name);

            File check_file = new File(folder_backup_path + "/" + file_name);
            if (check_file.isFile()) {
                System.out.println("File nay da ton tai");

                Runtime.getRuntime().gc();
            } else {
                if (is_db) {

                    loadDB();
                    List<SmsOutModel> smsOutModel = null;
                    smsOutModel = smsOutDao.getSmsOutFail(begin_date, end_date);

                    String money = "";
                    String phone_number = "";
                    String command_name = "";
                    String service_number = "";
                    String date_time_process = "";
                    String date_send = "";
                    List<String> data = new ArrayList<String>();
                    boolean check_key = false;
                    int t = -1;
                    objWriter.writeLog("Begin Load DB:----  ", LogDef.LOG_LEVEL.LOG_INFO);
                    for (SmsOutModel smsOutModel1 : smsOutModel) {

                        money = cmd_money.get(smsOutModel1.getService_number());
                        phone_number = smsOutModel1.getPhone_number().trim();
                        command_name = smsOutModel1.getCommand_name().trim();
                        service_number = smsOutModel1.getService_number().trim();
                        date_time_process = f_date_time.format(smsOutModel1.getDate_time_process());
                        date_send = date_time_process.substring(0, 8);


                        Set keys = lscmd_fail.keySet();
//
//                        StringBuilder bui = new StringBuilder(command_name);
//                        bui.reverse();
                        for (Iterator i = keys.iterator(); i.hasNext();) {
                            String key = (String) i.next();
                            t = command_name.toUpperCase().indexOf(key);
                            if (t > -1) {
                                check_key = true;
                            }
//                        System.out.println("Key: " + key);
//                        System.out.println("COMMAND NAME: " + command_name.toUpperCase());
//                        System.out.println("check key: " + check_key);
//                        System.out.println("----------------------");
                        }
//!lscmd_fail.containsKey(command_name)
//                    System.out.println("NUMBER: ************************");
                        Thread.sleep(100);
//                    System.out.println( j++);
                        if (!check_key) {
//                            System.out.println("MONEY: " + money);
//                            System.out.println("PHONE: " + phone_number);
//                            System.out.println("COMMAND NAME: " + command_name);
//                            System.out.println("SERVICE_NUMBER: " + service_number);
//                            System.out.println("DATE TIME PROCESS: " + date_time_process);
//                            System.out.println("DATE TIME SEND: " + date_send);
//                            System.out.println("--------------------------------------");

                            if (command_name == null || command_name.equals("")) {
                                command_name = "NULL";
                            } else if (command_name.indexOf("\n") != -1) {
                                command_name = command_name.replaceAll("\\\n", "");
                            }

                            if (command_name.length() > 20) {
                                command_name = command_name.substring(0, 20);
                            }

                            data.add(service_number + ";" + phone_number + ";" + date_time_process + ";" + date_time_process + ";" + command_name + ";" + date_send + ";" + money + ";" + cpName);
//                            check_key = true;
                        }
                        check_key = false;
                    }
                    objWriter.writeLog("Load DB OK:----End  ", LogDef.LOG_LEVEL.LOG_INFO);
                    writeFile(file_name, data);
                    data.clear();
                }
                boolean check_upload_file = false;
                check_upload_file = Upfile();
                if (check_upload_file) {
                    objWriter.writeLog("Upload File OK:  ", LogDef.LOG_LEVEL.LOG_INFO);
                    boolean check_copy_file = false;
                    File file = new File(folder_name + "/" + file_name);
                    file = file.getAbsoluteFile();

                    String file_ = file.toString();
                    System.out.println("File: " + file_);
                    check_copy_file = copyfile(file_, folder_backup_path, "file");
                    if (check_copy_file) {
                        delete(file);
                    } else {
                        objWriter.writeLog("Upload File FAIL:  ", LogDef.LOG_LEVEL.LOG_ERROR);
                        System.out.println("Copy file Fail");
                        System.exit(0);
                    }
                } else {
                    System.out.println("Upfile fail");
                    objWriter.writeLog("Upfile file fail:  " + file_name, LogDef.LOG_LEVEL.LOG_ERROR);
                    System.exit(0);
                }
                DisconnectFtpServer();

                releaseDB();
                Runtime.getRuntime().gc();
                objWriter.writeLog("Run way OK :  ", LogDef.LOG_LEVEL.LOG_INFO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            objWriter.writeLog("Stop tool, reason: " + e.getMessage(), LogDef.LOG_LEVEL.LOG_ERROR);
            System.exit(0);
        }
    }

    private void processSmsOutCMDFail_ERROR() {
        try {
            while (true) {
                loadDB();
                List<SmsOutModel> smsOutModel = null;
                Date d = new Date();
                SimpleDateFormat f_date = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cal = Calendar.getInstance();

                cal.setTime(d);
                cal.add(Calendar.DATE, -1);
                String begin_date = f_date.format(cal.getTime()) + " 00:00:00";
                String end_date = f_date.format(cal.getTime()) + " 23:59:59";
                smsOutModel = smsOutDao.getSmsOutFail(begin_date, end_date);
                for (SmsOutModel smsOutModel1 : smsOutModel) {
                    System.out.println("PHONE NUMBER" + smsOutModel1.getPhone_number());
                    System.out.println("COMMAND: " + smsOutModel1.getCommand_name());
                    System.out.println("SERVICE NUMBER: " + smsOutModel1.getService_number());
                    System.out.println("TIME: " + smsOutModel1.getDate_time_process());
                    System.out.println("--------------------------------------");
                }
//                if (enable_use_time_config) {
//                    // su dung thoi gian dat trong config
//                    if (begin_time_config != null && !begin_time_config.trim().equals("")
//                            && end_time_config != null && !end_time_config.trim().equals("")) {
//                        List<String> data = new ArrayList<String>();
//                        String file_name = "";
//                        smsOutModel = smsOutDao.getSmsOutFail(begin_time_config, end_time_config);
//
//                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//                        Calendar date_begin = Calendar.getInstance();
//                        Calendar date_end = Calendar.getInstance();
//                        date_begin.setTime(formatter.parse(begin_time_config));
//                        date_end.setTime(formatter.parse(end_time_config));
//                        while (date_begin.before(date_end)) {
//                            file_name = date_begin.YEAR + "" + (date_begin.MONTH + 1) + date_begin.DATE + ".txt";
////                            smsOutModel = smsOutDao.getSmsOutFail(date_begin.toString(), );
//                            date_begin.add(Calendar.DATE, 1);
//                        }
//
//
//                        for (SmsOutModel smsOutModel1 : smsOutModel) {
//                            System.out.println("PHONE NUMBER" + smsOutModel1.getPhone_number());
//                            System.out.println("COMMAND: " + smsOutModel1.getCommand_name());
//                            System.out.println("SERVICE NUMBER: " + smsOutModel1.getService_number());
//                            System.out.println("TIME: " + smsOutModel1.getDate_time_process());
//                            System.out.println("--------------------------------------");
//                            file_name = smsOutModel1.getDate_time_process().getYear() + ".txt";
//                        }
//                        // Quet xong du lieu co thoi gian dat trong file config --> Stop tool
//                        objWriter.writeLog("Stop tool, Nguyen nhan : Quet xong tap du lieu dat trong config", LogDef.LOG_LEVEL.LOG_ERROR);
//                        System.exit(0);
//                    } else {
//                        // chua nhap thoi gian trong config ->> stop tool
//                        objWriter.writeLog("Stop tool, Nguyen nhan : chua nhap thoi gian trong config", LogDef.LOG_LEVEL.LOG_ERROR);
//                        System.exit(0);
//                    }
//                } else {
//                    // su dung thoi gian hang ngay
//                }


                releaseDB();
                Runtime.getRuntime().gc();
//                Thread.sleep(speed * 60000);
            }
        } catch (Exception e) {
            objWriter.writeLog("Stop tool, Nguyen nhan :" + e.getMessage(), LogDef.LOG_LEVEL.LOG_ERROR);
            System.exit(0);
        }
    }

    private String getday(int inc) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar today = Calendar.getInstance();
        // Subtract 1 day
        today.add(Calendar.DATE, inc);
        // Make an SQL Date out of that
        java.sql.Date day = new java.sql.Date(today.getTimeInMillis());
        return (dateFormat.format(day));
    }

    private boolean ConnectFtpServer() {
        try {
            client.connect(ftp_host, ftp_port);
            client.login(ftp_user, ftp_pass);
            System.out.println("Connected FTP Server!");
            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
            Logger.getLogger(CDRBilling.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    public boolean Upfile() {
        Hashtable ht = new Hashtable();
        try {
            ConnectFtpServer();
            String[] listfolder = client.listNames();
            System.out.println("LIST FODER: " + listfolder.length);
//            for (int i = 0; i < listfolder.length; i++) {
//                System.out.println("Folder " + listfolder[i] + " exist!");
//                client.changeDirectory("\\" + listfolder[i]);
//                String[] listfile = client.listNames();
//                for (int j = 0; j < listfile.length; j++) {
//                    ht.put(listfile[j], "true");
//                }
//                client.changeDirectoryUp();
//            }
            System.out.println("Hashcode size = " + ht.size());
            File folder = new File(folder_name);
            folder = folder.getAbsoluteFile();
            File file = new File(folder + "");

            File[] filearray = file.listFiles();
            for (File f : filearray) {
                try {
                    if (ht.get(f.getName()) == null & f.getName().indexOf(getday(-1)) != -1) {
                        client.upload(f);
                        System.out.println("Upload file " + f.getName() + " completed!");
                    }
                } catch (Exception e) {
                    System.out.println("Don't upload file:" + f.getName() + " !");
                }
                //System.out.println("########" + f.getName());
//                if (ht.get(f.getName()) == null & f.getName().indexOf(this.getday(-1)) != -1) {
//                    client.changeDirectory("\\" + this.getday(-1));
//                    //System.out.println(client.currentDirectory());
//                    try {
//                        client.upload(f);
//                        System.out.println("Upload file " + f.getName() + " completed!");
//                    } catch (Exception ex) {
//                        System.out.println("Don't upload file:" + f.getName() + " !");
//                    }
//                }
            }
            return true;
        } catch (IllegalStateException ex) {
            System.out.println("Error: " + ex.toString());
            Logger.getLogger(CDRBilling.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("Error: " + ex.toString());
            Logger.getLogger(CDRBilling.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPIllegalReplyException ex) {
            System.out.println("Error: " + ex.toString());
            Logger.getLogger(CDRBilling.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPException ex) {
            System.out.println("Error: " + ex.toString());
            Logger.getLogger(CDRBilling.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPDataTransferException ex) {
            System.out.println("Error: " + ex.toString());
            Logger.getLogger(CDRBilling.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPAbortedException ex) {
            System.out.println("Error: " + ex.toString());
            Logger.getLogger(CDRBilling.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPListParseException ex) {
            System.out.println("Error: " + ex.toString());
            Logger.getLogger(CDRBilling.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean DisconnectFtpServer() {
        try {
            client.disconnect(true);
            System.out.println("Server disconnected!");
            return true;
        } catch (IllegalStateException ex) {
            System.out.println("Error: " + ex.toString());
            Logger.getLogger(CDRBilling.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("Error: " + ex.toString());
            Logger.getLogger(CDRBilling.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPIllegalReplyException ex) {
            System.out.println("Error: " + ex.toString());
            Logger.getLogger(CDRBilling.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPException ex) {
            System.out.println("Error: " + ex.toString());
            Logger.getLogger(CDRBilling.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private void writeFile(String file_name, List<String> data) {
        objWriter.writeLog("Begin Write file:  " + file_name, LogDef.LOG_LEVEL.LOG_INFO);
        File folder = new File(folder_name);
        folder = folder.getAbsoluteFile();
        if (!folder.isDirectory()) {
            folder.mkdir();
        } else {
//            System.out.println(" Thu muc Da ton tai");
        }
        folder = null;
        File file = new File(folder_name + "/" + file_name);
        file = file.getAbsoluteFile();
//        System.out.println("File:" + file);
        System.out.println("Size data: " + data.size());
        if (!file.isFile()) {
            try {
                file.createNewFile();
                //write file
                FileWriter fw = new FileWriter(file);
                BufferedWriter out = new BufferedWriter(fw);
                out.flush();
                for (int i = 0; i < data.size(); i++) {
                    out.write(data.get(i));
                    out.write("\n");
                }
                out.close();
                fw.close();
                System.out.println("write file successful");
                objWriter.writeLog("write file successful, name file:  " + file_name, LogDef.LOG_LEVEL.LOG_INFO);
            } catch (Exception e) {
                e.printStackTrace();
                objWriter.writeLog("write Error, name file:  " + file_name + ",Reson" + e.getMessage(), LogDef.LOG_LEVEL.LOG_INFO);
                System.exit(0);
            }
        } else {
//            System.out.println("File Da ton tai");
        }
        file = null;
    }

    private static boolean copyfile(String srFile, String dtFile, String type) {
        String copycmd = "";
        if (srFile != null && dtFile != null) {
            if (type.equals("file")) {
                copycmd = "xcopy " + srFile + " " + dtFile + " /y";
            } else {
                copycmd = "xcopy " + srFile + " " + dtFile + " /i /e /y";
                String vn_path = dtFile.replaceAll("\\\\", "");
                System.out.println("vn_path:" + vn_path);
//                File file = new File("vn");
            }
        }
        System.out.println(copycmd);
        System.out.println(srFile);
        System.out.println(dtFile);
        try {
            Runtime rt_ex = Runtime.getRuntime();
            Process pr_ex = rt_ex.exec(copycmd);
            BufferedReader input_ex = new BufferedReader(new InputStreamReader(pr_ex.getInputStream()));
            String line_ex = "";
            while ((line_ex = input_ex.readLine()) != null) {
                System.out.println(line_ex);
            }
            pr_ex.waitFor();
            pr_ex.destroy();

            return true;

        } catch (Exception e) {
            e.printStackTrace();            
        }
        return false;
    }

    public static void delete(File file) throws IOException {
//        File file = new File(file_name);

        if (file.isDirectory()) {

            //directory is empty, then delete it
            if (file.list().length == 0) {

                file.delete();
//                System.out.println("Directory is deleted : "
//                        + file.getAbsolutePath());

            } else {

                //list all the directory contents
                String files[] = file.list();

                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);

                    //recursive delete
                    delete(fileDelete);
                }

                //check the directory again, if empty then delete it
                if (file.list().length == 0) {
                    file.delete();
//                    System.out.println("Directory is deleted : "
//                            + file.getAbsolutePath());
                }
            }

        } else {
            //if file, then delete it
            file.delete();
//            System.out.println("File is deleted : " + file.getAbsolutePath());
        }
    }
}
