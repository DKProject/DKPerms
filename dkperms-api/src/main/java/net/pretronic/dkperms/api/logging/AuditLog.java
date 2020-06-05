/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 14.03.20, 14:14
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.logging;

import net.pretronic.dkperms.api.logging.record.LogRecordResult;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.scope.PermissionScope;

public interface AuditLog {

    LogRecordResult get();

    LogRecordResult get(long from, long to);

    LogRecordResult getRecent(int last);

    LogRecordResult getRecent(long time);


    void recover(LogType type,int key);

    void rollback(long time);

    //LogId | Time | ExecutorId | Type | Action | ObjectId | ScopeId | Field | OldValue | NewValue

    void createRecord(PermissionObject executor, LogType type, LogAction action, int owner,int key, String field,Object oldValue,Object newValue, Object data);

    void createRecordAsync(PermissionObject executor, LogType type, LogAction action, int owner,int key, String field,Object oldValue,Object newValue,Object data);

    //LogId | Time | ExecutorId | Type | DELETE | ObjectId | Id | Name | Hallo | "Test"
    //LogId | Time | ExecutorId | Type | DELETE | ObjectId | Id | Disabled | Hallo | "Test"
    //LogId | Time | ExecutorId | Type | UPDATE | ObjectId | Id | Name | Hallo | "Test"
    //LogId | Time | ExecutorId | Type | CREATE | ObjectId | Id | Name | null | "Hallo"


}
