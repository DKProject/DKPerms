/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.scope;

public interface PermissionScopeManager {

    PermissionScope getCurrentInstanceScope();

    PermissionScope getRoot();

    PermissionScope getNamespace(String name);

    PermissionScope get(String scopeOrder);

    PermissionScopeBuilder newBuilder();

    //search

}
