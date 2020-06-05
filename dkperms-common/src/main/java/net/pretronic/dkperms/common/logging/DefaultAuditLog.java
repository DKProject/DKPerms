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
import net.pretronic.dkperms.api.logging.record.LogRecordResult;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.scope.PermissionScope;

public class DefaultAuditLog implements AuditLog {


    @Override
    public LogRecordResult get() {
        return null;
    }

    @Override
    public LogRecordResult get(long from, long to) {
        return null;
    }

    @Override
    public LogRecordResult getRecent(int last) {
        return null;
    }

    @Override
    public LogRecordResult getRecent(long time) {
        return null;
    }

    @Override
    public void recover(LogType type, int key) {

    }

    @Override
    public void rollback(long time) {

    }

    @Override
    public void createRecord(PermissionObject executor, LogType type, LogAction action, int owner, int key, String field, Object oldValue, Object newValue, Object data) {

    }

    @Override
    public void createRecordAsync(PermissionObject executor, LogType type, LogAction action, int owner, int key, String field, Object oldValue, Object newValue, Object data) {

    }
}

