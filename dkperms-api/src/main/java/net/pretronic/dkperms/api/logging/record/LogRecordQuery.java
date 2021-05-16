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

import net.pretronic.dkperms.api.logging.LogType;
import net.pretronic.dkperms.api.object.PermissionObject;

import javax.swing.*;

public interface LogRecordQuery {

    LogRecordQuery actions(Action... action);

    LogRecordQuery types(LogType... types);


    LogRecordQuery executor(PermissionObject object);

    LogRecordQuery owner(PermissionObject object);

    LogRecordQuery object(Object object);


    LogRecordQuery field(String name);

    LogRecordQuery oldValue(Object value);

    LogRecordQuery newValue(Object value);


    LogRecordQuery from(long start);

    LogRecordQuery to(long end);


    LogRecordResult execute();

}
