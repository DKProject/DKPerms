/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 01.03.20, 12:58
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.scope.data;

import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedData;

import java.util.Collection;

public class DefaultScopeBasedData<T> implements ScopeBasedData<T> {

    private final PermissionScope scope;
    private final Collection<T> data;

    public DefaultScopeBasedData(PermissionScope scope, Collection<T> data) {
        this.scope = scope;
        this.data = data;
    }

    @Override
    public PermissionScope getScope() {
        return scope;
    }

    @Override
    public Collection<T> getData() {
        return data;
    }
}
