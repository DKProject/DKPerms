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

import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.graph.PermissionGraph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.SyncAction;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;
import net.pretronic.dkperms.common.calculator.PermissionCalculator;
import net.pretronic.libraries.synchronisation.observer.AbstractObservable;
import net.pretronic.libraries.synchronisation.observer.Observable;
import net.pretronic.libraries.synchronisation.observer.ObserveCallback;
import net.pretronic.libraries.utility.Validate;

import java.util.ArrayList;
import java.util.List;

public class DefaultPermissionGraph extends AbstractObservable<PermissionObject,SyncAction> implements PermissionGraph, ObserveCallback<PermissionObject, SyncAction> {

    private final PermissionObject owner;
    private final Graph<PermissionScope> scopes;
    private final Graph<PermissionObject> groups;

    private final List<PermissionEntity> result;

    public DefaultPermissionGraph(PermissionObject owner, Graph<PermissionScope> scopes, Graph<PermissionObject> groups) {
        Validate.notNull(owner,scopes);
        this.owner = owner;
        this.scopes = scopes;
        this.groups = groups;
        this.result = new ArrayList<>();
    }

    @Override
    public List<PermissionEntity> traverse() {
        if(result.isEmpty()){
            if(groups == null) traverseOne();
            else traverseMany();
        }
        return result;
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
        return PermissionCalculator.calculate(traverse(),permission);
    }

    @Override
    public List<String> toStringList() {
        return PermissionCalculator.toStringList(this);
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
        callObservers(action);
    }
}
