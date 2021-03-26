/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 03.11.19, 19:20
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.storage;

import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.query.SearchOrder;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.databasequery.api.query.type.FindQuery;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.api.object.search.ObjectSearchQuery;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;
import net.pretronic.dkperms.api.storage.ObjectStorage;
import net.pretronic.dkperms.common.object.DefaultPermissionObject;
import net.pretronic.dkperms.common.object.DefaultPermissionObjectType;
import net.pretronic.dkperms.common.object.meta.DefaultObjectMetaEntry;
import net.pretronic.dkperms.common.scope.data.ArrayScopeBasedDataList;
import net.pretronic.libraries.utility.Iterators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;

public class PDQObjectStorage implements ObjectStorage {

    private DatabaseCollection object;
    private DatabaseCollection object_type;
    private DatabaseCollection object_meta;
    private DatabaseCollection parent;

    @Override
    public Collection<PermissionObjectType> getObjectTypes() {
        Collection<PermissionObjectType> types = new ArrayList<>();
        this.object_type.find().execute().loadIn(types, entry
                -> new DefaultPermissionObjectType(entry.getInt("Id")
                ,entry.getString("Name")
                ,entry.getString("DisplayName")
                ,entry.getBoolean("IsParentAble")));
        return types;
    }

    @Override
    public PermissionObjectType getObjectType(int id) {
        QueryResult result = this.object_type.find().where("Id",id).execute();
        if(!result.isEmpty()) result.first().to(entry
                -> new DefaultPermissionObjectType(entry.getInt("Id")
                ,entry.getString("Name")
                ,entry.getString("DisplayName")
                ,entry.getBoolean("IsParentAble")));
        return null;
    }

    @Override
    public PermissionObjectType getObjectType(String name) {
        QueryResult result = this.object_type.find().where("Name",name).execute();
        if(!result.isEmpty()) result.first().to( entry
                -> new DefaultPermissionObjectType(entry.getInt("Id")
                ,entry.getString("Name")
                ,entry.getString("DisplayName")
                ,entry.getBoolean("IsParentAble")));
        return null;
    }

    @Override
    public int createObjectType(String name,String displayName, boolean group) {
        return this.object_type.insert()
                .set("Name",name)
                .set("DisplayName",displayName)
                .set("IsParentAble",group)
                .executeAndGetGeneratedKeys("id").first().getInt("id");
    }

    @Override
    public void updateObjectType(int typeId, String name,String displayName) {
        this.object_type.update()
                .set("Name",name)
                .set("DisplayName",displayName)
                .where("Id",typeId)
                .execute();
    }

    @Override
    public void deleteObjectType(int id) {
        this.object_type.delete().where("Id",id).execute();
    }

    @Override
    public void deleteObjectType(String name) {
        this.object_type.delete().where("Name",name).execute();
    }

    @Override
    public PermissionObject getObject(int id) {
        QueryResult result = this.object.find().where("Id",id).limit(1).execute();
        return readPermissionObject(result);
    }

    @Override
    public PermissionObject getObjectByAssignment(UUID id) {
        QueryResult result = this.object.find().where("AssignmentId",id).limit(1).execute();
        return readPermissionObject(result);
    }

    private PermissionObject readPermissionObject(QueryResult result) {
        if(!result.isEmpty()) return result.first().to((Function<QueryResultEntry, PermissionObject>) entry
                -> new DefaultPermissionObject(entry.getInt("Id")
                ,entry.getUniqueId("AssignmentId")
                ,entry.getString("Name")
                ,entry.getBoolean("Disabled")
                ,entry.getInt("Priority")
                , DKPerms.getInstance().getObjectManager().getType(entry.getInt("TypeId"))
                ,DKPerms.getInstance().getScopeManager().getScope(entry.getInt("ScopeId"))));
        return null;
    }

    @Override
    public PermissionObject getObject(String name, PermissionScope scope, PermissionObjectType type) {
        QueryResult result = this.object.find()
                .where("Name",name)
                .where("ScopeId",scope.getId())
                .where("TypeId",type.getId())
                .limit(1).execute();
        if(!result.isEmpty()){
            QueryResultEntry entry = result.first();
            return new DefaultPermissionObject(entry.getInt("Id")
                    ,entry.getUniqueId("AssignmentId")
                    ,entry.getString("Name")
                    ,entry.getBoolean("Disabled")
                    ,entry.getInt("Priority")
                    ,type,scope);
        }
        return null;
    }

