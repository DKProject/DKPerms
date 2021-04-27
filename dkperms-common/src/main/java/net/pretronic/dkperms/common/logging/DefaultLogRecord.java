/*
 * (C) Copyright 2021 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 21.04.21, 15:41
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.logging;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.logging.LogAction;
import net.pretronic.dkperms.api.logging.LogType;
import net.pretronic.dkperms.api.logging.record.LogRecord;
import net.pretronic.dkperms.api.object.PermissionObject;

public class DefaultLogRecord implements LogRecord {

    private final int id;
    private final int executorId;
    private final long time;

    private final LogType type;
    private final LogAction action;

    private final int ownerId;
    private final int key;

    private final String field;

    public DefaultLogRecord(int id, int executorId, long time, LogType type, LogAction action, int ownerId, int key, String field) {
        this.id = id;
        this.executorId = executorId;
        this.time = time;
        this.type = type;
        this.action = action;
        this.ownerId = ownerId;
        this.key = key;
        this.field = field;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getExecutorId() {
        return executorId;
    }

    @Override
    public PermissionObject getExecutor() {
        return DKPerms.getInstance().getObjectManager().getObject(executorId);
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public LogType getType() {
        return type;
    }

    @Override
    public LogAction getAction() {
        return action;
    }

    @Override
    public int getOwnerId() {
        return ownerId;
    }

    @Override
    public PermissionObject getOwner() {
        if(ownerId > 0) return DKPerms.getInstance().getObjectManager().getObject(ownerId);
        return null;
    }

    @Override
    public String getField() {
        return field;
    }

    @Override
    public int getKey() {
        return key;
    }

    @Override
    public Object getOldValue() {
        return null;
    }

    @Override
    public Object getNewValue() {
        return null;
    }
}
