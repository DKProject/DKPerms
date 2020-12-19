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
import net.pretronic.dkperms.api.logging.Loggable;
import net.pretronic.dkperms.api.logging.record.LogRecordResult;
import net.pretronic.dkperms.api.object.PermissionObject;

import java.util.Locale;

public class DefaultAuditLog implements AuditLog {

    private final boolean enabled;

    public DefaultAuditLog(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public LogRecordResult get() {
        throw new UnsupportedOperationException();
    }

    @Override
    public LogRecordResult get(long from, long to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LogRecordResult getRecent(int last) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LogRecordResult getRecent(long time) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clean(long beforeTimestamp) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createRecord(PermissionObject executor, LogType type, LogAction action, PermissionObject owner, Loggable object, String field, Object oldValue, Object newValue) {
        if(enabled && (field == null || !oldValue.equals(newValue))){
            try{
                DKPerms.getInstance().getStorage().getAuditLogStorage().createRecord(
                        System.currentTimeMillis()
                        ,executor.getId()
                        ,type,action
                        ,owner != null ?owner.getId() : null
                        ,object.getId()
                        ,field != null ? field.toLowerCase() : null
                        ,oldValue != null ? oldValue.toString() : null
                        ,newValue != null ? newValue.toString() : null
                        ,object.serializeRecordToString());
            }catch (Exception e){
                e.printStackTrace();
                throw e;
            }
        }
    }

    @Override
    public void createRecordAsync(PermissionObject executor, LogType type, LogAction action, PermissionObject owner, Loggable object, String field, Object oldValue, Object newValue) {
        if(enabled && (field == null || !oldValue.equals(newValue))){
            DKPerms.getInstance().getExecutor().executeVoid(() -> createRecord(executor, type, action, owner, object, field, oldValue, newValue));
        }
    }


}

