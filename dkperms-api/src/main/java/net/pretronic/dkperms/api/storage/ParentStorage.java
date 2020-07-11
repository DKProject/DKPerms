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

import net.pretronic.dkperms.api.entity.ParentEntity;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;

import java.util.Collection;

public interface ParentStorage {

    Collection<ParentEntity> getParentReferences(PermissionObject object, PermissionScope scope);

    ScopeBasedDataList<ParentEntity> getParentReferences(PermissionObject object, Collection<PermissionScope> scope);

    ScopeBasedDataList<ParentEntity> getAllParentReferences(PermissionObject object, Collection<PermissionScope> skipped);

    int createParentReference(int objectId, int scopeId, int ParentId, PermissionAction action, long timeout);

    void removeParentReference(int entityId);

    void clearParentReferences(int objectId);

    void clearParentReferences(int objectId, int scopeId);

    void updateParentReference(int entityId, int scopeId,PermissionAction action, long timeout);

    void updateParentReferenceScope(int entityId, int scopeId);

    void updateParentReferenceAction(int entityId, PermissionAction action);

    void updateParentReferenceTimeout(int entityId,long timeout);

    void deleteTimedOutParentReferences();
}
