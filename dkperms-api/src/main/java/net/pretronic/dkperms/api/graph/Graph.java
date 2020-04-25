/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 18.01.20, 22:37
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.graph;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.SyncAction;
import net.pretronic.libraries.synchronisation.observer.Observable;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Graph<T> extends Iterable<T>, Observable<PermissionObject, SyncAction> {

    List<T> traverse();

    default CompletableFuture<List<T>> traverseAsync(){
        return DKPerms.getInstance().getExecutor().execute(this::traverse);
    }

    default T getFist(){
        return traverse().get(0);
    }

    default CompletableFuture<T> getFistAsync(){
        return DKPerms.getInstance().getExecutor().execute(this::getFist);
    }

    default T getLast(){
        List<T> result = traverse();
        return result.get(result.size()-1);
    }

    default CompletableFuture<T> getLastAsync(){
        return DKPerms.getInstance().getExecutor().execute(this::getLast);
    }

    @Override
    default Iterator<T> iterator() {
        return traverse().iterator();
    }

    default void subscribeObservers(){/*Optional*/}

    default void unsubscribeObservers(){/*Optional*/}
}
