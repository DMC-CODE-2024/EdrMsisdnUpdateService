package com.gl.P5Process;


import com.gl.Application;

import java.sql.Connection;
import java.util.List;


public class ProcessMsisdnUpdate {

    public static void p5(Connection conn) {
        for (String table : List.<String>of(Application.edrDb + ".active_unique_imei", Application.edrDb + ".active_imei_with_different_imsi"))
            updateActiveTableMsisdn(conn, table);
    }

    private static void updateActiveTableMsisdn(Connection conn, String table) {
        String q = "UPDATE " + table + " aui JOIN  " + Application.appDb + ".active_msisdn_list aml ON aui.imsi = aml.imsi SET aui.msisdn = aml.msisdn WHERE (aui.msisdn IS NULL OR aui.msisdn = '') ";
        QueryExecuter.runQuery(conn, q);
    }
}
