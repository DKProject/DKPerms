/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.storage;

import net.pretronic.dkperms.api.context.PermissionContext;
import net.pretronic.dkperms.api.scope.PermissionScope;

import java.util.List;

public interface DKPermsStorage {

    String getName();

    boolean connect();

    boolean isConnected();

    boolean disconnect();


    List<PermissionScope> loadScopes(PermissionScope parent);

    int insertScope(PermissionScope scope);

    void updateScopeName(PermissionScope scope);

    void updateScopeParent(PermissionScope scope, PermissionScope newParent);

    void deleteScope(PermissionScope scope);


    PermissionContext getContext(int id);

    PermissionContext getContext(String name);

    int createContext(String name);
}
