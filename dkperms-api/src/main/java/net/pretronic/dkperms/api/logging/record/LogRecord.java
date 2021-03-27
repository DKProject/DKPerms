/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 14.03.20, 15:01
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.logging.record;

import net.pretronic.dkperms.api.logging.LogAction;
import net.pretronic.dkperms.api.logging.LogType;
import net.pretronic.dkperms.api.object.PermissionObject;

public interface LogRecord {

    int getId();

    int getExecutorId();

    PermissionObject getExecutor();

    long getTime();

    LogType getType();

    LogAction getAction();

    int getOwnerId();

    PermissionObject getOwner();


    String getField();

    int getKey();

    Object getOldValue();

    Object getNewValue();





    /*
    ExecutorObjectId | Time | Action | ObjectId | ScopeId | Collection | Field | PrimaryKey | OldValue | NewValue

    65567 | 10:00 | CREATE | 973939 | 1 | DKPerms_Object | Deleted | 973939 | 1 | 0 |
    65567 | 10:00 | CREATE | 973939 | 1 | DKPerms_Object | Name | 973939 | Dkrieger | Dkrieger |

    65567 | 10:10 | CREATE | 973939 | 1 | DKPerms_Permissions | Permission | 777 | null | test.test |
    65567 | 10:10 | CREATE | 973939 | 1 | DKPerms_Permissions | Timeout | 777 | null | test.test |
    65567 | 10:10 | CREATE | 973969 | 1 | DKPerms_Permissions | Permission | 777 | null | test.test |
    65567 | 10:10 | CREATE | 9739t39 | 1 | DKPerms_Permissions | Permission | 777 | null | test.test |
    65567 | 10:10 | CREATE | 973t939 | 1 | DKPerms_Permissions | Permission | 777 | null | test.test |
    65567 | 10:10 | CREATE | 973t939 | 1 | DKPerms_Permissions | Permission | 777 | null | test.test |



    65567 | 10:10 | CREATE | DKPerms_Object | Name | 973939 | null | Dkrieger |
    65567 | 10:10 | CREATE | DKPerms_Object | Disabled | 973939 | null | false |
    65567 | 10:12 | ENTITY_ADD | DKPerms_Permission | Permission | 974539 | null | false |



    OBJECT -> Executor | Time | Type | ScopeName | ScopeId | ObjectName | ObjectId | ObjectAssignmentId | ObjectTypeName | ObjectTypeId
    ENTITY -> Executor | Time | Type |

     */


}
