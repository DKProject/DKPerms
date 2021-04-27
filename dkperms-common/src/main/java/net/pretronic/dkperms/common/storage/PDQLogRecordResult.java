/*
 * (C) Copyright 2021 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 21.04.21, 15:37
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.storage;

import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.dkperms.api.logging.LogAction;
import net.pretronic.dkperms.api.logging.LogType;
import net.pretronic.dkperms.api.logging.record.LogRecord;
import net.pretronic.dkperms.api.logging.record.LogRecordResult;
import net.pretronic.dkperms.common.logging.DefaultLogRecord;

import java.util.ArrayList;
import java.util.List;

public class PDQLogRecordResult implements LogRecordResult {

    private final DatabaseCollection logRecords;

    public PDQLogRecordResult(DatabaseCollection logRecords) {
        this.logRecords = logRecords;
    }

    @Override
    public List<LogRecord> getAll() {
        return null;
    }

    @Override
    public List<LogRecord> get(int page, int pageSize) {
        List<LogRecord> records = new ArrayList<>();
        logRecords.find().page(page,pageSize)
                .execute().loadIn(records, entry -> new DefaultLogRecord(entry.getInt("Id")
                        ,entry.getInt("ExecutorId")
                        ,entry.getLong("Time")
                        ,LogType.valueOf(entry.getString("Type"))
                        ,LogAction.valueOf(entry.getString("Action"))
                        ,entry.getInt("OwnerId")
                        ,entry.getInt("ObjectId")
                       ,entry.getString("Field")));
        return records;
    }
}
