/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 03.11.19, 16:01
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common;

import net.prematic.databasequery.api.DatabaseDriver;
import net.prematic.databasequery.sql.SqlDatabaseDriverConfig;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseDriver;
import net.prematic.libraries.logging.PrematicLogger;
import net.prematic.libraries.logging.SimplePrematicLogger;
import net.prematic.libraries.utility.GeneralUtil;
import net.prematic.libraries.utility.concurrent.AsyncExecutor;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.backup.BackupManager;
import net.pretronic.dkperms.api.context.PermissionContextManager;
import net.pretronic.dkperms.api.object.PermissionObjectManager;
import net.pretronic.dkperms.api.permission.analyse.PermissionAnalyse;
import net.pretronic.dkperms.api.scope.PermissionScopeManager;
import net.pretronic.dkperms.api.storage.DKPermsStorage;
import net.pretronic.dkperms.common.context.DefaultPermissionContextManager;
import net.pretronic.dkperms.common.object.DefaultPermissionObjectManager;
import net.pretronic.dkperms.common.scope.DefaultPermissionScopeManager;
import net.pretronic.dkperms.common.storage.PDQStorage;
import org.slf4j.impl.StaticLoggerBinder;

public class DKPermsTest extends DKPerms {

    private final PermissionScopeManager scopeManager;
    private final PermissionContextManager contextManager;
    private final PermissionObjectManager objectManager;

    private final DKPermsStorage storage;
    private final AsyncExecutor executor;

    public DKPermsTest() {
        DKPerms.setInstance(this);

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
        storage = new PDQStorage(driver,driver.getDatabase("DKPermsV2"));
        storage.connect();

        this.scopeManager = new DefaultPermissionScopeManager();
        this.contextManager = new DefaultPermissionContextManager();
        this.objectManager = new DefaultPermissionObjectManager();
        this.executor = new AsyncExecutor(GeneralUtil.getDefaultExecutorService());

        this.scopeManager.loadRootScope();
        ((DefaultPermissionObjectManager)this.objectManager).initialise();
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public int getBuild() {
        return 0;
    }

    @Override
    public PrematicLogger getLogger() {
        return null;
    }

    @Override
    public PermissionScopeManager getScopeManager() {
        return scopeManager;
    }

    @Override
    public PermissionContextManager getContextManager() {
        return contextManager;
    }

    @Override
    public PermissionObjectManager getObjectManager() {
        return objectManager;
    }

    @Override
    public BackupManager getBackupManager() {
        return null;
    }

    @Override
    public DKPermsStorage getStorage() {
        return storage;
    }

    @Override
    public AsyncExecutor getExecutor() {
        return executor;
    }

    @Override
    public PermissionAnalyse startAnalyse() {
        return null;
    }

    @Override
    public boolean synchronize() {
        return false;
    }
}
