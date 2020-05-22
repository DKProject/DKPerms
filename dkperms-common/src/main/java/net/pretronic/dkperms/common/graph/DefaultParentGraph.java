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

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.entity.PermissionParentEntity;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.graph.ParentGraph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.SyncAction;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedData;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;
import net.pretronic.dkperms.common.entity.DefaultPermissionParentEntity;
import net.pretronic.libraries.synchronisation.observer.AbstractObservable;
import net.pretronic.libraries.synchronisation.observer.ObserveCallback;
import net.pretronic.libraries.utility.SystemUtil;
import net.pretronic.libraries.utility.Validate;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.ToIntFunction;

public final class DefaultParentGraph extends AbstractObservable<PermissionObject, SyncAction> implements ParentGraph, ObserveCallback<PermissionObject, SyncAction> {

    private final PermissionObject owner;
    private final Graph<PermissionScope> scopes;
    private final boolean subGroups;

    private final List<PermissionParentEntity> entities;
    private final Map<PermissionParentEntity,Integer> objectPriority;

    private boolean subscribe;

    private boolean looked;

    public DefaultParentGraph(PermissionObject owner, Graph<PermissionScope> scopes, boolean subGroups) {
        Validate.notNull(owner,scopes);
        this.owner = owner;
        this.scopes = scopes;
        this.subGroups = subGroups;

        this.entities = new ArrayList<>();
        this.objectPriority = new HashMap<>();
    }

    @Override
    public List<PermissionParentEntity> traverse() {
        if(this.objectPriority.isEmpty()) traverse0();
        return this.entities;
    }

    private void traverse0(){
        while (looked) SystemUtil.sleepUninterruptible(300, TimeUnit.MILLISECONDS);
        findNextGroups(owner,1);

        for (PermissionObject defaultGroup : DKPerms.getInstance().getObjectManager().getDefaultGroups(scopes)) {
            DefaultPermissionParentEntity defaultEntity =
                    new DefaultPermissionParentEntity(owner,-1,defaultGroup,PermissionAction.ALLOW,defaultGroup.getScope(),-1);

            this.objectPriority.put(defaultEntity,Integer.MAX_VALUE);
            this.entities.add(defaultEntity);
        }

        entities.sort(Comparator
                .comparingInt((PermissionParentEntity o) -> o.getScope().getLevel())
                .thenComparing(((Comparator<PermissionParentEntity>) (o1, o2) -> Integer.compare(objectPriority.get(o1), objectPriority.get(o2))).reversed())
                .thenComparing(Comparator.comparingInt((ToIntFunction<PermissionParentEntity>) entity -> entity.getGroup().getPriority()).reversed()));

        System.out.println("-> Traversed Group Graph");
        trySubscribe();
    }

    private void findNextGroups(PermissionObject object, int level){
        ScopeBasedDataList<PermissionParentEntity> dataList = object.getParents(scopes);
        for (ScopeBasedData<PermissionParentEntity> groupData : dataList) {
            for (PermissionParentEntity group : groupData.getData()) {
                if(!group.hasTimeout()){
                    if(!entities.contains(group)){
                        entities.add(group);
                        objectPriority.put(group,level);
                        if(subGroups) findNextGroups(group.getGroup(),level+1);
                    }
                }
            }
        }
    }

    @Override
    public void subscribeObservers() {
        subscribe = true;
        System.out.println("Enabled subscriber subscription");
        trySubscribe();
    }

    @Override
    public void unsubscribeObservers() {
        System.out.println("Unsubscribing from observers "+this+" | "+subGroups);
        subscribe = false;
        owner.unsubscribeObserver(this);
        if(subGroups){
            for (PermissionParentEntity object : objectPriority.keySet()) {
                object.getGroup().unsubscribeObserver(this);
            }
        }
    }

    private void trySubscribe(){
        if(!subscribe) return;
        looked = true;
        System.out.println("-----------------");
        System.out.println("Subscribing to observers "+this+" | "+subGroups);
        if(!owner.isObserverSubscribed(this)){
            owner.subscribeObserver(this);
            System.out.println("Subscribing to owner");
        }
        if(subGroups){
            for (PermissionParentEntity object : objectPriority.keySet()) {
                if(!object.getGroup().isObserverSubscribed(this)){
                    System.out.println("Subscribing to group observer "+object.getGroup().getName());
                    object.getGroup().subscribeObserver(this);
                }
            }
        }
        System.out.println("-----------------");
        looked = false;
    }

    @Override
    public void callback(PermissionObject observable, SyncAction action) {
        System.out.println("GROUP GRAPH CALLBACK "+action);
        if(action == SyncAction.OBJECT_GROUP_UPDATE){
            if(subGroups && subscribe){
                for (PermissionParentEntity object : objectPriority.keySet()) {
                    object.getGroup().unsubscribeObserver(this);
                }
            }
            this.objectPriority.clear();
            this.entities.clear();
        }
        callObservers(observable,action);
    }

    @Override
    public PermissionParentEntity getGroup(PermissionObject group) {
        return null;
    }

    @Override
    public boolean containsGroup(PermissionObject group) {
        return false;
    }

    @Override
    public PermissionAction calculateGroup(PermissionObject group) {
        return null;
    }
}
