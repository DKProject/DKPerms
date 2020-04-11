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

public interface ScopeBasedData<T> {

    PermissionScope getScope();

    Collection<T> getData();


}
