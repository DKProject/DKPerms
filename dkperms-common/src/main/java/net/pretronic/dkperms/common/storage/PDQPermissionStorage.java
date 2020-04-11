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
import net.pretronic.dkperms.api.entity.PermissionGroupEntity;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;
import net.pretronic.dkperms.api.storage.PermissionStorage;
import net.pretronic.dkperms.common.entity.DefaultPermissionEntity;
import net.pretronic.dkperms.common.entity.DefaultPermissionGroupEntity;
import net.pretronic.dkperms.common.scope.data.ArrayScopeBasedDataList;
import net.pretronic.libraries.utility.Iterators;

import java.util.ArrayList;
import java.util.Collection;

public class PDQPermissionStorage implements PermissionStorage {

    private DatabaseCollection object_permissions;

    @Override
    public Collection<PermissionEntity> getPermissions(int objectId, PermissionScope scope) {
        Collection<PermissionEntity> permissions = new ArrayList<>();
        this.object_permissions.find()
                .where("ObjectId",objectId)
                .where("ScopeId",scope.getId())
                .execute().loadIn(permissions, entry
                -> new DefaultPermissionEntity(entry.getInt("Id"),entry.getString("Permission")
                        ,PermissionAction.of(entry.getInt("Action")),scope,entry.getLong("Timeout")));
        return permissions;
    }

    @Override
    public ScopeBasedDataList<PermissionEntity> getPermissions(int objectId, Collection<PermissionScope> scopes) {
        QueryResult result = object_permissions.find()
                .where("ObjectId",objectId)
                .whereIn("ScopeId", scopes, PermissionScope::getId)
                .orderBy("ScopeId", SearchOrder.ASC)
                .execute();
        return getPermissions(result,scopes);
    }

    @Override
    public ScopeBasedDataList<PermissionEntity> getAllPermissions(int objectId, Collection<PermissionScope> skipped) {
        QueryResult result = object_permissions.find()
                .where("ObjectId",objectId)
                .not(query -> query.whereIn("ScopeId", skipped, PermissionScope::getId))
                .orderBy("ScopeId", SearchOrder.ASC)
                .execute();
        return getPermissions(result,null);
    }

    private ScopeBasedDataList<PermissionEntity> getPermissions(QueryResult result, Collection<PermissionScope> scopes) {
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
            entities.add(new DefaultPermissionEntity(entry.getInt("Id")
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

    public void setCollections(DatabaseCollection object_permissions){
        this.object_permissions = object_permissions;
    }
}
