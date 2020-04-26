/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 09.11.19, 20:18
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.storage;

import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.query.SearchOrder;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;
import net.pretronic.dkperms.common.object.meta.DefaultObjectMetaEntry;
import net.pretronic.dkperms.common.scope.data.ArrayScopeBasedDataList;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.entity.PermissionGroupEntity;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.storage.GroupStorage;
import net.pretronic.dkperms.common.entity.DefaultPermissionGroupEntity;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class PDQGroupStorage implements GroupStorage {

    private DatabaseCollection group_entities;

    @Override
    public Collection<PermissionGroupEntity> getGroupReferences(PermissionObject object, PermissionScope scope) {
        List<PermissionGroupEntity> entities = new ArrayList<>();
        group_entities.find()
                .where("ObjectId",object.getId())
                .where("ScopeId",scope.getId())
                .execute()
                .loadIn(entities, entry
                -> new DefaultPermissionGroupEntity(object,entry.getInt("Id")
                ,DKPerms.getInstance().getObjectManager().getObject(entry.getInt("GroupId"))
                ,PermissionAction.of(entry.getInt("Action")),scope,entry.getLong("Timeout")));
        return entities;
    }

    @Override
    public ScopeBasedDataList<PermissionGroupEntity> getGroupReferences(PermissionObject object, Collection<PermissionScope> scope) {
        QueryResult result = group_entities.find()
                .where("ObjectId",object.getId())
                .orderBy("ScopeId", SearchOrder.ASC)
                .execute();
        return getGroupReferences(object,result,scope);
    }

    @Override
    public ScopeBasedDataList<PermissionGroupEntity> getAllGroupReferences(PermissionObject object, Collection<PermissionScope> skipped) {
        QueryResult result = group_entities.find()
                .where("ObjectId",object.getId())
                .not(query -> query.whereIn("ScopeId", skipped, PermissionScope::getId))
                .orderBy("ScopeId", SearchOrder.ASC)
                .execute();
        return getGroupReferences(object,result,null);
    }

    private ScopeBasedDataList<PermissionGroupEntity> getGroupReferences(PermissionObject object,QueryResult result, Collection<PermissionScope> scopes) {
        ScopeBasedDataList<PermissionGroupEntity> response = new ArrayScopeBasedDataList<>();
        PermissionScope last = null;
        Collection<PermissionGroupEntity> entities = null;
        for (QueryResultEntry entry : result) {
            int id = entry.getInt("ScopeId");
            if(last == null || last.getId() != id){
                if(scopes == null) last = DKPerms.getInstance().getScopeManager().getScope(id);
                else last = Iterators.findOne(scopes, scope1 -> scope1.getId() == id);

                entities = new ArrayList<>();
                response.put(last,entities);
            }
            entities.add(new DefaultPermissionGroupEntity(object,entry.getInt("Id")
                    ,DKPerms.getInstance().getObjectManager().getObject(entry.getInt("GroupId"))
                    ,PermissionAction.of(entry.getInt("Action"))
                    ,last,entry.getLong("Timeout")));
        }
        return response;
    }

    @Override
    public int createGroupReference(int objectId, int scopeId, int groupId, PermissionAction action, long timeout) {
        return this.group_entities.insert()
                .set("ObjectId",objectId)
                .set("ScopeId",scopeId)
                .set("GroupId",groupId)
                .set("Action",action.ordinal())
                .set("Timeout",timeout)
                .executeAndGetGeneratedKeyAsInt("id");
    }

    @Override
    public void removeGroupReference(int entityId) {
        this.group_entities.delete()
                .where("Id")
                .execute();
    }

    @Override
    public void clearGroupReferences(int objectId) {
        this.group_entities.delete()
                .where("ObjectId",objectId)
                .execute();
    }

    @Override
    public void clearGroupReferences(int objectId, int scopeId) {
        this.group_entities.delete()
                .where("ObjectId",objectId)
                .where("ScopeId",scopeId)
                .execute();
    }

    @Override
    public void updateGroupReference(int entityId, int scopeId, PermissionAction action, long timeout) {
        this.group_entities.update()
                .set("ScopeId",scopeId)
                .set("Action",action.ordinal())
                .set("Timeout",timeout)
                .where("Id",entityId)
                .execute();
    }

    @Override
    public void updateGroupReferenceScope(int entityId, int scopeId) {
        this.group_entities.update()
                .set("ScopeId",scopeId)
                .where("Id",entityId)
                .execute();
    }

    @Override
    public void updateGroupReferenceAction(int entityId, PermissionAction action) {
        this.group_entities.update()
                .set("Action",action.ordinal())
                .where("Id",entityId)
                .execute();
    }

    @Override
    public void updateGroupReferenceTimeout(int entityId, long timeout) {
        this.group_entities.update()
                .set("Timeout",timeout)
                .where("Id",entityId)
                .execute();
    }

    public void setCollections(DatabaseCollection group_entities){
        this.group_entities = group_entities;
    }

}
