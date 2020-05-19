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

import net.pretronic.dkperms.api.graph.ObjectGraph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.object.SyncAction;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.libraries.synchronisation.observer.AbstractObservable;
import net.pretronic.libraries.synchronisation.observer.ObserveCallback;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;

import java.util.List;
import java.util.function.Predicate;

public class FilteredObjectGraph extends AbstractObservable<PermissionObject, SyncAction> implements ObjectGraph, ObserveCallback<PermissionObject, SyncAction> {

    private final ObjectGraph graph;
    private final PermissionObjectType filteredType;

    public FilteredObjectGraph(ObjectGraph graph,PermissionObjectType filteredType) {
        Validate.notNull(graph,filteredType);
        this.graph = graph;
        this.filteredType = filteredType;
    }

    @Override
    public List<PermissionObject> traverse() {
        return Iterators.filter(traverse(), object -> object.getType().equals(filteredType));
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
        this.graph.subscribeObserver(this);
    }

    @Override
    public void unsubscribeObservers() {
        this.graph.unsubscribeObservers();
    }

    @Override
    public void callback(PermissionObject object, SyncAction action) {
        callObservers(object, action);
    }
}