    @Override
    public ObjectSearchQuery createSearchQuery() {
        return new PDQObjectSearchQuery(object.find(),object,object_meta,parent);
    }

    @Override
    public Collection<PermissionObject> getObjects(String name, PermissionObjectType type, Collection<PermissionObject> skipp) {
        Collection<PermissionObject> response = new ArrayList<>();
        FindQuery query = this.object.find()
                .where("Name",name);
        if(type != null) query.where("TypeId",type.getId());
        if(!skipp.isEmpty()) query.not(query1 -> query1.whereIn("Id", skipp, (Function<PermissionObject, Object>) PermissionObject::getId));
        query.execute().loadIn(response, entry -> new DefaultPermissionObject(entry.getInt("Id")
                ,entry.getUniqueId("AssignmentId")
                ,entry.getString("Name")
                ,entry.getBoolean("Disabled")
                ,entry.getInt("Priority")
                ,DKPerms.getInstance().getObjectManager().getType(entry.getInt("TypeId"))
                ,DKPerms.getInstance().getScopeManager().getScope(entry.getInt("ScopeId"))));
        return response;
    }


    @Override
    public PermissionObject createObject(PermissionScope scope, PermissionObjectType type, String name, UUID assignmentId) {
        int id = this.object.insert()
                .set("TypeId",type.getId())
                .set("ScopeId",scope.getId())
                .set("Name",name)
                .set("Priority",0)
                .set("AssignmentId",assignmentId)
                .set("Disabled",false)
                .set("DisplayName",name)
                .executeAndGetGeneratedKeys("id").first().getInt("id");
        return new DefaultPermissionObject(id,assignmentId,name,false,0,type,scope);
    }

    @Override
    public void updateObjectName(int objectId, String name) {
        this.object.update()
                .set("Name",name)
                .where("Id",objectId)
                .execute();
    }

    @Override
    public void updateObjectType(int objectId, PermissionObjectType type) {
        this.object.update()
                .set("TypeId",type.getId())
                .where("Id",objectId)
                .execute();
    }

    @Override
    public void updateObjectScope(int objectId, PermissionScope scope) {
        this.object.update()
                .set("ScopeId",scope.getId())
                .where("Id",objectId)
                .execute();
    }

    @Override
    public void updateObjectPriority(int objectId, int priority) {
        this.object.update()
                .set("Priority",priority)
                .where("Id",objectId)
                .execute();
    }

    @Override
    public void updateObjectDisabled(int objectId, boolean disabled) {
        this.object.update()
                .set("Disabled",disabled)
                .where("Id",objectId)
                .execute();
    }

    @Override
    public void updateMetaTimeout(int objectId, long timeout) {
        this.object.update()
                .set("Timeout",timeout)
                .where("Id",objectId)
                .execute();
    }

    @Override
    public void deleteObject(int objectId) {
        this.parent.delete().where("ParentId",objectId).execute();
        this.parent.delete().where("ObjectId",objectId).execute();
        this.object.delete().where("Id",objectId).execute();
    }

    @Override
    public Collection<ObjectMetaEntry> getMetaEntries(PermissionObject object, PermissionScope scope) {
        Collection<ObjectMetaEntry> result = new ArrayList<>();
        this.object_meta.find()
                .where("ObjectId",object.getId())
                .where("ScopeId",scope.getId())
                .execute().loadIn(result, entry
                -> new DefaultObjectMetaEntry(object,scope
                ,entry.getInt("Id")
                ,entry.getString("Key")
                ,entry.getString("Value")
                ,entry.getInt("Priority")
                ,entry.getLong("Timeout")));
        return result;
    }

    @Override
    public ScopeBasedDataList<ObjectMetaEntry> getMetaEntries(PermissionObject object, Collection<PermissionScope> scopes) {
        QueryResult result = object_meta.find()
                .where("ObjectId",object.getId())
                .whereIn("ScopeId", scopes, PermissionScope::getId)
                .orderBy("ScopeId", SearchOrder.ASC)
                .execute();
        return getMetaEntries(object,result,scopes);
    }

