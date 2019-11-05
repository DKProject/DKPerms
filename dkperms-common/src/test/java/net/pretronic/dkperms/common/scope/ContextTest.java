/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 02.11.19, 13:02
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.scope;

import net.prematic.databasequery.api.DatabaseDriver;
import net.prematic.databasequery.sql.SqlDatabaseDriverConfig;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseDriver;
import net.prematic.libraries.logging.SimplePrematicLogger;
import net.pretronic.dkperms.common.context.DefaultPermissionContextManager;
import net.pretronic.dkperms.common.storage.PDQStorage;
import org.slf4j.impl.StaticLoggerBinder;

public class ContextTest {

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


        DefaultPermissionContextManager contextManager = new DefaultPermissionContextManager();

        System.out.println(contextManager.getOrCreateContext("Test").getId());

        System.out.println(contextManager.hasContext("Test"));


        Thread.sleep(2000);
    }

}
