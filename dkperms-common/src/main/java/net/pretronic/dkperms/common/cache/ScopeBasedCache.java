/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 09.11.19, 17:17
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.cache;

import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedData;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;
import net.pretronic.dkperms.common.scope.data.ArrayScopeBasedDataList;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public abstract class ScopeBasedCache<T> {

    private final ScopeBasedDataList<T> entries;
    private boolean loadedAll;

    public ScopeBasedCache() {
        this.entries = new ArrayScopeBasedDataList<>();
        this.loadedAll = false;
    }

    public ScopeBasedDataList<T> getAll(){
        if(!loadedAll){
            entries.addAll(loadAll(entries.getScopes()));
            loadedAll = true;
        }
        return entries;
    }

    public Collection<T> get(PermissionScope scope){
        if(scope.isSaved()){
            ScopeBasedData<T> entry = getEntry(scope);
            if(entry == null){
                Collection<T> result = load(scope);
                entry = new CacheEntry(scope,result!=null?result:new ArrayList<>());
                this.entries.add(entry);
            }
            return entry.getData();
        }else return Collections.emptyList();
    }

    public ScopeBasedDataList<T> get(Graph<PermissionScope> scopes){
        return get(scopes.traverse());
    }

    public ScopeBasedDataList<T> get(Collection<PermissionScope> scopes){
        ScopeBasedDataList<T> result = new ArrayScopeBasedDataList<>();
        scopes.removeIf(scope -> !scope.isSaved());

        for (ScopeBasedData<T> entry : entries) {
            PermissionScope scope = findScope(scopes,entry.getScope());
            if(scope != null){
                scopes.remove(scope);
                result.add(entry);
            }
        }

        if(!scopes.isEmpty()){
            ScopeBasedDataList<T> response = load(scopes);
            result.addAll(response);
            this.entries.addAll(response);
        }
        return result;
    }

    private PermissionScope findScope(Collection<PermissionScope> scopes, PermissionScope scope){
        for (PermissionScope scope0 : scopes) if(scope0.equals(scope)) return scope;
        return null;
    }

    public <O extends T> void insert(PermissionScope scope, O object){
        if(!scope.isSaved()) throw new IllegalArgumentException("Scope is not saved");
        ScopeBasedData<T> entry = getEntry(scope);
        if(entry == null){
            entry = new CacheEntry(scope,new ArrayList<>());
            this.entries.add(entry);
        }
        entry.getData().add(object);
    }

    public <O extends T> void remove(PermissionScope scope,O object){
        ScopeBasedData<T> entry = getEntry(scope);
        if(entry != null) entry.getData().remove(object);
    }

    private ScopeBasedData<T> getEntry(PermissionScope scope){
        return Iterators.findOne(this.entries, entry -> entry.getScope().equals(scope));
    }

    public void reset(){
        this.entries.clear();
        loadedAll = false;
    }

    public void reset(PermissionScope scope){
        Validate.notNull(scope);
        reset(scope.getId());
        loadedAll = false;
    }

    public void reset(int scopeId){
        Iterators.removeOne(this.entries, entry -> entry.getScope().getId() == scopeId);
        loadedAll = false;
    }

    public void clear(PermissionScope scope){
        ScopeBasedData<T> result = Iterators.findOne(this.entries, entry -> entry.getScope().equals(scope));
        if(result != null) result.getData().clear();
    }

    public void clear(){
        this.entries.clear();
        this.loadedAll = true;
    }

    public void clearCache(){
        this.entries.clear();
        this.loadedAll = false;
    }

    protected abstract Collection<T> load(PermissionScope scope);

    protected abstract ScopeBasedDataList<T> load(Collection<PermissionScope> scopes);

    protected abstract ScopeBasedDataList<T> loadAll(Collection<PermissionScope> skipped);


    private class CacheEntry implements ScopeBasedData<T> {

        private final PermissionScope scope;
        private final Collection<T> objects;

        public CacheEntry(PermissionScope scope, Collection<T> objects) {
            this.scope = scope;
            this.objects = objects;
        }

        @Override
        public PermissionScope getScope() {
            return scope;
        }

        @Override
        public Collection<T> getData() {
            return objects;
        }
    }
}
