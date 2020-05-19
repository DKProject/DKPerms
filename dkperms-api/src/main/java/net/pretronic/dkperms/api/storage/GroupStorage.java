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

import net.pretronic.dkperms.api.entity.PermissionParentEntity;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;

import java.util.Collection;

public interface GroupStorage {

    Collection<PermissionParentEntity> getGroupReferences(PermissionObject object, PermissionScope scope);

    ScopeBasedDataList<PermissionParentEntity> getGroupReferences(PermissionObject object, Collection<PermissionScope> scope);

    ScopeBasedDataList<PermissionParentEntity> getAllGroupReferences(PermissionObject object, Collection<PermissionScope> skipped);

    int createGroupReference(int objectId, int scopeId, int groupId, PermissionAction action, long timeout);

    void removeGroupReference(int entityId);

    void clearGroupReferences(int objectId);

    void clearGroupReferences(int objectId, int scopeId);

    void updateGroupReference(int entityId, int scopeId,PermissionAction action, long timeout);

    void updateGroupReferenceScope(int entityId, int scopeId);

    void updateGroupReferenceAction(int entityId, PermissionAction action);

    void updateGroupReferenceTimeout(int entityId,long timeout);

    void deleteTimedOutGroupReferences();
}
