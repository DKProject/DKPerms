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

import net.pretronic.dkperms.api.object.SyncAction;
import net.pretronic.dkperms.common.object.DefaultPermissionObject;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.graph.ObjectMetaGraph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.meta.ObjectMeta;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;
import net.pretronic.dkperms.common.cache.ObjectMetaCache;
import net.pretronic.dkperms.common.graph.EmptyGraph;
import net.pretronic.dkperms.common.graph.DefaultObjectMetaGraph;

import java.util.*;

public class DefaultObjectMeta implements ObjectMeta {

    private final DefaultPermissionObject object;
    private final ObjectMetaCache cache;

    public DefaultObjectMeta(DefaultPermissionObject object) {
        this.object = object;
        this.cache = new ObjectMetaCache(object);
    }

    @Override
    public ObjectMetaEntry get(String key) {
        return get(key,object.getScope());
    }

    @Override
    public ObjectMetaEntry get(String key,PermissionScope scope) {
        return Iterators.findOne(getEntries(scope), entry -> entry.getKey().equalsIgnoreCase(key));
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
        ObjectMetaEntry entry = get(key);
        return entry != null && entry.equalsValue(value);
    }

    @Override
    public boolean isSet(PermissionScope scope, String key, Object value) {
        ObjectMetaEntry entry = get(key,scope);
        return entry != null && entry.equalsValue(value);
    }

    @Override
    public ObjectMetaEntry set(PermissionObject executor,String key, Object value) {
        return set(executor,key,value,object.getScope());
    }

    @Override
    public ObjectMetaEntry set(PermissionObject executor,String key, Object value, PermissionScope scope) {
        Validate.notNull(key,value,scope);
        ObjectMetaEntry entry = get(key,scope);
        if(entry == null){
            if(!scope.isSaved()) scope.insert();
            int id = DKPerms.getInstance().getStorage().getObjectStorage().insertMeta(object.getId(),scope.getId(),key,value.toString());
            entry = new DefaultObjectMetaEntry(object,scope,id,key,value);
            cache.insert(scope,entry);
        }else entry.setValue(executor,value);
        synchronizeMeta(scope);
        return entry;
    }

    @Override
    public void unset(PermissionObject executor,String key) {
        unset(executor,key, object.getScope());
    }

    @Override
    public void unset(PermissionObject executor,String key, PermissionScope scope) {
        ObjectMetaEntry entry = get(key,scope);
        if(entry != null){
            DKPerms.getInstance().getStorage().getObjectStorage().deleteMetaEntry(entry.getId());
            cache.remove(scope,entry);
            synchronizeMeta(scope);
        }
    }

    @Override
    public void clear(PermissionObject executor) {
        DKPerms.getInstance().getStorage().getObjectStorage().clearMeta(object.getId());
        this.cache.clear();
        synchronizeMeta(null);
    }

    @Override
    public void clear(PermissionObject executor,PermissionScope scope) {
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
