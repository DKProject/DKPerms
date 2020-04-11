/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 29.02.20, 20:47
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.graph.scope;

import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.graph.Graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ScopeGraph implements Graph<PermissionScope> {

    private final PermissionScope start;
    private final PermissionScope end;

    public ScopeGraph(PermissionScope start, PermissionScope end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public List<PermissionScope> traverse() {
        List<PermissionScope> result = new ArrayList<>();
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

        return result;
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
