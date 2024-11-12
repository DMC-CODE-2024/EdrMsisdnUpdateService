//package com.gl.P5Process;
//
//import com.gl.AlertAudit.AlertService;
//import com.gl.AlertAudit.ModuleAuditTrail;
//import com.gl.Config.PropertyReader;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.io.FileWriter;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.Statement;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//
//import static com.gl.P5Process.QueryExecuter.runQuery;
//
//public class Process {
//    static Logger log = LogManager.getLogger(Process.class);
//    public static PropertyReader propertyReader = new PropertyReader();
//
//    public static void p5(Connection conn) {
//        var insertedKey = new ModuleAuditTrail().insertModuleAudit("EDR", "EdrUpdateMsisdn", conn);
//        long executionStartTime = new Date().getTime();
//        int total = 0;
//        String dbname = null;
//        try {
//            dbname = propertyReader.getConfigPropValue("appDbName");
//        } catch (Exception e) {
//            log.info("Not able to get details  :" + e);
//        }
//        for (String table : List.of(dbname + ".active_unique_imei", dbname + ".active_unique_foreign_imei", dbname + ".active_imei_with_different_imsi", dbname + ".active_foreign_imei_with_different_imsi")) {
//            total += updateDb(conn, table);
//            generateAlertAndWriteInFile(conn, table);
//        }
//        new ModuleAuditTrail().updateModuleAudit(200, "Success", "", insertedKey, executionStartTime, total, conn, 4);
//    }
//
//    private static void generateAlertAndWriteInFile(Connection conn, String table) {
//        var a = getMsisdnNullCount(conn, table);
//        if (a != 0) {
//            log.info("Still blank msisdn left:" + a);
//            raiseAlert(conn, table, a);
//            writeInFile(conn, table, a);
//        }
//    }
//
//    private static void writeInFile(Connection conn, String table, int a) {
//        var f = getSystemConfigDetailsByTag(conn, "EdrMissingMsisdnImsiLocation");
//        try {
//            Files.createDirectories(Paths.get(f));
//        } catch (Exception e) {
//        }
//        String sdfTime = new SimpleDateFormat("yyyyMMdd").format(new Date());
//        var filpath = f + "/" + table + "_" + sdfTime + ".txt";
//        String query = " select imsi from " + table + " where ( msisdn ='' or msisdn is null)  ";
//        log.info("File Location {}", filpath);
//        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query); FileWriter fw = new FileWriter(filpath)) {
//            while (rs.next()) {
//                fw.write(rs.getString("imsi"));
//                fw.write("\n");
//            }
//        } catch (Exception e) {
//            log.error(e + "Not able to write in File:" + query);
//        }
//    }
//
//    private static void raiseAlert(Connection conn, String table, int a) {
//        new AlertService().raiseAnAlert("alert1793", "Unable to find " + a + " msisdn for " + table, "EdrUpdateMsisdnProcess ", 0, conn);
//    }
//
//    private static int getMsisdnNullCount(Connection conn, String table) {
//        String query = "select count(*) as count from " + table + " where (msisdn ='' or msisdn is null)  ";
//        log.info(query);
//        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
//            while (rs.next()) {
//                return rs.getInt("count");
//            }
//        } catch (Exception e) {
//            log.error(Thread.currentThread().getStackTrace()[0].getMethodName() + " : Unable to run query: " + e.getLocalizedMessage() + " [Query] :" + query);
//            new AlertService().raiseAnAlert("alert1769", "Not able to get " , "EdrUpdateMsisdnProcess ", 0, conn);
//            return 0;
//        }
//        return 0;
//    }
//
//    private static int updateDb(Connection conn, String table) {
//        var q = " UPDATE  " + table + " a JOIN app.active_msisdn_list b ON b.imsi = a.imsi SET a.msisdn = b.msisdn WHERE  (a.msisdn = '' or a.msisdn is null )  ";
//        return runQuery(conn, q);
//    }
//
//    public static String getSystemConfigDetailsByTag(Connection conn, String tag) {
//        String query = "select value from  sys_param where tag='" + tag + "'";
//        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query);) {
//            while (rs.next()) {
//                return rs.getString("value");
//            }
//        } catch (Exception e) {
//            log.error(e);
//        }
//        return null;
//    }
//}
