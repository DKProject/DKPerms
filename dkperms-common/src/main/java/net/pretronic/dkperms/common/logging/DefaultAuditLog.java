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

public class DefaultAuditLog implements AuditLog {

    private final boolean enabled;

    public DefaultAuditLog(boolean enabled) {
        this.enabled = enabled;
    }

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
    public void clean(long beforeTimestamp) {

    }

    @Override
    public void createRecord(PermissionObject executor, LogType type, LogAction action, int owner, int key, String field, Object oldValue, Object newValue, Object data) {
        if(enabled){
            try{
                DKPerms.getInstance().getStorage().getAuditLogStorage()
                        .createRecord(System.currentTimeMillis()
                                ,executor.getId(),type,action,owner,key,field
                                ,oldValue != null ? oldValue.toString() : null
                                ,newValue != null ? newValue.toString() : null
                                ,data != null ? data.toString() : null);
            }catch (Exception e){
                System.out.println("Audit error:");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void createRecordAsync(PermissionObject executor, LogType type, LogAction action, int owner, int key, String field, Object oldValue, Object newValue, Object data) {
        if(enabled){
            DKPerms.getInstance().getExecutor().executeVoid(() -> createRecord(executor, type, action, owner, key, field, oldValue, newValue, data));
        }
    }
}

