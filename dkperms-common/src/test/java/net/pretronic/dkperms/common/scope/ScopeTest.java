/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.scope;

import net.prematic.databasequery.api.DatabaseDriver;
import net.prematic.databasequery.sql.SqlDatabaseDriverConfig;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseDriver;
import net.prematic.libraries.logging.PrematicLoggerFactory;
import net.prematic.libraries.logging.SimplePrematicLogger;
import net.pretronic.dkperms.api.scope.PermissionScopeManager;
import net.pretronic.dkperms.common.storage.PDQStorage;
import org.slf4j.impl.StaticLoggerBinder;

public class ScopeTest {

    public static void main(String[] args) throws Exception{
        SimplePrematicLogger logger = new SimplePrematicLogger();
        StaticLoggerBinder.getSingleton().setLogger(logger);

        SqlDatabaseDriverConfig config = new SqlDatabaseDriverConfig(MySqlDatabaseDriver.class)
                .setHost("localhost")
                .setPort(3306)
                .setUsername("root")
                .getDataSourceConfig().setClassName("com.zaxxer.hikari.HikariDataSource")
                .out();
        config.setMultipleDatabaseConnectionsAble(true);

        DatabaseDriver driver = config.createDatabaseDriver("DKPerms",logger);
        driver.registerDefaultAdapters();
        PDQStorage storage = new PDQStorage(driver,driver.getDatabase("DKPermsV2"));
        storage.connect();


        DefaultPermissionScopeManager scopeManager = new DefaultPermissionScopeManager(storage);
        scopeManager.initialise();

        System.out.println(scopeManager.getRoot().getChild("Namespace","Test").isSaved());



        Thread.sleep(2000);

        /*

        - SCOPE xv
            - test -> S5
        - SCOPE bd
            -test  -> S3
            SCOPE dsfsd
             -test -> S7

         */
    }

}
