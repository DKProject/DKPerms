/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 02.11.19, 15:05
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.object.meta;

import net.prematic.libraries.utility.Iterators;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.meta.ObjectMeta;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.api.scope.PermissionScope;

import java.util.*;


//@Todo implement effective entries
public class DefaultObjectMeta implements ObjectMeta {

    private final PermissionObject object;
    private final Collection<CacheEntry> entries;
    private boolean loadedAll;

    public DefaultObjectMeta(PermissionObject object) {
        this.object = object;
        this.entries = new ArrayList<>();
        this.loadedAll = false;
    }

    @Override
    public ObjectMetaEntry get(String key) {
        return get(DKPerms.getInstance().getScopeManager().getRoot(),key);
    }

    @Override
    public ObjectMetaEntry get(PermissionScope scope, String key) {
        return Iterators.findOne(getEntries(scope), entry -> entry.getKey().equalsIgnoreCase(key));
    }

    @Override
    public ObjectMetaEntry getInheritance(PermissionScope scope, String key) {
        List<PermissionScope> scopes = DKPerms.getInstance().getScopeManager().getValidScopes(scope);
        loadScopes(scopes);
        for (int i = 0; i < scopes.size(); i++) {
            ObjectMetaEntry result = get(scope,key);
            if(result != null) return result;
        }
        return null;
    }

    @Override
    public ObjectMeta getEffective(PermissionScope scope, String key) {
        return null;
    }

    @Override
    public Collection<ObjectMetaEntry> getEntries() {
        loadAll();
        List<ObjectMetaEntry> entries = new ArrayList<>();
        this.entries.forEach(entry -> entries.addAll(entry.entries));
        return entries;
    }

    @Override
    public Collection<ObjectMetaEntry> getEntries(PermissionScope scope) {
        if(scope.isSaved()){
            CacheEntry entry = Iterators.findOne(this.entries, entry1 -> entry1.scopeId == scope.getId());
            if(entry == null){
                if(!loadedAll){
                    Collection<ObjectMetaEntry> result = DKPerms.getInstance().getStorage().getObjectStorage().getMetas(object.getId(),scope);
                    entry = new CacheEntry(scope.getId(),result);
                    this.entries.add(entry);
                }else return Collections.emptyList();
            }
            return entry.entries;
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<ObjectMetaEntry> getInheritanceEntries(PermissionScope scope) {
        List<PermissionScope> scopes = DKPerms.getInstance().getScopeManager().getValidScopes(scope);
        loadScopes(scopes);
        List<ObjectMetaEntry> entries = new ArrayList<>();
        scopes.forEach(scope1 -> {
            if(scope1.isSaved()) entries.addAll(getEntries(scope1));
        });
        return entries;
    }

    @Override
    public Collection<ObjectMetaEntry> getEffectiveEntries(PermissionScope scope, String key) {
        return null;
    }

    @Override
    public boolean contains(String key) {
        return get(key) != null;
    }

    @Override
    public boolean contains(PermissionScope scope, String key) {
        return get(scope,key) != null;
    }

    @Override
    public boolean containsInheritance(PermissionScope scope, String key) {
        return getInheritance(scope, key) != null;
    }

    @Override
    public boolean containsEffective(PermissionScope scope, String key) {
        return getEffectiveEntries(scope, key) != null;
    }

    @Override
    public ObjectMetaEntry set(String key, Object value) {
        return set(key,value, DKPerms.getInstance().getScopeManager().getRoot());
    }

    @Override
    public ObjectMetaEntry set(String key, Object value, PermissionScope scope) {
        Objects.requireNonNull(key,"Key can't be null");
        Objects.requireNonNull(value,"Value can't be null");
        Objects.requireNonNull(scope,"Scope can't be null");
        ObjectMetaEntry entry = get(scope,key);
        if(entry == null){
            int id = DKPerms.getInstance().getStorage().getObjectStorage().insertMeta(scope.getId(),object.getId(),key,value.toString());
            entry = new DefaultObjectMetaEntry(scope,id,key,value);
            CacheEntry cache = Iterators.findOne(this.entries, entry1 -> entry1.scopeId == scope.getId());
            if(cache == null){
                cache = new CacheEntry(scope.getId(),new ArrayList<>());
                this.entries.add(cache);
            }
            cache.entries.add(entry);
        }else entry.setValue(value);
        return entry;
    }

    @Override
    public void unset(String key) {
        unset(key, DKPerms.getInstance().getScopeManager().getRoot());
    }

    @Override
    public void unset(String key, PermissionScope scope) {
        ObjectMetaEntry entry = get(scope, key);
        if(entry != null){
            DKPerms.getInstance().getStorage().getObjectStorage().deleteMeta(entry.getId());
            CacheEntry cache = Iterators.findOne(this.entries, entry1 -> entry1.scopeId == scope.getId());
            if(cache != null) Iterators.removeOne(cache.entries, entry2 -> entry2.getKey().equalsIgnoreCase(key));
        }
    }

    @Override
    public void clear() {
        DKPerms.getInstance().getStorage().getObjectStorage().clearMetas(object.getId());
        this.entries.clear();
        this.loadedAll = true;
    }

    @Override
    public void clear(PermissionScope scope) {
        DKPerms.getInstance().getStorage().getObjectStorage().clearMetas(object.getId(),scope.getId());
        CacheEntry result = Iterators.findOne(this.entries, entry -> entry.scopeId == scope.getId());
        if(result == null) this.entries.add(new CacheEntry(scope.getId(),new ArrayList<>()));
        else result.entries.clear();
    }

    @Override
    public Iterator<ObjectMetaEntry> iterator() {
        return getEntries().iterator();
    }

    private void loadScopes(Collection<PermissionScope> scopes){

    }

    private void loadAll(){
        loadedAll = true;
    }

    private static class CacheEntry {

        private final int scopeId;
        private Collection<ObjectMetaEntry> entries;

        private CacheEntry(int scopeId, Collection<ObjectMetaEntry> entries) {
            this.scopeId = scopeId;
            this.entries = entries;
        }
    }
}
