/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 10.11.19, 18:59
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.cache;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;

import java.util.Collection;
import java.util.Map;

public class PermissionCache extends ScopeBasedCache<PermissionEntity> {

    private final PermissionObject object;

    public PermissionCache(PermissionObject object) {
        this.object = object;
    }

    @Override
    protected Collection<PermissionEntity> load(PermissionScope scope) {
        return DKPerms.getInstance().getStorage().getPermissionStorage().getPermissions(object,scope);
    }

    @Override
    protected ScopeBasedDataList<PermissionEntity> load(Collection<PermissionScope> scopes) {
        return DKPerms.getInstance().getStorage().getPermissionStorage().getPermissions(object,scopes);
    }

    @Override
    protected ScopeBasedDataList<PermissionEntity> loadAll(Collection<PermissionScope> scopes) {
        return DKPerms.getInstance().getStorage().getPermissionStorage().getAllPermissions(object,scopes);
    }
}
