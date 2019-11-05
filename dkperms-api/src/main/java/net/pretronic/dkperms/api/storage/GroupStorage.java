/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 05.11.19, 18:36
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.storage;

import net.pretronic.dkperms.api.entity.PermissionGroupEntity;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;

import java.util.Collection;
import java.util.Map;

public interface GroupStorage {



    Collection<PermissionGroupEntity> getEntities(int objectId, PermissionScope scope);

    Map<PermissionScope,Collection<PermissionGroupEntity>> getEntities(int objectId, Collection<PermissionScope> scope);


    int addGroup(int scopeId, int groupId, PermissionAction action, long timeout);

    void removeGroup(int entityId);

    void clearGroups(int objectId);

    void clearGroups(int objectId, int scopeId);

}
