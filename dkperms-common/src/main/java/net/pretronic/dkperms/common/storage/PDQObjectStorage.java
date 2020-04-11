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
import net.pretronic.databasequery.api.query.type.SearchQuery;
import net.pretronic.dkperms.api.object.search.ObjectSearchQuery;
import net.pretronic.dkperms.api.object.search.ObjectSearchResult;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.map.Pair;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.entity.PermissionGroupEntity;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;
import net.pretronic.dkperms.api.storage.ObjectStorage;
import net.pretronic.dkperms.common.entity.DefaultPermissionGroupEntity;
import net.pretronic.dkperms.common.object.DefaultPermissionObject;
import net.pretronic.dkperms.common.object.DefaultPermissionObjectType;
import net.pretronic.dkperms.common.object.meta.DefaultObjectMetaEntry;
import net.pretronic.dkperms.common.scope.data.ArrayScopeBasedDataList;

import java.util.*;
import java.util.function.Function;

public class PDQObjectStorage implements ObjectStorage {

    private DatabaseCollection object;
    private DatabaseCollection object_type;
    private DatabaseCollection object_meta;

    @Override
    public Collection<PermissionObjectType> getObjectTypes() {
        Collection<PermissionObjectType> types = new ArrayList<>();
        this.object_type.find().execute().loadIn(types, entry
                -> new DefaultPermissionObjectType(entry.getInt("Id")
                ,entry.getString("Name")
                ,entry.getBoolean("IsGroup")));
        return types;
    }

    @Override
    public PermissionObjectType getObjectType(int id) {
        QueryResult result = this.object_type.find().where("Id",id).execute();
        if(!result.isEmpty()) result.first().to(entry
                -> new DefaultPermissionObjectType(entry.getInt("Id")
                ,entry.getString("Name")
                ,entry.getBoolean("IsGroup")));
        return null;
    }

    @Override
    public PermissionObjectType getObjectType(String name) {
        QueryResult result = this.object_type.find().where("Name",name).execute();
        if(!result.isEmpty()) result.first().to( entry
                -> new DefaultPermissionObjectType(entry.getInt("Id")
                ,entry.getString("Name")
                ,entry.getBoolean("IsGroup")));
        return null;
    }

    @Override
    public int createObjectType(String name, boolean group) {
        return this.object_type.insert()
                .set("Name",name)
                .set("IsGroup",group)
                .executeAndGetGeneratedKeys("id").first().getInt("id");
    }

    @Override
    public void updateObjectType(int typeId, String name) {
        this.object_type.update()
                .set("Name",name)
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
        if(!result.isEmpty()) return result.first().to((Function<QueryResultEntry, PermissionObject>) entry
                -> new DefaultPermissionObject(entry.getInt("Id")
                ,entry.getUniqueId("AssignmentId")
                ,entry.getString("Name")
                ,entry.getBoolean("Disabled")
                ,entry.getBoolean("Deleted")
                ,DKPerms.getInstance().getObjectManager().getType(entry.getInt("Type"))
                ,DKPerms.getInstance().getScopeManager().getScope(entry.getInt("ScopeId"))));
        return null;
    }

    @Override
    public PermissionObject getObjectByAssignment(UUID id) {
        QueryResult result = this.object.find().where("AssignmentId",id).limit(1).execute();
        if(!result.isEmpty()) return result.first().to((Function<QueryResultEntry, PermissionObject>) entry
                -> new DefaultPermissionObject(entry.getInt("Id")
                ,entry.getUniqueId("AssignmentId")
                ,entry.getString("Name")
                ,entry.getBoolean("Disabled")
                ,entry.getBoolean("Deleted")
                ,DKPerms.getInstance().getObjectManager().getType(entry.getInt("TypeId"))
                ,DKPerms.getInstance().getScopeManager().getScope(entry.getInt("ScopeId"))));
        return null;
    }

    @Override
    public PermissionObject getObject(String name, PermissionScope scope, PermissionObjectType type) {
        QueryResult result = this.object.find()
                .where("Name",name)
                .where("ScopeId",scope.getId())
                .where("TypeId",type.getId())
                .where("Deleted",false)
                .limit(1).execute();
        if(!result.isEmpty()) return result.first().to((Function<QueryResultEntry, PermissionObject>) entry
                -> new DefaultPermissionObject(entry.getInt("Id")
                ,entry.getUniqueId("AssignmentId")
                ,entry.getString("Name")
                ,entry.getBoolean("Disabled")
                ,entry.getBoolean("Deleted")
                ,type,scope));
        return null;
    }

