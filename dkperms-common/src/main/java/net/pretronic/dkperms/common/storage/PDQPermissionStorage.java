/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 11.11.19, 19:54
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
import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;
import net.pretronic.dkperms.api.storage.PermissionStorage;
import net.pretronic.dkperms.common.entity.DefaultPermissionEntity;
import net.pretronic.dkperms.common.scope.data.ArrayScopeBasedDataList;
import net.pretronic.libraries.utility.Iterators;

import java.util.ArrayList;
import java.util.Collection;

public class PDQPermissionStorage implements PermissionStorage {

    private DatabaseCollection object_permissions;

    @Override
    public Collection<PermissionEntity> getPermissions(PermissionObject object, PermissionScope scope) {
        Collection<PermissionEntity> permissions = new ArrayList<>();
        this.object_permissions.find()
                .where("ObjectId",object.getId())
                .where("ScopeId",scope.getId())
                .execute().loadIn(permissions, entry
                -> new DefaultPermissionEntity(object,entry.getInt("Id"),entry.getString("Permission")
                        ,PermissionAction.of(entry.getInt("Action")),scope,entry.getLong("Timeout")));
        return permissions;
    }

    @Override
    public ScopeBasedDataList<PermissionEntity> getPermissions(PermissionObject object, Collection<PermissionScope> scopes) {
        QueryResult result = object_permissions.find()
                .where("ObjectId",object.getId())
                .whereIn("ScopeId", scopes, PermissionScope::getId)
                .orderBy("ScopeId", SearchOrder.ASC)
                .execute();
        return getPermissions(object,result,scopes);
    }

    @Override
    public ScopeBasedDataList<PermissionEntity> getAllPermissions(PermissionObject object, Collection<PermissionScope> skipped) {
        QueryResult result = object_permissions.find()
                .where("ObjectId",object.getId())
                .not(query -> query.whereIn("ScopeId", skipped, PermissionScope::getId))
                .orderBy("ScopeId", SearchOrder.ASC)
                .execute();
        return getPermissions(object,result,null);
    }

    private ScopeBasedDataList<PermissionEntity> getPermissions(PermissionObject object,QueryResult result, Collection<PermissionScope> scopes) {
        ScopeBasedDataList<PermissionEntity> response = new ArrayScopeBasedDataList<>();
        PermissionScope last = null;
        Collection<PermissionEntity> entities = null;
        for (QueryResultEntry entry : result) {
            int id = entry.getInt("ScopeId");
            if(last == null || last.getId() != id){
                if(scopes == null) last = DKPerms.getInstance().getScopeManager().getScope(id);
                else last = Iterators.findOne(scopes, scope1 -> scope1.getId() == id);
                entities = new ArrayList<>();
                response.put(last,entities);
            }
            entities.add(new DefaultPermissionEntity(object,entry.getInt("Id")
                    ,entry.getString("Permission")
                    ,PermissionAction.of(entry.getInt("Action"))
                    ,last,entry.getLong("Timeout")));
        }
        return response;
    }

    @Override
    public int addPermission(int objectId, int scopeId, String permission, PermissionAction action, long timeout) {
        return object_permissions.insert()
                .set("ObjectId",objectId)
                .set("ScopeId",scopeId)
                .set("Permission",permission)
                .set("Action",action.ordinal())
                .set("Timeout",timeout)
                .executeAndGetGeneratedKeyAsInt("id");
    }

    @Override
    public void deletePermissions(int entityId) {
        object_permissions.delete()
                .where("Id",entityId)
                .execute();
    }

    @Override
    public void clearPermissions(int objectId, int scopeId) {
        object_permissions.delete()
                .where("ObjectId",objectId)
                .where("ScopeId",scopeId)
                .execute();
    }

    @Override
    public void clearPermissions(int objectId) {
        object_permissions.delete()
                .where("ObjectId",objectId)
                .execute();
    }

    @Override
    public void updatePermission(int entityId, int scopeId, PermissionAction action, long timeout) {
        object_permissions.update()
                .set("ScopeId",scopeId)
                .set("Action",action.ordinal())
                .set("Timeout",timeout)
                .where("Id",entityId)
                .execute();
    }

    @Override
    public void updatePermissionAction(int entityId, PermissionAction action) {
        object_permissions.update()
                .set("Action",action.ordinal())
                .where("Id",entityId)
                .execute();
    }

    @Override
    public void updatePermissionScope(int entityId, int scopeId) {
        object_permissions.update()
                .set("ScopeId",scopeId)
                .where("Id",entityId)
                .execute();
    }

    @Override
    public void updatePermissionTimeout(int entityId, long timeout) {
        object_permissions.update()
                .set("Timeout",timeout)
                .where("Id",entityId)
                .execute();
    }

    @Override
    public void deleteTimedOutPermissions() {
        this.object_permissions.delete()
                .whereHigher("Timeout",0)
                .whereLower("Timeout",System.currentTimeMillis()).execute();
    }

    public void setCollections(DatabaseCollection object_permissions){
        this.object_permissions = object_permissions;
    }
}
