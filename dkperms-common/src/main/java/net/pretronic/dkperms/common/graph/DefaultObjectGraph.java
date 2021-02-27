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

import net.pretronic.dkperms.api.entity.ParentEntity;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.graph.ObjectGraph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.SyncAction;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.libraries.synchronisation.observer.AbstractObservable;
import net.pretronic.libraries.synchronisation.observer.ObserveCallback;
import net.pretronic.libraries.utility.SystemUtil;
import net.pretronic.libraries.utility.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;

public class DefaultObjectGraph extends AbstractObservable<PermissionObject, SyncAction> implements ObjectGraph, ObserveCallback<PermissionObject, SyncAction> {

    private final Graph<ParentEntity> inheritanceGraph;

    private List<PermissionObject> result;

    private boolean traversing;
    private final BooleanSupplier sleeper = () -> traversing;

    public DefaultObjectGraph(Graph<ParentEntity> inheritanceGraph) {
        Validate.notNull(inheritanceGraph);
        this.inheritanceGraph = inheritanceGraph;
        this.result = new ArrayList<>();
    }

    @Override
    public List<PermissionObject> traverse() {
        if(traversing) SystemUtil.sleepAsLong(sleeper);
        if(result.isEmpty()) traverse0();
        return Collections.unmodifiableList(result);
    }

    private void traverse0() {
        if(traversing){//Additional check
            SystemUtil.sleepAsLong(sleeper);
            return;
        }
        try{
            traversing = true;
            List<PermissionObject> blocked = new ArrayList<>();
            List<ParentEntity> inheritance = inheritanceGraph.traverse();

            for (ParentEntity entity : inheritance) {
                if(!blocked.contains(entity.getParent())){
                    if(entity.getAction() == PermissionAction.ALLOW){
                        result.remove(entity.getParent());
                        result.add(entity.getParent());
                    }else if(entity.getAction() == PermissionAction.REJECT){
                        result.remove(entity.getParent());
                    }else if(entity.getAction() == PermissionAction.ALLOW_ALWAYS){
                        if(!result.contains(entity.getParent())) result.add(entity.getParent());
                        blocked.add(entity.getParent());
                    }else if(entity.getAction() == PermissionAction.REJECT_ALWAYS){
                        result.remove(entity.getParent());
                        blocked.add(entity.getParent());
                    }
                }
            }
            traversing = false;
        }catch (Exception e){
            traversing = false;
            throw e;
        }
    }

    @Override
    public boolean contains(PermissionObject group) {
        return traverse().contains(group);
    }

    @Override
    public PermissionObject getHighest() {
        List<PermissionObject> result = traverse();
        if(!result.isEmpty()){
            return result.get(result.size()-1);
        }
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
        if(action == SyncAction.OBJECT_GROUP_UPDATE || action == SyncAction.OBJECT_META_UPDATE || action == SyncAction.OBJECT_RELOAD){
            this.result.clear();
        }
        callObservers(observable,action);
    }
}
