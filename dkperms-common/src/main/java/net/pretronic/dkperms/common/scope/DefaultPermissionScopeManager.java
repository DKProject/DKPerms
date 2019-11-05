/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.scope;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.PermissionScopeBuilder;
import net.pretronic.dkperms.api.scope.PermissionScopeManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DefaultPermissionScopeManager implements PermissionScopeManager {

    private PermissionScope root;
    private PermissionScope current;

    @Override
    public PermissionScope getCurrentInstanceScope() {
        return current;
    }

    @Override
    public void setCurrentInstanceScope(PermissionScope scope) {
        Objects.requireNonNull(scope,"Scope is null");
        this.current = scope;
    }

    @Override
    public PermissionScope getRoot() {
        return root;
    }

    @Override
    public PermissionScope getNamespace(String name) {
        Objects.requireNonNull(name,"Name can't be null");
        return root.getChild(PermissionScope.NAMESPACE,name);
    }

    @Override
    public PermissionScope get(String scopeOrder) {
        Objects.requireNonNull(scopeOrder,"Scope order can't be null\"");
        String[] scopes = scopeOrder.split("[;=]");
        PermissionScope last = root;
        if(scopes.length %2 != 0) throw new IllegalArgumentException("Invalid length of conditions");
        for (int i = 0; i < scopes.length; i+=2) last = last.getChild(scopes[i],scopes[i+1]);
        return last;
    }

    @Override
    public PermissionScopeBuilder newBuilder() {
        return new DefaultPermissionScopeBuilder(this.root);
    }

    @Override
    public List<PermissionScope> getValidScopes(PermissionScope currentScope) {
        Objects.requireNonNull(currentScope,"Scope can't be null");
        List<PermissionScope> result = new ArrayList<>();
        result.add(root);
        PermissionScope[] scopes = new PermissionScope[currentScope.getLevel()];
        PermissionScope current = currentScope;
        for (int i = scopes.length - 1; i >= 0; i--) {
            scopes[i] = current;
            current = current.getParent();
        }
        findValidScopes(root,0,scopes,result);

        //@Todo update order
        result.sort((o1, o2) -> o1.getLevel() > o2.getLevel()?0:-1);

        return result;
    }

    private void findValidScopes(PermissionScope start, int index, PermissionScope[] current,Collection<PermissionScope> result){
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
            if(scope.getName().equalsIgnoreCase(current[i].getName()) && scope.getKey().equalsIgnoreCase(current[i].getKey())) {
                return index+1;
            }
        }
        return -1;
    }

    @Override
    public CompletableFuture<Collection<PermissionScope>> getValidScopesAsync(PermissionScope currentScope) {
        return DKPerms.getInstance().getExecutor().execute(() -> getValidScopes(currentScope));
    }

    @Override
    public void loadRootScope() {
        List<PermissionScope> result = DKPerms.getInstance().getStorage().getScopeStorage().loadScopes(null);
        if(result.isEmpty()){
            this.root = new DefaultPermissionScope(-1,"Root","Root",null);
            this.root.insert();
        }else{
            this.root = result.get(0);
            if(result.size() > 1) DKPerms.getInstance().getLogger().warn("Found more then one root scopes.");
        }
    }

}
