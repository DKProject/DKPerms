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
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.SyncAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.libraries.synchronisation.observer.UnusedObservable;
import net.pretronic.libraries.utility.SystemUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class ScopeGraph extends UnusedObservable<PermissionObject,SyncAction> implements Graph<PermissionScope> {

    private final PermissionScope start;
    private final PermissionScope end;

    private final List<PermissionScope> result;
    private boolean traversed;

    private final AtomicBoolean traversing = new AtomicBoolean(false);
    private final BooleanSupplier sleeper = traversing::get;

    public ScopeGraph(PermissionScope start, PermissionScope end) {
        this.start = start;
        this.end = end;
        this.result = new ArrayList<>();
    }

    @Override
    public List<PermissionScope> traverse() {
        if(traversing.get() || (!traversed && !traversing.compareAndSet(false,true))){
            SystemUtil.sleepAsLong(sleeper);
        }
        if(!traversed) traverse0();
        return Collections.unmodifiableList(result);
    }

    private void traverse0(){
        try{
            result.add(start);
            PermissionScope[] scopes = new PermissionScope[end.getLevel()-start.getLevel()];
            PermissionScope current = end;
            for (int i = scopes.length - 1; i >= 0; i--) {
                scopes[i] = current;
                current = current.getParent();
            }
            findValidScopes(start,0,scopes,result);

            result.sort((o1, o2) -> {
                if(o1.getLevel() > o2.getLevel()) return 1;
                else if(o1.getLevel() < o2.getLevel()) return -1;
                else{
                    int level1 = findScopeLevel(o1,scopes);
                    int level2 = findScopeLevel(o2,scopes);
                    return Integer.compare(level1, level2);
                }
            });
            traversed = true;
            traversing.set(false);
        }catch (Exception e){
            traversing.set(false);
            throw  e;
        }
    }

    private void findValidScopes(PermissionScope start, int index, PermissionScope[] current, Collection<PermissionScope> result){
        for (PermissionScope child : start.getChildren()) {
            int newIndex = getValidIndex(child,index,current);
            if(newIndex != -1){
                result.add(child);
                if(current.length > newIndex) findValidScopes(child,newIndex,current,result);
            }
        }
    }

    private int getValidIndex(PermissionScope scope, int index,PermissionScope[] current){
        for (int i = index; i < current.length; i++) {
            if(isSameType(scope,current[i])) return index+1;
        }
        return -1;
    }

    private int findScopeLevel(PermissionScope current, PermissionScope[] scopes){
        for (PermissionScope scope : scopes) {
            if(isSameType(scope,current)) return scope.getLevel();
        }
        return -1;
    }

    private boolean isSameType(PermissionScope scope1, PermissionScope scope2){
        return scope1.getName().equalsIgnoreCase(scope2.getName()) && scope1.getKey().equalsIgnoreCase(scope2.getKey());
    }
}
