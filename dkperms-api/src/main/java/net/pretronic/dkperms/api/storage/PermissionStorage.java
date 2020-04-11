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

import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.entity.PermissionGroupEntity;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;

import java.util.Collection;

public interface PermissionStorage {

    Collection<PermissionEntity> getPermissions(int objectId,PermissionScope scope);

    ScopeBasedDataList<PermissionEntity> getPermissions(int objectId, Collection<PermissionScope> scope);

    ScopeBasedDataList<PermissionEntity> getAllPermissions(int objectId, Collection<PermissionScope> skipped);

    int addPermission(int objectId, int scopeId, String permission, PermissionAction action, long timeout);

    void deletePermissions(int entityId);

    void clearPermissions(int objectId,int scopeId);

    void clearPermissions(int objectId);

}
