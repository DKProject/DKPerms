/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 04.05.20, 18:48
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.object.meta;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.graph.ObjectMetaGraph;
import net.pretronic.dkperms.api.logging.LogType;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.SyncAction;
import net.pretronic.dkperms.api.object.meta.ObjectMeta;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedData;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;
import net.pretronic.dkperms.common.cache.ObjectMetaCache;
import net.pretronic.dkperms.common.graph.DefaultObjectMetaGraph;
import net.pretronic.dkperms.common.graph.EmptyGraph;
import net.pretronic.dkperms.common.object.DefaultPermissionObject;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class DefaultObjectMeta implements ObjectMeta {

    private final DefaultPermissionObject object;
    private final ObjectMetaCache cache;

    public DefaultObjectMeta(DefaultPermissionObject object) {
        this.object = object;
        this.cache = new ObjectMetaCache(object);
    }

    public ObjectMetaCache getCache() {
        return cache;
    }

    @Override
    public List<ObjectMetaEntry> get(String key) {
        return get(key,object.getScope());
    }

    @Override
    public List<ObjectMetaEntry> get(String key, PermissionScope scope) {
        List<ObjectMetaEntry> result =  Iterators.filter(cache.get(scope), entry -> entry.getKey().equalsIgnoreCase(key));
        result.sort(DefaultObjectMetaGraph.PRIORITY_COMPARATOR);
        return result;
    }

    @Override
    public ScopeBasedDataList<ObjectMetaEntry> getEntries() {
        return cache.getAll();
    }

    @Override
    public ScopeBasedDataList<ObjectMetaEntry> getEntries(Graph<PermissionScope> scopes) {
        return cache.get(scopes);
    }

    @Override
    public Collection<ObjectMetaEntry> getEntries(PermissionScope scope) {
        return cache.get(scope);
    }

    @Override
    public boolean contains(String key) {
        return get(key) != null;
    }

    @Override
    public boolean contains(String key, PermissionScope scope) {
        return get(key,scope) != null;
    }

    @Override
    public ObjectMetaGraph newGraph(Graph<PermissionScope> scopes) {
        return new DefaultObjectMetaGraph(object,scopes, EmptyGraph.newEmptyGraph());
    }

    @Override
    public ObjectMetaGraph newInheritanceGraph(Graph<PermissionScope> scopes) {
        return new DefaultObjectMetaGraph(object,scopes,object.newEffectedGroupInheritanceGraph(scopes));
    }

    @Override
    public boolean isSet(String key, Object value) {
        ObjectMetaEntry entry = getHighest(key);
        return entry != null && entry.equalsValue(value);
    }

    @Override
    public boolean isSet(PermissionScope scope, String key, Object value) {
        ObjectMetaEntry entry = getHighest(key,scope);
        return entry != null && entry.equalsValue(value);
    }

    @Override
    public ObjectMetaEntry set(PermissionObject executor, String key, Object value, int priority, PermissionScope scope, long timeout) {
        Validate.notNull(key,value,scope);
        List<ObjectMetaEntry> entries = get(key,scope);
        if(entries.size() == 1){
            ObjectMetaEntry entry = entries.get(0);
            entry.update(executor,value,priority,scope,timeout);
            return entry;
        }else{
            if(!entries.isEmpty()){
                DKPerms.getInstance().getStorage().getObjectStorage().deleteMetaEntries(object.getId(),scope.getId(),key);
                for (ObjectMetaEntry entry : entries) {
                    DKPerms.getInstance().getAuditLog().createDeleteRecordAsync(executor, LogType.ENTITY_META,object,entry);
                }
            }
            return add(executor, key, value, priority, scope, timeout);
        }
    }

    @Override
    public ObjectMetaEntry add(PermissionObject executor, String key, Object value, int priority, PermissionScope scope, long timeout) {
        Validate.notNull(key,value,scope);
        if(!scope.isSaved()) scope.insert();
        int id = DKPerms.getInstance().getStorage().getObjectStorage().insertMeta(object.getId(),scope.getId(),key,value.toString(),priority,timeout);
        ObjectMetaEntry entry = new DefaultObjectMetaEntry(object,scope,id,key,value,priority,timeout);
        cache.insert(scope,entry);
        DKPerms.getInstance().getAuditLog().createCreateRecordAsync(executor, LogType.ENTITY_META,object,entry);
        synchronizeMeta(scope);
        return entry;
    }

    @Override
    public void unset(PermissionObject executor,String key, PermissionScope scope) {
        Validate.notNull(key,scope);
        List<ObjectMetaEntry> entries = get(key,scope);
        if(!entries.isEmpty()){
            DKPerms.getInstance().getStorage().getObjectStorage().deleteMetaEntries(object.getId(),scope.getId(),key);
            for(ObjectMetaEntry entry : entries){
                DKPerms.getInstance().getAuditLog().createDeleteRecordAsync(executor, LogType.ENTITY_META,object,entry);
                this.cache.remove(scope,entry);
            }
            synchronizeMeta(scope);
        }
    }

    @Override
    public void remove(PermissionObject executor, ObjectMetaEntry entry, PermissionScope scope) {
        if(entry.getOwner().equals(object)){
            DKPerms.getInstance().getStorage().getObjectStorage().deleteMetaEntry(object.getId());
            DKPerms.getInstance().getAuditLog().createDeleteRecordAsync(executor, LogType.ENTITY_META,object,entry);
            cache.remove(scope,entry);
            synchronizeMeta(scope);
        }
    }

    @Override
    public void clear(PermissionObject executor) {
        for (ScopeBasedData<ObjectMetaEntry> entries : getEntries()) {
            for (ObjectMetaEntry entry : entries.getData()) {
                DKPerms.getInstance().getAuditLog().createDeleteRecordAsync(executor, LogType.ENTITY_META,object,entry);
            }
        }
        DKPerms.getInstance().getStorage().getObjectStorage().clearMeta(object.getId());
        this.cache.clear();
        synchronizeMeta(null);
    }

    @Override
    public void clear(PermissionObject executor,PermissionScope scope) {
        for (ObjectMetaEntry entry : getEntries(scope)) {
            DKPerms.getInstance().getAuditLog().createDeleteRecordAsync(executor, LogType.ENTITY_META,object,entry);
        }

        DKPerms.getInstance().getStorage().getObjectStorage().clearMeta(object.getId(),scope.getId());
        cache.clear(scope);
        synchronizeMeta(scope);
    }

    @Override
    public Iterator<ObjectMetaEntry> iterator() {
        return getEntries().getAll().iterator();
    }

    private void synchronizeMeta(PermissionScope scope){
        Document data = Document.newDocument();
        if(scope != null) data.set("scope",scope.getId());
        object.executeSynchronisationUpdate(SyncAction.OBJECT_META_UPDATE,data);
    }
}
