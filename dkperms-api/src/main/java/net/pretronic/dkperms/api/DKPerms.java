/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api;

import net.pretronic.dkperms.api.logging.AuditLog;
import net.pretronic.dkperms.api.migration.MigrationAssistant;
import net.pretronic.dkperms.api.object.PermissionObjectManager;
import net.pretronic.dkperms.api.permission.analyse.PermissionAnalyser;
import net.pretronic.dkperms.api.scope.PermissionScopeManager;
import net.pretronic.dkperms.api.storage.DKPermsStorage;
import net.pretronic.libraries.event.EventBus;
import net.pretronic.libraries.logging.PretronicLogger;
import net.pretronic.libraries.utility.concurrent.AsyncExecutor;

public abstract class DKPerms {

    private static DKPerms INSTANCE;

    public abstract String getVersion();

    public abstract int getBuild();


    public abstract PretronicLogger getLogger();

    public abstract DKPermsStorage getStorage();

    public abstract MigrationAssistant getMigrationAssistant();

    public abstract AuditLog getAuditLog();

    public abstract PermissionScopeManager getScopeManager();

    public abstract PermissionObjectManager getObjectManager();

    public abstract DKPermsFormatter getFormatter();

    public abstract PermissionAnalyser getAnalyser();


    public abstract EventBus getEventBus();

    public abstract AsyncExecutor getExecutor();

    public static DKPerms getInstance(){
        return INSTANCE;
    }

    public static void setInstance(DKPerms instance){
        if(INSTANCE != null && instance != null) throw new IllegalArgumentException("DKPerms instance is already set.");
        INSTANCE = instance;
    }
}
