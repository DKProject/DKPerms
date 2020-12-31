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
import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.event.permission.DKPermsPermissionCalculationEvent;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.graph.PermissionGraph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.SyncAction;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.permission.analyse.PermissionAnalyseResult;
import net.pretronic.dkperms.api.permission.analyse.PermissionRequest;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;
import net.pretronic.dkperms.common.calculator.PermissionCalculator;
import net.pretronic.dkperms.common.permission.DefaultPermissionCheckResult;
import net.pretronic.dkperms.common.permission.DefaultPermissionRequest;
import net.pretronic.libraries.synchronisation.observer.AbstractObservable;
import net.pretronic.libraries.synchronisation.observer.ObserveCallback;
import net.pretronic.libraries.utility.SystemUtil;
import net.pretronic.libraries.utility.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;

public class DefaultPermissionGraph extends AbstractObservable<PermissionObject,SyncAction> implements PermissionGraph, ObserveCallback<PermissionObject, SyncAction> {

    private final PermissionObject owner;
    private final Graph<PermissionScope> scopes;
    private final Graph<PermissionObject> groups;

    private final List<PermissionEntity> result;

    private boolean traversing;
    private final BooleanSupplier sleeper = () -> traversing;

    public DefaultPermissionGraph(PermissionObject owner, Graph<PermissionScope> scopes, Graph<PermissionObject> groups) {
        Validate.notNull(owner,scopes);
        this.owner = owner;
        this.scopes = scopes;
        this.groups = groups;
        this.result = new ArrayList<>();
    }

    @Override
    public List<PermissionEntity> traverse() {
        if(traversing) SystemUtil.sleepAsLong(sleeper);
        if(result.isEmpty()) traverseInternal();
        return Collections.unmodifiableList(result);
    }

    private void traverseInternal(){
        if(traversing){//Additional check
            SystemUtil.sleepAsLong(sleeper);
            return;
        }
        traversing = true;
        try{
            if(groups == null) traverseOne();
            else traverseMany();
            traversing = false;
        }catch (Exception e){
            traversing = false;
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public void traverseMany(){
        List<PermissionObject> groups = this.groups.traverse();

        ScopeBasedDataList<PermissionEntity>[] dataList = new ScopeBasedDataList[groups.size()+1];

        int index = 0;
        for (PermissionObject group : groups) {
            dataList[index++] = group.getPermissions(scopes);
        }
        dataList[index] = owner.getPermissions(scopes);

        for (PermissionScope scope : scopes.traverse()) {
            for (ScopeBasedDataList<PermissionEntity> data : dataList) {
                result.addAll(data.getData(scope));
            }
        }
    }

    public void traverseOne(){
        ScopeBasedDataList<PermissionEntity> data = owner.getPermissions(scopes);
        for (PermissionScope scope : scopes.traverse()) {
            result.addAll(data.getData(scope));
        }
    }

    @Override
    public PermissionEntity getPermission(String permission) {
        return PermissionCalculator.findBestPermissionEntity(traverse(),permission);
    }

    @Override
    public boolean containsPermission(String permission) {
        return PermissionCalculator.containsPermission(this,permission);
    }

    @Override
    public PermissionAction calculatePermission(String permission) {
        PermissionAction result =  PermissionCalculator.calculate(traverse(),permission);
        if(DKPerms.getInstance().getAnalyser().isEnabled()){
            if(DKPerms.getInstance().getAnalyser().canPermissionBeAnalysed(permission)){
                StackTraceElement[] stackTrace= Thread.currentThread().getStackTrace();
                PermissionRequest request = new DefaultPermissionRequest(owner,this,scopes,permission,result,stackTrace);
                DKPerms.getInstance().getAnalyser().offerRequest(request);
            }
        }

        DKPermsPermissionCalculationEvent event = new DKPermsPermissionCalculationEvent(owner,permission,scopes,this,result);
        DKPerms.getInstance().getEventBus().callEvent(event);
        return event.getAction();
    }

    @Override
    public List<String> toStringList() {
        return PermissionCalculator.toStringList(this);
    }

    @Override
    public PermissionAnalyseResult<PermissionEntity> analyze(String permission) {
        List<PermissionEntity> result = new ArrayList<>();
        for (PermissionEntity entity : traverse()) {
            if(entity.check(permission) != PermissionAction.NEUTRAL){
                result.add(entity);
            }
        }
        PermissionAction finalAction = PermissionAction.NEUTRAL;
        if(!result.isEmpty()) finalAction = result.get(result.size()-1).getAction();
        return new DefaultPermissionCheckResult<>(finalAction,result);
    }

    @Override
    public void subscribeObservers() {
        if(groups == null) owner.subscribeObserver(this);
        else groups.subscribeObserver(this);
    }

    @Override
    public void unsubscribeObservers() {
        if(groups == null) owner.unsubscribeObserver(this);
        else groups.unsubscribeObserver(this);
    }

    @Override
    public void callback(PermissionObject observable, SyncAction action) {
        if(action == SyncAction.OBJECT_GROUP_UPDATE || action == SyncAction.OBJECT_PERMISSION_UPDATE){
            this.result.clear();
        }
        callObservers(observable,action);
    }
}
