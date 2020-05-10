/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 22.04.20, 19:02
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.object;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.graph.*;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.snapshot.PermissionObjectSnapshot;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.common.graph.DefaultGroupGraph;
import net.pretronic.dkperms.common.graph.DefaultObjectGraph;
import net.pretronic.dkperms.common.graph.DefaultObjectMetaGraph;
import net.pretronic.dkperms.common.graph.DefaultPermissionGraph;
import net.pretronic.libraries.synchronisation.observer.AbstractObservable;
import net.pretronic.libraries.utility.Validate;

public class DefaultPermissionObjectSnapshot extends AbstractObservable<PermissionObjectSnapshot,PermissionScope> implements PermissionObjectSnapshot {

    private final PermissionObject object;

    private PermissionScope scope;
    private Graph<PermissionScope> scopeGraph;

    private PermissionGraph permissionGraph;
    private PermissionGraph permissionInheritanceGraph;

    private GroupGraph groupGraph;
    private GroupGraph groupInheritanceGraph;

    private ObjectGraph effectedGroupGraph;
    private ObjectGraph effectedGroupInheritanceGraph;

    private ObjectMetaGraph metaGraph;
    private ObjectMetaGraph metaInheritanceGraph;

    public DefaultPermissionObjectSnapshot(PermissionObject object,PermissionScope scope) {
        Validate.notNull(object,scope);

        this.object = object;
        setScope(scope);
    }

    @Override
    public PermissionObject getObject() {
        return object;
    }

    @Override
    public PermissionScope getScope() {
        return scope;
    }

    @Override
    public Graph<PermissionScope> getScopeRange() {
        return scopeGraph;
    }

    @Override
    public void setScope(PermissionScope scope) {
        Validate.notNull(scope);
        PermissionScope oldScope = this.scope;

        if(groupGraph != null) unsubscribe();

        this.scope = scope;
        this.scopeGraph = DKPerms.getInstance().getScopeManager().newGraph(scope);

        this.groupGraph = new DefaultGroupGraph(object,scopeGraph,false);
        this.groupInheritanceGraph = new DefaultGroupGraph(object,scopeGraph,true);

        this.effectedGroupGraph = new DefaultObjectGraph(this.groupGraph);
        this.effectedGroupInheritanceGraph = new DefaultObjectGraph(this.groupInheritanceGraph);

        this.metaGraph = new DefaultObjectMetaGraph(object,scopeGraph,null);
        this.metaInheritanceGraph = new DefaultObjectMetaGraph(object,scopeGraph,effectedGroupInheritanceGraph);

        this.permissionGraph = new DefaultPermissionGraph(object,scopeGraph,null);
        this.permissionInheritanceGraph = new DefaultPermissionGraph(object,scopeGraph,effectedGroupInheritanceGraph);

        subscribe();
        callObservers(this,oldScope);
    }

    private void subscribe(){
        System.out.println("Snapshot Subscribe");
        this.groupGraph.subscribeObservers();
        this.groupInheritanceGraph.subscribeObservers();
        this.effectedGroupGraph.subscribeObservers();
        this.effectedGroupInheritanceGraph.subscribeObservers();
        this.metaGraph.subscribeObservers();
        this.metaInheritanceGraph.subscribeObservers();
        this.permissionGraph.subscribeObservers();
        this.permissionInheritanceGraph.subscribeObservers();
    }

    private void unsubscribe(){
        System.out.println("Snapshot unsubscribe");
        this.groupGraph.unsubscribeObservers();
        this.groupInheritanceGraph.unsubscribeObservers();
        this.effectedGroupGraph.unsubscribeObservers();
        this.effectedGroupInheritanceGraph.unsubscribeObservers();
        this.metaGraph.unsubscribeObservers();
        this.metaInheritanceGraph.unsubscribeObservers();
        this.permissionGraph.unsubscribeObservers();
        this.permissionInheritanceGraph.unsubscribeObservers();
    }

    @Override
    public PermissionGraph getPermissionGraph() {
        return permissionGraph;
    }

    @Override
    public PermissionGraph getPermissionInheritanceGraph() {
        return permissionInheritanceGraph;
    }

    @Override
    public GroupGraph getGroupGraph() {
        return groupGraph;
    }

    @Override
    public GroupGraph getGroupInheritanceGraph() {
        return groupInheritanceGraph;
    }

    @Override
    public ObjectGraph getEffectedGroupGraph() {
        return effectedGroupGraph;
    }

    @Override
    public ObjectGraph getEffectedGroupInheritanceGraph() {
        return effectedGroupInheritanceGraph;
    }

    @Override
    public ObjectMetaGraph getMetaGraph() {
        return metaGraph;
    }

    @Override
    public ObjectMetaGraph getMetaInheritanceGraph() {
        return metaInheritanceGraph;
    }
}
