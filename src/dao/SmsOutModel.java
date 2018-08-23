/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Timestamp;

/**
 *
 * @author TrungTD
 */
public class SmsOutModel {

    private String phone_number;
    private String service_number;
    private String command_name;
    private Timestamp date_time_process;

    public String getCommand_name() {
        return command_name;
    }

    public void setCommand_name(String command_name) {
        this.command_name = command_name;
    }

    public Timestamp getDate_time_process() {
        return date_time_process;
    }

    public void setDate_time_process(Timestamp date_time_process) {
        this.date_time_process = date_time_process;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getService_number() {
        return service_number;
    }

    public void setService_number(String service_number) {
        this.service_number = service_number;
    }
}
