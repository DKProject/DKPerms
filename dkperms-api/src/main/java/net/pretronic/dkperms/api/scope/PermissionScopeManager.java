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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PermissionScopeManager {

    PermissionScope getCurrentInstanceScope();

    void setCurrentInstanceScope(PermissionScope scope);

    PermissionScope getRoot();

    PermissionScope getNamespace(String name);

    PermissionScope get(String scopeOrder);

    List<PermissionScope> getValidScopes(PermissionScope currentScope);

    CompletableFuture<Collection<PermissionScope>> getValidScopesAsync(PermissionScope currentScope);

    PermissionScopeBuilder newBuilder();

    void loadRootScope();
    //search

}
