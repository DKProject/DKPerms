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
import net.pretronic.dkperms.api.entity.PermissionGroupEntity;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.graph.GroupGraph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.SyncAction;
import net.pretronic.dkperms.api.object.meta.ObjectMeta;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedData;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;
import net.pretronic.dkperms.common.entity.DefaultPermissionGroupEntity;
import net.pretronic.libraries.synchronisation.observer.AbstractObservable;
import net.pretronic.libraries.synchronisation.observer.ObserveCallback;
import net.pretronic.libraries.utility.Validate;

import java.util.*;
import java.util.function.ToIntFunction;

public final class DefaultGroupGraph extends AbstractObservable<PermissionObject, SyncAction> implements GroupGraph, ObserveCallback<PermissionObject, SyncAction> {

    private final PermissionObject owner;
    private final Graph<PermissionScope> scopes;
    private final boolean subGroups;

    private final List<PermissionGroupEntity> entities;
    private final Map<PermissionGroupEntity,Integer> objectPriority;

    private boolean subscribe;

    public DefaultGroupGraph(PermissionObject owner, Graph<PermissionScope> scopes, boolean subGroups) {
        Validate.notNull(owner,scopes);
        this.owner = owner;
        this.scopes = scopes;
        this.subGroups = subGroups;

        this.entities = new ArrayList<>();
        this.objectPriority = new HashMap<>();
    }

    @Override
    public List<PermissionGroupEntity> traverse() {
        if(this.objectPriority.isEmpty()) traverse0();
        return this.entities;
    }

    private void traverse0(){
        findNextGroups(owner,1);

        for (PermissionObject defaultGroup : DKPerms.getInstance().getObjectManager().getDefaultGroups(scopes)) {
            DefaultPermissionGroupEntity defaultEntity =
                    new DefaultPermissionGroupEntity(owner,-1,defaultGroup,PermissionAction.ALLOW,defaultGroup.getScope(),-1);

            this.objectPriority.put(defaultEntity,Integer.MAX_VALUE);
            this.entities.add(defaultEntity);
        }

        entities.sort(Comparator
                .comparingInt((PermissionGroupEntity o) -> o.getScope().getLevel())
                .thenComparing(((Comparator<PermissionGroupEntity>) (o1, o2) -> Integer.compare(objectPriority.get(o1), objectPriority.get(o2))).reversed())
                .thenComparing(Comparator.comparingInt((ToIntFunction<PermissionGroupEntity>) entity -> entity.getGroup().getPriority()).reversed()));

        trySubscribe();
    }

    private void findNextGroups(PermissionObject object, int level){
        ScopeBasedDataList<PermissionGroupEntity> dataList = object.getGroups(scopes);
        for (ScopeBasedData<PermissionGroupEntity> groupData : dataList) {
            for (PermissionGroupEntity group : groupData.getData()) {
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
        trySubscribe();
    }

    @Override
    public void unsubscribeObservers() {
        System.out.println("Unsubscribing from observers");
        subscribe = false;
        owner.unsubscribeObserver(this);
        if(subGroups){
            for (PermissionGroupEntity object : objectPriority.keySet()) {
                object.getGroup().unsubscribeObserver(this);
            }
        }
    }

    private void trySubscribe(){
        if(!subscribe) return;
        System.out.println("-----------------");
        System.out.println("Subscribing to observers "+this+" | "+subGroups);
        if(!owner.isObserverSubscribed(this)){
            owner.subscribeObserver(this);
            System.out.println("Subscribing to owner");
        }
        if(subGroups){
            for (PermissionGroupEntity object : objectPriority.keySet()) {
                if(!object.getGroup().isObserverSubscribed(this)){
                    System.out.println("Subscribing to group observer "+object.getGroup().getName());
                    object.getGroup().subscribeObserver(this);
                }
            }
        }
        System.out.println("-----------------");
    }

    @Override
    public void callback(PermissionObject observable, SyncAction action) {
        if(action == SyncAction.OBJECT_GROUP_UPDATE){
            if(subGroups && subscribe){
                for (PermissionGroupEntity object : objectPriority.keySet()) {
                    object.getGroup().unsubscribeObserver(this);
                }
            }
            this.objectPriority.clear();
            this.entities.clear();
        }
        callObservers(observable,action);
    }

    @Override
    public PermissionGroupEntity getGroup(PermissionObject group) {
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
