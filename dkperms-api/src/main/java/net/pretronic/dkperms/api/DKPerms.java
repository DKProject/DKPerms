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

import net.pretronic.dkperms.api.backup.BackupManager;
import net.pretronic.dkperms.api.context.PermissionContextManager;
import net.pretronic.dkperms.api.object.PermissionObjectManager;
import net.pretronic.dkperms.api.permission.analyse.PermissionAnalyse;
import net.pretronic.dkperms.api.scope.PermissionScope;

public interface DKPerms {

    String getVersion();

    int getBuild();

    PermissionScope getScopeManager();

    PermissionContextManager getContextManager();

    PermissionObjectManager getObjectManager();

    BackupManager getBackupManager();

    PermissionAnalyse startAnalyse();

    boolean synchronize();

    static DKPerms getInstance(){
        return null;
    }
}