    @Override
    public ScopeBasedDataList<ObjectMetaEntry> getAllMetaEntries(PermissionObject object, Collection<PermissionScope> skipped) {
        QueryResult result = object_meta.find()
                .where("ObjectId",object.getId())
                .not(query -> query.whereIn("ScopeId", skipped, PermissionScope::getId))
                .orderBy("ScopeId", SearchOrder.ASC)
                .execute();
        return getMetaEntries(object,result,null);
    }

    private ScopeBasedDataList<ObjectMetaEntry> getMetaEntries(PermissionObject object, QueryResult result, Collection<PermissionScope> scopes) {
        ScopeBasedDataList<ObjectMetaEntry> response = new ArrayScopeBasedDataList<>();
        PermissionScope last = null;
        Collection<ObjectMetaEntry> entities = null;
        for (QueryResultEntry entry : result) {
            int id = entry.getInt("ScopeId");
            if(last == null || last.getId() != id){
                if(scopes == null) last = DKPerms.getInstance().getScopeManager().getScope(id);
                else last = Iterators.findOne(scopes, scope1 -> scope1.getId() == id);

                entities = new ArrayList<>();
                response.put(last,entities);
            }
            entities.add(new DefaultObjectMetaEntry(object,last
                    ,entry.getInt("Id")
                    ,entry.getString("Key")
                    ,entry.getString("Value")
                    ,entry.getInt("Priority")
                    ,entry.getLong("Timeout")));
        }
        return response;
    }

    @Override
    public int insertMeta(int objectId, int scopeId, String key, String value, int priority, long timeout) {
        return this.object_meta.insert()
                .set("ScopeId",scopeId)
                .set("ObjectId",objectId)
                .set("Key",key)
                .set("Value",value)
                .set("Priority",priority)
                .set("Timeout",timeout)
                .executeAndGetGeneratedKeyAsInt("id");
    }

    @Override
    public void updateMeta(int metaId, int scopeId, String value, int priority, long timeout) {
        this.object_meta.update()
                .set("Priority",priority)
                .set("ScopeId",scopeId)
                .set("Value",value)
                .where("Id",metaId)
                .execute();
    }

    @Override
    public void updateMetaPriority(int metaId, int priority) {
        this.object_meta.update()
                .set("Priority",priority)
                .where("Id",metaId)
                .execute();
    }

    @Override
    public void updateMetaScope(int metaId, int scopeId) {
        this.object_meta.update()
                .set("ScopeId",scopeId)
                .where("Id",metaId)
                .execute();
    }

    @Override
    public void updateMetaValue(int metaId, String value) {
        this.object_meta.update()
                .set("Value",value)
                .where("Id",metaId)
                .execute();
    }

    @Override
    public void deleteMetaEntry(int objectId, String key, int scopeId) {
        this.object_meta.delete()
                .where("ObjectId",objectId)
                .where("ScopeId",scopeId)
                .where("Key",key)
                .execute();
    }

    @Override
    public void deleteMetaEntry(int entryId) {
        this.object_meta.delete()
                .where("Id",entryId)
                .execute();
    }

    @Override
    public void deleteMetaEntries(int objectId, int scopeId, String key) {
        this.object_meta.delete()
                .where("ObjectId",objectId)
                .where("ScopeId",scopeId)
                .where("Key",key)
                .execute();
    }

    @Override
    public void clearMeta(int objectId) {
        this.object_meta.delete()
                .where("ObjectId",objectId)
                .execute();
    }

    @Override
    public void clearMeta(int objectId, int scopeId) {
        this.object_meta.delete()
                .where("ObjectId",objectId)
                .where("ScopeId",scopeId)
                .execute();
    }

    @Override
    public void deleteTimedOutMetaEntries() {
        this.object_meta.delete()
                .whereHigher("Timeout",0)
                .whereLower("Timeout",System.currentTimeMillis()).execute();
    }

    public void setCollections(DatabaseCollection object, DatabaseCollection object_type, DatabaseCollection object_meta, DatabaseCollection parent){
        this.object = object;
        this.object_type = object_type;
        this.object_meta = object_meta;
        this.parent = parent;
    }
}