    @Override
    public ObjectSearchQuery createSearchQuery() {
        return new PDQObjectSearchQuery(object.find(),object,object_meta);
    }

    @Override
    public Collection<PermissionObject> getObjects(String name, PermissionObjectType type, Collection<PermissionObject> skipp) {
        Collection<PermissionObject> response = new ArrayList<>();
        FindQuery query = this.object.find()
                .where("Name",name)
                .where("Deleted",false);
        if(type != null) query.where("TypeId",type.getId());
        if(!skipp.isEmpty()) query.not(query1 -> query1.whereIn("Id", skipp, (Function<PermissionObject, Object>) PermissionObject::getId));
        query.execute().loadIn(response, entry -> new DefaultPermissionObject(entry.getInt("Id")
                ,entry.getUniqueId("AssignmentId")
                ,entry.getString("Name")
                ,entry.getBoolean("Disabled")
                ,entry.getBoolean("Deleted")
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
                .set("AssignmentId",assignmentId)
                .set("Disabled",false)
                .set("Deleted",false)
                .set("DisplayName",name)
                .set("Description","")
                .executeAndGetGeneratedKeys("id").first().getInt("id");
        return new DefaultPermissionObject(id,assignmentId,name,false,false,type,scope);
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
    public void updateObjectDisabled(int objectId, boolean disabled) {
        this.object.update()
                .set("Disabled",disabled)
                .where("Id",objectId)
                .execute();
    }

    @Override
    public void deleteObject(int objectId) {
        this.object.delete()
                .where("Id",objectId).limit(1)
                .execute();
    }

    @Override
    public ObjectMetaEntry getMetaEntry(int metaId) {
        QueryResult result = object_meta.find().where("Id",metaId).limit(1).execute();
        if(!result.isEmpty()){
            QueryResultEntry entry = result.first();
            return new DefaultObjectMetaEntry(
                    DKPerms.getInstance().getScopeManager().getScope(entry.getInt("ScopeId"))
                    ,entry.getInt("Id")
                    ,entry.getString("Key")
                    ,entry.getString("Value"));
        }
        return null;
    }

    @Override
    public Collection<ObjectMetaEntry> getMetaEntries(int objectId, PermissionScope scope) {
        Collection<ObjectMetaEntry> result = new ArrayList<>();
        this.object_meta.find()
                .where("ObjectId",objectId)
                .where("ScopeId",scope.getId())
                .execute().loadIn(result, entry
                -> new DefaultObjectMetaEntry(scope,entry.getInt("Id")
                ,entry.getString("Key"),entry.getString("Value")));
        return result;
    }

    @Override
    public ScopeBasedDataList<ObjectMetaEntry> getMetaEntries(int objectId, Collection<PermissionScope> scopes) {
        QueryResult result = object_meta.find()
                .where("ObjectId",objectId)
                .whereIn("ScopeId", scopes, PermissionScope::getId)
                .orderBy("ScopeId", SearchOrder.ASC)
                .execute();
        return getMetaEntries(result,scopes);
    }

    @Override
    public ScopeBasedDataList<ObjectMetaEntry> getAllMetaEntries(int objectId, Collection<PermissionScope> skipped) {
        System.out.println("SKIPPED: "+skipped);
        QueryResult result = object_meta.find()
                .where("ObjectId",objectId)
                .not(query -> query.whereIn("ScopeId", skipped, PermissionScope::getId))
                .orderBy("ScopeId", SearchOrder.ASC)
                .execute();
        return getMetaEntries(result,null);
    }

    private ScopeBasedDataList<ObjectMetaEntry> getMetaEntries(QueryResult result, Collection<PermissionScope> scopes) {
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
            entities.add(new DefaultObjectMetaEntry(last,entry.getInt("Id")
                    ,entry.getString("Key"),entry.getString("Value")));
        }
        return response;
    }

    @Override
    public int insertMeta(int objectId, int scopeId, String key, String value) {
        return this.object_meta.insert()
                .set("ScopeId",scopeId)
                .set("ObjectId",objectId)
                .set("Key",key)
                .set("Value",value)
                .executeAndGetGeneratedKeyAsInt("id");
    }

    @Override
    public void updateMeta(int metaId, String value) {
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

    public void setCollections(DatabaseCollection object, DatabaseCollection object_type, DatabaseCollection object_meta){
        this.object = object;
        this.object_type = object_type;
        this.object_meta = object_meta;
    }
}
