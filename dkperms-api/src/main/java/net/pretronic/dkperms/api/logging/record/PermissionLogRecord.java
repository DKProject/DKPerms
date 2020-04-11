/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 14.03.20, 15:02
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.logging.record;

import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;

public interface PermissionLogRecord extends LogRecord{

    ObjectMetaEntry getPermissionEntry();

}
