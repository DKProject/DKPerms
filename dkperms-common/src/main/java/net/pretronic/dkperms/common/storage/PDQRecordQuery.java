/*
 * (C) Copyright 2021 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 21.04.21, 15:45
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.storage;

import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.dkperms.api.logging.LogType;
import net.pretronic.dkperms.api.logging.record.LogRecordQuery;
import net.pretronic.dkperms.api.logging.record.LogRecordResult;
import net.pretronic.dkperms.api.object.PermissionObject;

import javax.swing.*;

public class PDQRecordQuery implements LogRecordQuery {

    private final DatabaseCollection collection;

    public PDQRecordQuery(DatabaseCollection collection) {
        this.collection = collection;
    }

    @Override
    public LogRecordQuery actions(Action... action) {
        return null;
    }

    @Override
    public LogRecordQuery types(LogType... types) {
        return null;
    }

    @Override
    public LogRecordQuery executor(PermissionObject object) {
        return null;
    }

    @Override
    public LogRecordQuery owner(PermissionObject object) {
        return null;
    }

    @Override
    public LogRecordQuery object(Object object) {
        return null;
    }

    @Override
    public LogRecordQuery field(String name) {
        return null;
    }

    @Override
    public LogRecordQuery oldValue(Object value) {
        return null;
    }

    @Override
    public LogRecordQuery newValue(Object value) {
        return null;
    }

    @Override
    public LogRecordQuery from(long start) {
        return null;
    }

    @Override
    public LogRecordQuery to(long end) {
        return null;
    }

    @Override
    public LogRecordResult execute() {
        return new PDQLogRecordResult(collection);
    }
}
