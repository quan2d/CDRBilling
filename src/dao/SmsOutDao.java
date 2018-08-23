/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import common.jdbc.JdbcConnectionPool;
import common.jdbc.core.RowMapper;
import common.jdbc.core.simple.SimpleJdbcDaoSupport;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author TrungTD
 */
public class SmsOutDao extends SimpleJdbcDaoSupport {

    public SmsOutDao() {
        super();
    }

    public SmsOutDao(JdbcConnectionPool pool) {
        super(pool);
    }

    private static class SmsOutMapper implements RowMapper {

        public Object mapRow(ResultSet rs, int i) throws SQLException {
            SmsOutModel smsOut = new SmsOutModel();
            smsOut.setPhone_number(rs.getString("phone_number"));
            smsOut.setService_number(rs.getString("service_number"));
            smsOut.setCommand_name(rs.getString("command_name"));
            smsOut.setDate_time_process(rs.getTimestamp("date_time_process"));
            return smsOut;
        }
    }

    public List<SmsOutModel> getSmsOutFail(String begin_time, String end_time) {
        String query = "Call get_sms_out_fail('" + begin_time + "','" + end_time + "')";
        System.out.println(query);
        try {
            return getSimpleJdbcTemplate().query(query, new SmsOutMapper());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
