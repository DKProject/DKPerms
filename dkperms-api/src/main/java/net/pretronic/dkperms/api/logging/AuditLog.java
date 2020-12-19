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

public interface AuditLog {

    LogRecordResult get();

    LogRecordResult get(long from, long to);

    LogRecordResult getRecent(int last);

    LogRecordResult getRecent(long time);


    void clean(long beforeTimestamp);


    //LogId | Time | ExecutorId | Type | Action | ObjectId | ScopeId | Field | OldValue | NewValue

    void createRecord(PermissionObject executor, LogType type, LogAction action, PermissionObject owner,Loggable object, String field,Object oldValue,Object newValue);

    void createRecordAsync(PermissionObject executor, LogType type, LogAction action, PermissionObject owner,Loggable object, String field,Object oldValue,Object newValue);


    default void createUpdateRecordAsync(PermissionObject executor, LogType type, PermissionObject owner,Loggable object, String field,Object oldValue,Object newValue){
        createRecordAsync(executor,type,LogAction.UPDATE,owner,object,field,oldValue,newValue);
    }

    default void createCreateRecordAsync(PermissionObject executor, LogType type, PermissionObject owner,Loggable object){
        createRecordAsync(executor,type,LogAction.CREATE,owner,object,null,null,null);
    }

    default void createDeleteRecordAsync(PermissionObject executor, LogType type, PermissionObject owner,Loggable object){
        createRecordAsync(executor,type,LogAction.CREATE,owner,object,null,null,null);
    }

}
