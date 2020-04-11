/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 05.11.19, 18:36
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.storage;

import net.pretronic.dkperms.api.logging.LogAction;
import net.pretronic.dkperms.api.logging.LogType;

public interface AuditLogStorage {

    void createRecord(int executorId, long timeStamp, LogType type, LogAction action, int ownerId
            , int scopeId , String field, Object oldValue, Object newValue);

}
