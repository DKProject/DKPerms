/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 24.05.20, 20:47
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.logging.record;

import java.util.List;

public interface LogRecordResult {

    List<LogRecord> getAll();

    List<LogRecord> get(int page, int pageSize);

}
