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
import net.pretronic.dkperms.api.logging.record.LogRecordQuery;
import net.pretronic.dkperms.api.storage.AuditLogStorage;

public class PDQAuditLogStorage implements AuditLogStorage {

    private DatabaseCollection auditCollection;

    public void setCollections(DatabaseCollection auditCollection){
        this.auditCollection = auditCollection;
    }

    @Override
    public LogRecordQuery createQuery() {
        return new PDQRecordQuery(auditCollection);
    }

    @Override
    public void deleteRecords(long beforeTimestamp) {
        auditCollection.delete()
                .whereLower("Time",beforeTimestamp)
                .execute();
    }

    @Override
    public void createRecord(long timeStamp, int executorId, LogType type, LogAction action, int ownerId, int keyId, String field, String oldValue, String newValue, String data) {
        auditCollection.insert()
                .set("Time",timeStamp)
                .set("ExecutorId",executorId)
                .set("Type",type)
                .set("Action",action)
                .set("OwnerId",ownerId)
                .set("ObjectId",keyId)
                .set("Field",field)
                .set("OldValue",oldValue)
                .set("NewValue",newValue)
                .set("Data",data)
                .execute();
    }
}
