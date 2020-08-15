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
import net.pretronic.dkperms.api.event.DKPermsScopeChangeEvent;
import net.pretronic.dkperms.api.graph.*;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectSnapshot;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.common.graph.*;
import net.pretronic.libraries.synchronisation.observer.AbstractObservable;
import net.pretronic.libraries.utility.Validate;

public class DefaultPermissionObjectSnapshot extends AbstractObservable<PermissionObjectSnapshot,PermissionScope> implements PermissionObjectSnapshot {

    private final PermissionObject object;

    private PermissionScope scope;
    private Graph<PermissionScope> scopeGraph;

    private PermissionGraph permissionGraph;
    private PermissionGraph permissionInheritanceGraph;

    private ParentGraph groupGraph;
    private ParentGraph groupInheritanceGraph;

    private ObjectGraph effectedParentGraph;
    private ObjectGraph effectedParentInheritanceGraph;

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
    public void setScope(PermissionScope scope0) {
        Validate.notNull(scope0);
        PermissionScope scope = scope0;
        PermissionScope oldScope = this.scope;

        DKPermsScopeChangeEvent event = DKPerms.getInstance().getEventBus().callEvent(new DKPermsScopeChangeEvent(object,this,scope));
        scope = event.getNewScope();

        if(groupGraph != null) unsubscribe();

        this.scope = scope;
        this.scopeGraph = DKPerms.getInstance().getScopeManager().newGraph(scope);

        this.groupGraph = new DefaultParentGraph(object,scopeGraph,false);
        this.groupInheritanceGraph = new DefaultParentGraph(object,scopeGraph,true);

        this.effectedParentGraph = new DefaultObjectGraph(this.groupGraph);
        this.effectedParentInheritanceGraph = new DefaultObjectGraph(this.groupInheritanceGraph);

        this.effectedGroupGraph = new FilteredObjectGraph(this.effectedParentGraph, PermissionObjectType.GROUP);
        this.effectedGroupInheritanceGraph = new FilteredObjectGraph(this.effectedParentInheritanceGraph, PermissionObjectType.GROUP);

        this.metaGraph = new DefaultObjectMetaGraph(object,scopeGraph,null);
        this.metaInheritanceGraph = new DefaultObjectMetaGraph(object,scopeGraph,effectedParentInheritanceGraph);

        this.permissionGraph = new DefaultPermissionGraph(object,scopeGraph,null);
        this.permissionInheritanceGraph = new DefaultPermissionGraph(object,scopeGraph,effectedParentInheritanceGraph);

        subscribe();
        callObservers(this,oldScope);
    }

    private void subscribe(){
        this.groupGraph.subscribeObservers();
        this.groupInheritanceGraph.subscribeObservers();
        this.effectedParentGraph.subscribeObservers();
        this.effectedParentInheritanceGraph.subscribeObservers();
        this.metaGraph.subscribeObservers();
        this.metaInheritanceGraph.subscribeObservers();
        this.permissionGraph.subscribeObservers();
        this.permissionInheritanceGraph.subscribeObservers();
    }

    private void unsubscribe(){
        this.groupGraph.unsubscribeObservers();
        this.groupInheritanceGraph.unsubscribeObservers();
        this.effectedParentGraph.unsubscribeObservers();
        this.effectedParentInheritanceGraph.unsubscribeObservers();
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
    public ParentGraph getParentGraph() {
        return groupGraph;
    }

    @Override
    public ParentGraph getParentInheritanceGraph() {
        return groupInheritanceGraph;
    }

    @Override
    public ObjectGraph getEffectedParentGraph() {
        return effectedParentGraph;
    }

    @Override
    public ObjectGraph getEffectedParentInheritanceGraph() {
        return effectedParentInheritanceGraph;
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
