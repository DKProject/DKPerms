/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.storage;

import net.pretronic.dkperms.api.context.PermissionContext;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.scope.PermissionScope;

import java.util.Collection;
import java.util.List;

public interface DKPermsStorage {

    String getName();

    ScopeStorage getScopeStorage();

    ContextStorage getContextStorage();

    ObjectStorage getObjectStorage();


    boolean connect();

    boolean isConnected();

    boolean disconnect();
}
