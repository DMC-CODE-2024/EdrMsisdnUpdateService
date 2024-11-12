
package com.gl;

import com.gl.Config.MySQLConnection;
import com.gl.Config.PropertyReader;
import com.gl.P5Process.ProcessMsisdnUpdate;
import com.gl.P5Process.QueryExecuter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Application {
    static Logger log = LogManager.getLogger(QueryExecuter.class);
    public static PropertyReader propertyReader = new PropertyReader();
    public static String appDb;
    public static String audDb;
    public static String edrDb;
    public static String serverName;


    public static void main(String[] args) {
        try {
            appDb = propertyReader.getConfigPropValue("appdbName");
            audDb = propertyReader.getConfigPropValue("auddbName");
            edrDb = propertyReader.getConfigPropValue("edrappdbName");
            serverName = propertyReader.getConfigPropValue("serverName");
        } catch (Exception e) {
            log.error("Not able to fetch information " + e);
        }
        ProcessMsisdnUpdate.p5(new MySQLConnection().getConnection());
        System.exit(0);
    }
}

