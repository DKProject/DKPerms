/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 19.01.20, 13:03
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.cache;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;

import java.util.Collection;
import java.util.Map;

public class ObjectMetaCache extends ScopeBasedCache<ObjectMetaEntry> {

    private final PermissionObject object;

    public ObjectMetaCache(PermissionObject object) {
        this.object = object;
    }

    @Override
    protected Collection<ObjectMetaEntry> load(PermissionScope scope) {
        return DKPerms.getInstance().getStorage().getObjectStorage().getMetaEntries(object.getId(),scope);
    }

    @Override
    protected ScopeBasedDataList<ObjectMetaEntry> load(Collection<PermissionScope> scopes) {
        return DKPerms.getInstance().getStorage().getObjectStorage().getMetaEntries(object.getId(),scopes);
    }

    @Override
    protected ScopeBasedDataList<ObjectMetaEntry> loadAll(Collection<PermissionScope> skipped) {
        return DKPerms.getInstance().getStorage().getObjectStorage().getAllMetaEntries(object.getId(),skipped);
    }
}
