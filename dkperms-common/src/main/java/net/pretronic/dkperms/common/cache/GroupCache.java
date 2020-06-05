/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 09.11.19, 20:14
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.cache;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.entity.ParentEntity;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;

import java.util.Collection;

public final class GroupCache extends ScopeBasedCache<ParentEntity>{

    private final PermissionObject object;

    public GroupCache(PermissionObject object) {
        this.object = object;
    }

    @Override
    protected Collection<ParentEntity> load(PermissionScope scope) {
        return DKPerms.getInstance().getStorage().getParentStorage().getParentReferences(object,scope);
    }

    @Override
    protected ScopeBasedDataList<ParentEntity> load(Collection<PermissionScope> scopes) {
        return DKPerms.getInstance().getStorage().getParentStorage().getParentReferences(object,scopes);
    }

    @Override
    protected ScopeBasedDataList<ParentEntity> loadAll(Collection<PermissionScope> skipped) {
        return DKPerms.getInstance().getStorage().getParentStorage().getAllParentReferences(object,skipped);
    }
}
