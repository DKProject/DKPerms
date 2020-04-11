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
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;

import java.util.Collection;
import java.util.Map;

public interface GroupStorage {

    Collection<PermissionGroupEntity> getGroupReferences(int objectId, PermissionScope scope);

    ScopeBasedDataList<PermissionGroupEntity> getGroupReferences(int objectId, Collection<PermissionScope> scope);

    ScopeBasedDataList<PermissionGroupEntity> getAllGroupReferences(int objectId, Collection<PermissionScope> skipped);

    int createGroupReference(int objectId, int scopeId, int groupId, PermissionAction action, long timeout);

    void removeGroupReference(int entityId);

    void clearGroupReferences(int objectId);

    void clearGroupReferences(int objectId, int scopeId);

}
