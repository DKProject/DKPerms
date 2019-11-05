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

import net.prematic.libraries.logging.PrematicLogger;
import net.prematic.libraries.utility.concurrent.AsyncExecutor;
import net.pretronic.dkperms.api.backup.BackupManager;
import net.pretronic.dkperms.api.context.PermissionContextManager;
import net.pretronic.dkperms.api.object.PermissionObjectManager;
import net.pretronic.dkperms.api.permission.analyse.PermissionAnalyse;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.PermissionScopeManager;
import net.pretronic.dkperms.api.storage.DKPermsStorage;

public abstract class DKPerms {

    private static DKPerms INSTANCE;

    public abstract String getVersion();

    public abstract int getBuild();

    public abstract PrematicLogger getLogger();

    public abstract PermissionScopeManager getScopeManager();

    public abstract PermissionContextManager getContextManager();

    public abstract PermissionObjectManager getObjectManager();

    public abstract BackupManager getBackupManager();

    public abstract DKPermsStorage getStorage();

    public abstract AsyncExecutor getExecutor();

    public abstract PermissionAnalyse startAnalyse();

    public abstract boolean synchronize();

    public static DKPerms getInstance(){
        return INSTANCE;
    }

    public static void setInstance(DKPerms instance){
        if(INSTANCE != null) throw new IllegalArgumentException("DKPerms instance is already set.");
        INSTANCE = instance;
    }
}
