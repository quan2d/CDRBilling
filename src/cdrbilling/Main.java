/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cdrbilling;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author TrungTD
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String CHANNEL = "VOUCHER";
        if (!CHANNEL.toUpperCase().trim().equals("SMS")
                && !CHANNEL.toUpperCase().trim().equals("IVR")
                && !CHANNEL.toUpperCase().trim().equals("WEB")
                && !CHANNEL.toUpperCase().trim().equals("WAP")
                && !CHANNEL.toUpperCase().trim().equals("USSD")
                && !CHANNEL.toUpperCase().trim().equals("CLIENT")
                && !CHANNEL.toUpperCase().trim().equals("UNSUB")
                && !CHANNEL.toUpperCase().trim().equals("CSKH")
                && !CHANNEL.toUpperCase().trim().equals("MAXRETRY")
                && !CHANNEL.toUpperCase().trim().equals("SUBNOTEXIST")
                && !CHANNEL.toUpperCase().trim().equals("SYSTEM")) {

//                # set CHANNEL = "API"
            CHANNEL = "API";
        }
        System.out.println("CHANNEL: "+CHANNEL);
    }

    public static void main1(String[] args) {
        CDRBilling smsOut = new CDRBilling();
        smsOut.start();
//        smsOut.stop();
    }
}
