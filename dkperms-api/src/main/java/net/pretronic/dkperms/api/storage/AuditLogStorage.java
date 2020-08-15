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

    void createRecord(long timeStamp,int executorId, LogType type, LogAction action, int ownerId, int keyId
                        ,String field, String oldValue, String newValue,String data);

}
