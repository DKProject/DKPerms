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

import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.graph.ObjectMetaGraph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.SyncAction;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;
import net.pretronic.libraries.synchronisation.observer.AbstractObservable;
import net.pretronic.libraries.synchronisation.observer.ObserveCallback;
import net.pretronic.libraries.utility.SystemUtil;
import net.pretronic.libraries.utility.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BooleanSupplier;

public class DefaultObjectMetaGraph extends AbstractObservable<PermissionObject,SyncAction> implements ObjectMetaGraph, ObserveCallback<PermissionObject, SyncAction> {

    public static final Comparator<ObjectMetaEntry> PRIORITY_COMPARATOR = Comparator.comparingInt(ObjectMetaEntry::getPriority);

    private final PermissionObject owner;
    private final Graph<PermissionScope> scopes;
    private final Graph<PermissionObject> groups;

    private final List<ObjectMetaEntry> result;

    private boolean traversing;
    private final BooleanSupplier sleeper = () -> traversing;

    public DefaultObjectMetaGraph(PermissionObject owner, Graph<PermissionScope> scopes, Graph<PermissionObject> groups) {
        Validate.notNull(owner,scopes);
        this.owner = owner;
        this.scopes = scopes;
        this.groups = groups;

        this.result = new ArrayList<>();
    }

    @Override
    public List<ObjectMetaEntry> traverse() {
        if(traversing) SystemUtil.sleepAsLong(sleeper);
        if(result.isEmpty()) traverseInternal();
        return Collections.unmodifiableList(result);
    }

    private void traverseInternal(){
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
    public void traverseMany() {
        List<PermissionObject> groups  = this.groups.traverse();

        ScopeBasedDataList<ObjectMetaEntry>[] dataList = new ScopeBasedDataList[groups.size()+1];

        int index = 0;
        for (PermissionObject group : groups) {
            dataList[index++] = group.getMeta().getEntries(scopes);
        }
        dataList[index] = owner.getMeta().getEntries(scopes);

        for (PermissionScope scope : scopes.traverse()) {
            for (ScopeBasedDataList<ObjectMetaEntry> data : dataList) {
                List<ObjectMetaEntry> entries = new ArrayList<>(data.getData(scope));
                entries.sort(PRIORITY_COMPARATOR);
                result.addAll(entries);
            }
        }
    }

    public void traverseOne() {
        ScopeBasedDataList<ObjectMetaEntry> data = owner.getMeta().getEntries(scopes);
        for (PermissionScope scope : scopes.traverse()) {
            List<ObjectMetaEntry> entries = new ArrayList<>(data.getData(scope));
            entries.sort(PRIORITY_COMPARATOR);
            result.addAll(entries);
        }
    }

    @Override
    public ObjectMetaEntry calculate(String key) {
        List<ObjectMetaEntry> entries = traverse();
        ObjectMetaEntry result = null;
        for (ObjectMetaEntry entry : entries) {
            if(!entry.hasTimeout()){
                if(entry.getKey().equalsIgnoreCase(key)){
                    result = entry;
                }
            }
        }
        return result;
    }

    @Override
    public boolean isSet(String key) {
        return calculate(key) != null;
    }

    @Override
    public boolean isSet(String key, Object value) {
        ObjectMetaEntry entry = calculate(key);
        return entry != null && entry.equalsValue(value);
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
        if(action == SyncAction.OBJECT_GROUP_UPDATE || action == SyncAction.OBJECT_META_UPDATE){
            this.result.clear();
        }
        callObservers(observable,action);
    }
}
