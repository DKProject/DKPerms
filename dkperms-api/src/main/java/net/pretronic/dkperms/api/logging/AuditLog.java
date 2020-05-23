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

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.scope.PermissionScope;

public interface AuditLog {

    //ExecutorObjectId | Time | Action | ObjectId | ScopeId | Collection | Field | PrimaryKey | OldValue | NewValue | restore

    //ExecutorObjectId | (ExecutorObjectName) | Time | Action | ObjectId | ScopeId | Type | Field | PrimaryKey | OldValue | NewValue  | ObjectData | ValueData
    void createRecord(PermissionObject executor, LogType type, LogAction action, PermissionObject owner,Object key, String field,Object oldValue, Object newValue, Object data);

}
