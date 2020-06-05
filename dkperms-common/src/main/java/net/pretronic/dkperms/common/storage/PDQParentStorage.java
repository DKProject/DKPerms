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
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.entity.ParentEntity;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;
import net.pretronic.dkperms.api.storage.ParentStorage;
import net.pretronic.dkperms.common.entity.DefaultPermissionParentEntity;
import net.pretronic.dkperms.common.scope.data.ArrayScopeBasedDataList;
import net.pretronic.libraries.utility.Iterators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PDQParentStorage implements ParentStorage {

    private DatabaseCollection parent_entities;

    @Override
    public Collection<ParentEntity> getParentReferences(PermissionObject object, PermissionScope scope) {
        List<ParentEntity> entities = new ArrayList<>();
        parent_entities.find()
                .where("ObjectId",object.getId())
                .where("ScopeId",scope.getId())
                .execute()
                .loadIn(entities, entry
                -> new DefaultPermissionParentEntity(object,entry.getInt("Id")
                ,DKPerms.getInstance().getObjectManager().getObject(entry.getInt("ParentId"))
                ,PermissionAction.of(entry.getInt("Action")),scope,entry.getLong("Timeout")));
        return entities;
    }

    @Override
    public ScopeBasedDataList<ParentEntity> getParentReferences(PermissionObject object, Collection<PermissionScope> scope) {
        if(scope.isEmpty()) return new ArrayScopeBasedDataList<>();
        QueryResult result = parent_entities.find()
                .where("ObjectId",object.getId())
                .whereIn("ScopeId", scope, PermissionScope::getId)
                .orderBy("ScopeId", SearchOrder.ASC)
                .execute();
        return getParentReferences(object,result,scope);
    }

    @Override
    public ScopeBasedDataList<ParentEntity> getAllParentReferences(PermissionObject object, Collection<PermissionScope> skipped) {
        QueryResult result = parent_entities.find()
                .where("ObjectId",object.getId())
                .not(query -> query.whereIn("ScopeId", skipped, PermissionScope::getId))
                .orderBy("ScopeId", SearchOrder.ASC)
                .execute();
        return getParentReferences(object,result,null);
    }

    private ScopeBasedDataList<ParentEntity> getParentReferences(PermissionObject object, QueryResult result, Collection<PermissionScope> scopes) {
        ScopeBasedDataList<ParentEntity> response = new ArrayScopeBasedDataList<>();

        PermissionScope last = null;
        Collection<ParentEntity> entities = null;
        for (QueryResultEntry entry : result) {
            int id = entry.getInt("ScopeId");
            if(last == null || last.getId() != id){
                if(scopes == null) last = DKPerms.getInstance().getScopeManager().getScope(id);
                else last = Iterators.findOne(scopes, scope1 -> scope1.getId() == id);

                entities = new ArrayList<>();
                response.put(last,entities);
            }
            entities.add(new DefaultPermissionParentEntity(object,entry.getInt("Id")
                    ,DKPerms.getInstance().getObjectManager().getObject(entry.getInt("ParentId"))
                    ,PermissionAction.of(entry.getInt("Action"))
                    ,last,entry.getLong("Timeout")));
        }
        return response;
    }

    @Override
    public int createParentReference(int objectId, int scopeId, int ParentId, PermissionAction action, long timeout) {
        return this.parent_entities.insert()
                .set("ObjectId",objectId)
                .set("ScopeId",scopeId)
                .set("ParentId",ParentId)
                .set("Action",action.ordinal())
                .set("Timeout",timeout)
                .executeAndGetGeneratedKeyAsInt("id");
    }

    @Override
    public void removeParentReference(int entityId) {
        this.parent_entities.delete()
                .where("Id")
                .execute();
    }

    @Override
    public void clearParentReferences(int objectId) {
        this.parent_entities.delete()
                .where("ObjectId",objectId)
                .execute();
    }

    @Override
    public void clearParentReferences(int objectId, int scopeId) {
        this.parent_entities.delete()
                .where("ObjectId",objectId)
                .where("ScopeId",scopeId)
                .execute();
    }

    @Override
    public void updateParentReference(int entityId, int scopeId, PermissionAction action, long timeout) {
        this.parent_entities.update()
                .set("ScopeId",scopeId)
                .set("Action",action.ordinal())
                .set("Timeout",timeout)
                .where("Id",entityId)
                .execute();
    }

    @Override
    public void updateParentReferenceScope(int entityId, int scopeId) {
        this.parent_entities.update()
                .set("ScopeId",scopeId)
                .where("Id",entityId)
                .execute();
    }

    @Override
    public void updateParentReferenceAction(int entityId, PermissionAction action) {
        this.parent_entities.update()
                .set("Action",action.ordinal())
                .where("Id",entityId)
                .execute();
    }

    @Override
    public void updateParentReferenceTimeout(int entityId, long timeout) {
        this.parent_entities.update()
                .set("Timeout",timeout)
                .where("Id",entityId)
                .execute();
    }

    @Override
    public void deleteTimedOutParentReferences() {
        this.parent_entities.delete()
                .whereHigher("Timeout",0)
                .whereLower("Timeout",System.currentTimeMillis()).execute();
    }

    public void setCollections(DatabaseCollection parent_entities){
        this.parent_entities = parent_entities;
    }

}
