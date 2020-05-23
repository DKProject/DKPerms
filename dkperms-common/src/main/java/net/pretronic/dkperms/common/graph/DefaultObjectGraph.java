/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 24.04.20, 20:22
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.graph;

import net.pretronic.dkperms.api.entity.PermissionParentEntity;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.graph.ObjectGraph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.SyncAction;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.libraries.synchronisation.observer.AbstractObservable;
import net.pretronic.libraries.synchronisation.observer.ObserveCallback;
import net.pretronic.libraries.utility.Validate;

import java.util.ArrayList;
import java.util.List;

public class DefaultObjectGraph extends AbstractObservable<PermissionObject, SyncAction> implements ObjectGraph, ObserveCallback<PermissionObject, SyncAction> {

    private final Graph<PermissionParentEntity> inheritanceGraph;

    private List<PermissionObject> result;

    public DefaultObjectGraph(Graph<PermissionParentEntity> inheritanceGraph) {
        Validate.notNull(inheritanceGraph);
        this.inheritanceGraph = inheritanceGraph;
        this.result = new ArrayList<>();
    }

    @Override
    public List<PermissionObject> traverse() {
        if(result.isEmpty()) traverse0();
        return result;
    }

    private void traverse0() {
        List<PermissionObject> blocked = new ArrayList<>();
        List<PermissionParentEntity> inheritance = inheritanceGraph.traverse();

        for (PermissionParentEntity entity : inheritance) {
            if(!blocked.contains(entity.getGroup())){
                if(entity.getAction() == PermissionAction.ALLOW){
                    result.remove(entity.getGroup());
                    result.add(entity.getGroup());
                }else if(entity.getAction() == PermissionAction.REJECT){
                    result.remove(entity.getGroup());
                }else if(entity.getAction() == PermissionAction.ALLOW_ALWAYS){
                    if(!result.contains(entity.getGroup())) result.add(entity.getGroup());
                    blocked.add(entity.getGroup());
                }else if(entity.getAction() == PermissionAction.REJECT_ALWAYS){
                    result.remove(entity.getGroup());
                    blocked.add(entity.getGroup());
                }
            }
        }
    }

    @Override
    public boolean contains(PermissionObject group) {
        return traverse().contains(group);
    }

    @Override
    public PermissionAction calculate(PermissionObject group) {
        return null;
    }

    @Override
    public PermissionObject getHighest() {
        return null;
    }

    @Override
    public void subscribeObservers() {
        this.inheritanceGraph.subscribeObserver(this);
    }

    @Override
    public void unsubscribeObservers() {
        this.inheritanceGraph.unsubscribeObservers();
    }

    @Override
    public void callback(PermissionObject observable, SyncAction action) {
        if(action == SyncAction.OBJECT_GROUP_UPDATE || action == SyncAction.OBJECT_META_UPDATE){
            this.result.clear();
        }
        callObservers(observable,action);
    }
}
