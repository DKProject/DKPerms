/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.backup;

import net.pretronic.dkperms.api.object.group.PermissionGroupOrder;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.scope.PermissionScope;

import java.io.File;
import java.util.Collection;

public interface Backup {

    BackupMeta getMeta();

    PermissionScope getScopes();

    Collection<PermissionGroupOrder> getOrders();

    Collection<PermissionObject> getObjects();

    void save(File location);

    String download();

    //logs?



}
