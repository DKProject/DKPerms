/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 02.11.19, 13:24
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.storage;

import net.pretronic.dkperms.api.scope.PermissionScope;

import java.util.List;

public interface ScopeStorage {

    List<PermissionScope> loadScopes(PermissionScope parent);

    PermissionScope getScope(int id);

    int insertScope(int parentId, String key, String name);

    void updateScopeName(int scopeId, String key, String name);

    void updateScopeParent(int scopeId, int parentId);

    void deleteScope(int scopeId);

}
