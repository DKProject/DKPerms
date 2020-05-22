/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 14.03.20, 15:56
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.logging;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.logging.AuditLog;
import net.pretronic.dkperms.api.logging.LogAction;
import net.pretronic.dkperms.api.logging.LogType;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.scope.PermissionScope;

public class DefaultAuditLog implements AuditLog {

    @Override
    public void createRecord(PermissionObject executor, LogType type, LogAction action
            ,PermissionObject owner, PermissionScope scope, String field, Object oldValue, Object newValue) {

        DKPerms.getInstance().getStorage().getAuditLogStorage().createRecord(
                executor.getId(),System.currentTimeMillis(),type,action,owner.getId()
                ,scope.getId(),field,oldValue,newValue);
    }
}

