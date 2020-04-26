/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 03.11.19, 19:14
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.storage;

import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.query.Query;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.storage.ScopeStorage;
import net.pretronic.dkperms.common.scope.DefaultPermissionScope;

import java.util.ArrayList;
import java.util.List;

public class PDQScopeStorage implements ScopeStorage {

    private DatabaseCollection scope;

    @Override
    public List<PermissionScope> loadScopes(PermissionScope parent) {
        List<PermissionScope> scopes = new ArrayList<>();

        QueryResult result;
        if(parent == null) result = this.scope.find().whereIsNull("ParentId").execute();
        else result = this.scope.find().where("ParentId",parent.getId()).execute();

        result.loadIn(scopes, entry
                -> new DefaultPermissionScope(entry.getInt("Id")
                ,entry.getString("Key"),entry.getString("Name"),parent));
        return scopes;
    }

    @Override
    public PermissionScope getScope(int id) {
        QueryResult result = this.scope.find().where("Id",id).onlyOne().execute();
        if(!result.isEmpty()){
            QueryResultEntry entry = result.first();
            PermissionScope parent = DKPerms.getInstance().getScopeManager().getScope(entry.getInt("ParentId"));
            if(parent != null){
                if(parent.areChildrenLoaded()) throw new IllegalArgumentException("Scope loading and cache mismatch");
                else{
                    PermissionScope scope = new DefaultPermissionScope(
                            entry.getInt("Id"),entry.getString("Key"),entry.getString("Name"),parent);
                    if(parent instanceof DefaultPermissionScope){
                        ((DefaultPermissionScope)parent).provideReversedLoadedScope(scope);
                    }else throw new UnsupportedOperationException("Unsupported scope reverse loading operation");
                    return scope;
                }
            }
        }
        return null;
    }

    @Override
    public int getScopeId(int parentId, String key, String name) {
        QueryResult result = this.scope.find().where("ParentId",parentId)
                .where("Key",key)
                .where("Name",name)
                .onlyOne().execute();
        if(!result.isEmpty()){
            return result.first().getInt("Id");
        }
        return -1;
    }

    @Override
    public int insertScope(int parentId, String key, String name) {
        return this.scope.insert()
                .set("Key",key)
                .set("Name",name)
                .set("ParentId",parentId==-1? null : parentId)
                .executeAndGetGeneratedKeys("id")
                .first().getInt("id");
    }

    @Override
    public void updateScopeName(int scopeId, String key, String name) {
        this.scope.update()
                .set("Key",key)
                .set("Name",name)
                .where("Id",scopeId)
                .execute();
    }

    @Override
    public void updateScopeParent(int scopeId, int parentId) {
        this.scope.update()
                .set("Parent",scopeId)
                .where("id",parentId)
                .execute();
    }

    @Override
    public void deleteScope(int scopeId) {
        this.scope.delete().where("Id",scopeId).execute();
    }

    public void setCollections(DatabaseCollection scope){
        this.scope = scope;
    }
}
