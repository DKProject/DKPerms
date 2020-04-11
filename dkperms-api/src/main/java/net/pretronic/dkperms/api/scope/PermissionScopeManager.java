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

import net.pretronic.dkperms.api.graph.Graph;

public interface PermissionScopeManager {

    PermissionScope getCurrentInstanceScope();

    void setCurrentInstanceScope(PermissionScope scope);

    PermissionScope getRoot();

    PermissionScope getNamespace(String name);

    PermissionScope get(String scopeOrder);

    PermissionScope getScope(int id);

    default Graph<PermissionScope> newGraph(PermissionScope end){
        return newGraph(getRoot(),end);
    }

    Graph<PermissionScope> newGraph(PermissionScope start, PermissionScope end);

    PermissionScopeBuilder newBuilder();


}
