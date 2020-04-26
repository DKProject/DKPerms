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

import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.synchronisation.SynchronisationCaller;
import net.pretronic.libraries.synchronisation.SynchronisationHandler;
import net.pretronic.libraries.utility.interfaces.Initializable;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.PermissionScopeBuilder;
import net.pretronic.dkperms.api.scope.PermissionScopeManager;
import net.pretronic.dkperms.common.graph.ScopeGraph;

import java.util.List;
import java.util.Objects;

public class DefaultPermissionScopeManager implements PermissionScopeManager, Initializable<DKPerms> {

    private PermissionScope root;
    private PermissionScope current;

    private final ScopeSynchronizer scopeSynchronizer;

    public DefaultPermissionScopeManager() {
        this.scopeSynchronizer = new ScopeSynchronizer();
    }

    public ScopeSynchronizer getScopeSynchronizer() {
        return scopeSynchronizer;
    }

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
        Objects.requireNonNull(scopeOrder,"Scope order can't be null");
        if(scopeOrder.startsWith("\\\\")) scopeOrder = scopeOrder.substring(2);
        String[] scopes = scopeOrder.split("[\\\\;=/]");
        PermissionScope last = root;
        if(scopes.length %2 != 0) throw new IllegalArgumentException("Invalid length of conditions");
        for (int i = 0; i < scopes.length; i+=2) last = last.getChild(scopes[i],scopes[i+1]);
        return last;
    }

    @Override
    public PermissionScope getScope(int id) {
        PermissionScope result = findScope(root,id);
        if(result == null){
            result = DKPerms.getInstance().getStorage().getScopeStorage().getScope(id);
        }
        return result;
    }

    private PermissionScope findScope(PermissionScope scope, int id){
        if(scope.getId() == id) return scope;
        if(scope.areChildrenLoaded()){
            for (PermissionScope child : scope.getChildren()) {
                PermissionScope result = findScope(child,id);
                if(result != null) return result;
            }
        }
        return null;
    }

    @Override
    public PermissionScopeBuilder newBuilder() {
        return new DefaultPermissionScopeBuilder(this.root);
    }

    @Override
    public Graph<PermissionScope> newGraph(PermissionScope start, PermissionScope end) {
        return new ScopeGraph(start, end);
    }

    @Override
    public void initialise(DKPerms dkPerms) {
        if(root != null) throw new IllegalArgumentException("ScopeManager is already initialised");
        List<PermissionScope> result = dkPerms.getStorage().getScopeStorage().loadScopes(null);
        if(result.isEmpty()){
            this.root = new DefaultPermissionScope(-1,"Root","Root",null);
            this.root.insert();
        }else{
            this.root = result.get(0);
            if(result.size() > 1) dkPerms.getLogger().warn("Found more then one root scopes, please fix your tree");
        }
    }

    public class ScopeSynchronizer implements SynchronisationHandler<PermissionScope,Integer> {

        @Override
        public SynchronisationCaller<Integer> getCaller() {
            return DefaultPermissionScope.SYNCHRONISATION_CALLER;
        }

        @Override
        public void onDelete(Integer identifier, Document data) {
            PermissionScope result = findScope(root,identifier);
            if(result instanceof DefaultPermissionScope) ((DefaultPermissionScope) result).scopeDeleted();
        }

        @Override
        public void onCreate(Integer identifier, Document data) {
            int parent = data.getInt("parent");
            if(parent > 0){
                PermissionScope result = findScope(root,parent);
                if(result != null && result.areChildrenLoaded()){
                    for (PermissionScope child : result.getChildren()) {
                        if(child.getKey().equalsIgnoreCase(data.getString("key"))
                                && child.getName().equalsIgnoreCase(data.getString("name"))){
                            if(child instanceof  DefaultPermissionScope){
                                ((DefaultPermissionScope) child).scopeInserted(identifier);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onUpdate(Integer identifier, Document data) {
            throw new UnsupportedOperationException("It is not possible to update a scope");
        }

        @Override
        public void init(SynchronisationCaller<Integer> caller) {
            DefaultPermissionScope.SYNCHRONISATION_CALLER = caller;
        }
    }
}
