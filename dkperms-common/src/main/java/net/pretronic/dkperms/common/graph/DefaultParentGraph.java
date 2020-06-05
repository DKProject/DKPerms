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
import net.pretronic.dkperms.api.entity.ParentEntity;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.graph.ParentGraph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.SyncAction;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.permission.analyse.PermissionAnalyseResult;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedData;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;
import net.pretronic.dkperms.common.calculator.ParentCalculator;
import net.pretronic.dkperms.common.entity.DefaultPermissionParentEntity;
import net.pretronic.dkperms.common.permission.DefaultPermissionCheckResult;
import net.pretronic.libraries.synchronisation.observer.AbstractObservable;
import net.pretronic.libraries.synchronisation.observer.ObserveCallback;
import net.pretronic.libraries.utility.SystemUtil;
import net.pretronic.libraries.utility.Validate;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.ToIntFunction;

public final class DefaultParentGraph extends AbstractObservable<PermissionObject, SyncAction> implements ParentGraph, ObserveCallback<PermissionObject, SyncAction> {

    private final PermissionObject owner;
    private final Graph<PermissionScope> scopes;
    private final boolean subGroups;

    private final List<ParentEntity> entities;
    private final Map<ParentEntity,Integer> objectPriority;

    private boolean subscribe;

    private boolean traversing;
    private final BooleanSupplier sleeper = () -> traversing;

    public DefaultParentGraph(PermissionObject owner, Graph<PermissionScope> scopes, boolean subGroups) {
        Validate.notNull(owner,scopes);
        this.owner = owner;
        this.scopes = scopes;
        this.subGroups = subGroups;

        this.entities = new ArrayList<>();
        this.objectPriority = new HashMap<>();
    }

    @Override
    public List<ParentEntity> traverse() {
        if(traversing) SystemUtil.sleepAsLong(sleeper);
        if(objectPriority.isEmpty()) traverseInternal();
        return entities;
    }

    private void traverseInternal(){
        traversing = true;
        try{
            traverse0();
            traversing = false;
        }catch (Exception e){
            traversing = false;
            throw e;
        }
    }

    private void traverse0(){
        findNextGroups(owner,1);

        for (PermissionObject defaultGroup : DKPerms.getInstance().getObjectManager().getDefaultGroups(scopes)) {
            DefaultPermissionParentEntity defaultEntity = new DefaultPermissionParentEntity(owner,-1
                    ,defaultGroup,PermissionAction.ALLOW,defaultGroup.getScope(),-1);

            this.objectPriority.put(defaultEntity,Integer.MAX_VALUE);
            this.entities.add(defaultEntity);
        }

        entities.sort(Comparator
                .comparingInt((ParentEntity o) -> o.getScope().getLevel())
                .thenComparing(((Comparator<ParentEntity>) (o1, o2) -> Integer.compare(objectPriority.get(o1), objectPriority.get(o2))).reversed())
                .thenComparing(Comparator.comparingInt((ToIntFunction<ParentEntity>) entity -> entity.getParent().getPriority()).reversed()));

        trySubscribe();
    }

    private void findNextGroups(PermissionObject object, int level){
        ScopeBasedDataList<ParentEntity> dataList = object.getParents(scopes);
        for (ScopeBasedData<ParentEntity> groupData : dataList) {
            for (ParentEntity group : groupData.getData()) {
                if(!group.hasTimeout()){
                    if(!entities.contains(group)){
                        entities.add(group);
                        objectPriority.put(group,level);
                        if(subGroups) findNextGroups(group.getParent(),level+1);
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
        subscribe = false;
        owner.unsubscribeObserver(this);
        if(subGroups){
            for (ParentEntity object : objectPriority.keySet()) {
                object.getParent().unsubscribeObserver(this);
            }
        }
    }

    private void trySubscribe(){
        if(!subscribe) return;
        if(!owner.isObserverSubscribed(this)){
            owner.subscribeObserver(this);
        }
        if(subGroups){
            for (ParentEntity object : objectPriority.keySet()) {
                if(!object.getParent().isObserverSubscribed(this)){
                    object.getParent().subscribeObserver(this);
                }
            }
        }
    }

    @Override
    public void callback(PermissionObject observable, SyncAction action) {
        if(action == SyncAction.OBJECT_GROUP_UPDATE){
            if(subGroups && subscribe){
                for (ParentEntity object : objectPriority.keySet()) {
                    object.getParent().unsubscribeObserver(this);
                }
            }
            this.objectPriority.clear();
            this.entities.clear();
        }
        callObservers(observable,action);
    }

    @Override
    public ParentEntity getParent(PermissionObject group) {
        return ParentCalculator.findBestParentEntity(this,group);
    }

    @Override
    public boolean containsGroup(PermissionObject group) {
        return ParentCalculator.containsParent(this,group);
    }

    @Override
    public PermissionAction calculateGroup(PermissionObject group) {
        return ParentCalculator.calculate(this,group);
    }

    @Override
    public PermissionAnalyseResult<ParentEntity> analyze(PermissionObject group) {
        List<ParentEntity> result = new ArrayList<>();
        for (ParentEntity entity : traverse()) {
            if(entity.getParent().equals(group)){
                result.add(entity);
            }
        }
        PermissionAction finalAction = PermissionAction.NEUTRAL;
        if(!result.isEmpty()) finalAction = result.get(result.size()-1).getAction();
        return new DefaultPermissionCheckResult<>(finalAction,result);
    }
}
