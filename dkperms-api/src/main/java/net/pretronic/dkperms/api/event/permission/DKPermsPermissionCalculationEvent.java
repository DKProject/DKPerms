/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 24.05.20, 20:26
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.event.permission;

import net.pretronic.dkperms.api.event.DKPermsEvent;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.graph.PermissionGraph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.libraries.utility.Validate;

public class DKPermsPermissionCalculationEvent implements DKPermsEvent {

    private final PermissionObject object;
    private final String permission;
    private final Graph<PermissionScope> scopeRange;
    private final PermissionGraph graph;
    private PermissionAction action;

    public DKPermsPermissionCalculationEvent(PermissionObject object, String permission, Graph<PermissionScope> scopeRange, PermissionGraph graph, PermissionAction action) {
        this.object = object;
        this.permission = permission;
        this.scopeRange = scopeRange;
        this.graph = graph;
        this.action = action;
    }

    public PermissionObject getObject(){
        return object;
    }

    public String getPermission(){
        return permission;
    }

    public Graph<PermissionScope> getScopeRange(){
        return scopeRange;
    }

    public PermissionGraph getGraph() {
        return graph;
    }

    public PermissionAction getAction(){
        return action;
    }

    public void setAction(PermissionAction action){
        Validate.notNull(action);
        this.action = action;
    }

}
