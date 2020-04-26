/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 25.01.20, 23:05
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.graph;

import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.SyncAction;
import net.pretronic.libraries.synchronisation.observer.UnusedObservable;

import java.util.Collections;
import java.util.List;

public class EmptyGraph<T> extends UnusedObservable<PermissionObject, SyncAction> implements Graph<T> {

    private static Graph EMPTY = new EmptyGraph();

    @Override
    public List<T> traverse() {
        return Collections.emptyList();
    }

    public static <R> Graph<R> newEmptyGraph(){
        return EMPTY;
    }
}
