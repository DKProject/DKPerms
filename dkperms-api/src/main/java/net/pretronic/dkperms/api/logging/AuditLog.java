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

    void createRecord(PermissionObject executor, LogType type, LogAction action, int owner,int key, String field,Object oldValue,Object newValue, Object data);

    void createRecordAsync(PermissionObject executor, LogType type, LogAction action, int owner,int key, String field,Object oldValue,Object newValue,Object data);


    default void createUpdateRecordAsync(PermissionObject executor, LogType type, int owner,int key, String field,Object oldValue,Object newValue,Object data){
        createRecordAsync(executor,type,LogAction.UPDATE,owner,key,field,oldValue,newValue,data);
    }

    default void createCreateRecordAsync(PermissionObject executor, LogType type, int owner,int key,Object data){
        createRecordAsync(executor,type,LogAction.CREATE,owner,key,null,null,null,data);
    }

    default void createDeleteRecordAsync(PermissionObject executor, LogType type, int owner,int key,Object data){
        createRecordAsync(executor,type,LogAction.CREATE,owner,key,null,null,null,data);
    }





    //LogId | Time | ExecutorId | Type | DELETE | ObjectId | Id | Name | Hallo | "Test"
    //LogId | Time | ExecutorId | Type | DELETE | ObjectId | Id | Disabled | Hallo | "Test"
    //LogId | Time | ExecutorId | Type | UPDATE | ObjectId | Id | Name | Hallo | "Test"
    //LogId | Time | ExecutorId | Type | CREATE | ObjectId | Id | Name | null | "Hallo"


}
