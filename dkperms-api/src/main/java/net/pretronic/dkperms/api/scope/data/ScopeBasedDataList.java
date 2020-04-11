/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 01.03.20, 12:58
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.scope.data;

import net.pretronic.dkperms.api.scope.PermissionScope;

import java.util.Collection;
import java.util.List;

public interface ScopeBasedDataList<T> extends List<ScopeBasedData<T>> {

    ScopeBasedData<T> get(PermissionScope scope);

    Collection<T> getData(PermissionScope scope);

    Collection<T> getAll();

    Collection<PermissionScope> getScopes();

    void put(PermissionScope scope,Collection<T> data);
}
