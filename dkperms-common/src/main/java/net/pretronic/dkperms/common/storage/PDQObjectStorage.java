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

import net.prematic.databasequery.api.DatabaseCollection;
import net.prematic.databasequery.api.aggregation.AggregationBuilder;
import net.prematic.databasequery.api.query.result.QueryResult;
import net.prematic.databasequery.api.query.result.QueryResultEntry;
import net.prematic.libraries.utility.map.Pair;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.storage.ObjectStorage;
import net.pretronic.dkperms.common.object.DefaultPermissionObject;
import net.pretronic.dkperms.common.object.DefaultPermissionObjectType;
import net.pretronic.dkperms.common.object.meta.DefaultObjectMetaEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

public class PDQObjectStorage implements ObjectStorage {

    private DatabaseCollection object;
    private DatabaseCollection object_type;
    private DatabaseCollection object_meta;

    @Override
    public Collection<PermissionObjectType> getObjectTypes() {
        Collection<PermissionObjectType> types = new ArrayList<>();
        this.object_type.find().execute().loadIn(types, entry
                -> new DefaultPermissionObjectType(entry.getInt("Id"),entry.getString("Name")));
        return types;
    }

    @Override
    public PermissionObjectType getObjectType(int id) {
        QueryResult result = this.object_type.find().where("Id",id).execute();
        if(!result.isEmpty()) result.first().to( entry
                -> new DefaultPermissionObjectType(entry.getInt("Id"),entry.getString("Name")));
        return null;
    }

    @Override
    public PermissionObjectType getObjectType(String name) {
        QueryResult result = this.object_type.find().where("Name",name).execute();
        if(!result.isEmpty()) result.first().to( entry
                -> new DefaultPermissionObjectType(entry.getInt("Id"),entry.getString("Name")));
        return null;
    }

    @Override
    public int createObjectType(String name) {
        return this.object_type.insert()
                .set("Name",name)
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
        QueryResult result = this.object.find().where("Id",id).execute();
        if(!result.isEmpty()) return result.first().to((Function<QueryResultEntry, PermissionObject>) entry
                -> new DefaultPermissionObject(entry.getInt("id"),entry.getString("Name")
                ,entry.getString("DisplayName"),entry.getString("Description")));
        return null;
    }

    @Override
    public PermissionObject getObject(String name, int scopeId) {
        QueryResult result = this.object.find()
                .where("Name",name)
                .where("ScopeId",scopeId)
                .execute();
        if(!result.isEmpty()) return result.first().to((Function<QueryResultEntry, PermissionObject>) entry
                -> new DefaultPermissionObject(entry.getInt("id"),entry.getString("Name")
                ,entry.getString("DisplayName"),entry.getString("Description")));
        return null;
    }

    @Override
    public PermissionObject createObject(int scopeId, int typeId, String name) {
        int id = this.object.insert()
                .set("TypeId",typeId)
                .set("ScopeId",scopeId)
                .set("Name",name)
                .set("DisplayName",name)
                .set("Description","")
                .executeAndGetGeneratedKeys("id").first().getInt("id");
        return new DefaultPermissionObject(id,name,name,"");
    }


    @Override
    public ObjectMetaEntry getMeta(int metaId) {
        return null;
    }

    @Override
    public Collection<ObjectMetaEntry> getMetas(int objectId) {
        return null;
    }

    @Override
    public Collection<ObjectMetaEntry> getMetas(int objectId, PermissionScope scope) {
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
    public Collection<Pair<PermissionScope, Collection<ObjectMetaEntry>>> getMetas(int objectId, PermissionScope... scopes) {
        return null;
    }

    @Override
    public Collection<Pair<PermissionScope, Collection<ObjectMetaEntry>>> getMetas(int objectId, Collection<PermissionScope> scopes) {
        return null;//@Todo
    }

    @Override
    public int insertMeta(int scopeId, int objectId, String key, String value) {
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
    public void deleteMeta(int metaId) {
        this.object_meta.delete()
                .where("MetaId",metaId)
                .execute();
    }

    @Override
    public void deleteMeta(int objectId, String key, int scopeId) {
        this.object_meta.delete()
                .where("ObjectId",objectId)
                .where("ScopeId",scopeId)
                .where("Key",key)
                .execute();
    }

    @Override
    public void clearMetas(int objectId) {
        this.object_meta.delete()
                .where("ObjectId",objectId)
                .execute();
    }

    @Override
    public void clearMetas(int objectId, int scopeId) {
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
