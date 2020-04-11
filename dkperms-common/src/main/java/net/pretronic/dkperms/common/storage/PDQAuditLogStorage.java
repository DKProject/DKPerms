/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 15.03.20, 14:59
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.storage;

import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.dkperms.api.logging.LogAction;
import net.pretronic.dkperms.api.logging.LogType;
import net.pretronic.dkperms.api.storage.AuditLogStorage;

public class PDQAuditLogStorage implements AuditLogStorage {

    private DatabaseCollection auditCollection;

    @Override
    public void createRecord(int executorId, long timeStamp, LogType type, LogAction action, int ownerId, int scopeId, String field, Object oldValue, Object newValue) {
        auditCollection.insert()
                .set("ExecutorId",executorId)
                .set("Time",timeStamp)
                .set("Type",type)
                .set("Action",action)
                .set("OwnerId",ownerId)
                .set("ScopeId",scopeId)
                .set("Field",field)
                .set("OldValue",oldValue)
                .set("NewValue",newValue)
                .execute();
    }

    public void setCollections(DatabaseCollection auditCollection){
        this.auditCollection = auditCollection;
    }
}
