/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 14.03.20, 22:41
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.storage;

import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.databasequery.api.query.type.FindQuery;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.object.search.ObjectSearchQuery;
import net.pretronic.dkperms.api.object.search.ObjectSearchResult;
import net.pretronic.dkperms.api.object.search.sort.SortOrder;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.common.object.DefaultPermissionObject;
import net.pretronic.dkperms.common.object.DefaultPermissionObjectManager;
import net.pretronic.dkperms.common.object.search.DirectObjectSearchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PDQObjectSearchQuery implements ObjectSearchQuery {

    private final FindQuery query;
    private final DatabaseCollection objectCollection;
    private final DatabaseCollection metaCollection;

    private boolean inheritance;
    private boolean directLoading;

    public PDQObjectSearchQuery(FindQuery query, DatabaseCollection objectCollection, DatabaseCollection metaCollection) {
        this.query = query;
        this.objectCollection = objectCollection;
        this.metaCollection = metaCollection;

        this.inheritance = false;
        this.directLoading = false;
    }

    @Override
    public ObjectSearchQuery withName(String name) {
        query.where("Name",name);
        return this;
    }

    @Override
    public ObjectSearchQuery withType(PermissionObjectType type) {
        query.where("TypeId",type.getId());
        return this;
    }

    @Override
    public ObjectSearchQuery withScope(PermissionScope scope) {
        query.where("ScopeId",scope.getId());
        return this;
    }


    @Override
    public ObjectSearchQuery inScope(Graph<PermissionScope> scopeRange) {
        query.whereIn("ScopeId", scopeRange.traverse(), PermissionScope::getId);
        return this;
    }

    @Override
    public ObjectSearchQuery inheritance() {
        this.inheritance = true;
        return this;
    }

    @Override
    public ObjectSearchQuery hasPermission(String permission) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjectSearchQuery hasPermission(String permission, PermissionScope scope) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjectSearchQuery hasPermissionNot(String permission) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjectSearchQuery hasPermissionNot(String permission, PermissionScope scope) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjectSearchQuery isInGroup(PermissionObject group) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjectSearchQuery isInGroup(PermissionObject group, PermissionScope scope) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjectSearchQuery hasMeta(String key) {
        return null;
    }

    @Override
    public ObjectSearchQuery hasMeta(String key, PermissionScope scope) {
        return null;
    }

    @Override
    public ObjectSearchQuery hasMeta(String key, Object value) {
        query.join(metaCollection).on(objectCollection,"Id",metaCollection,"ObjectId")
                .where("Key",key).where("Value",value);
        return this;
    }

    @Override
    public ObjectSearchQuery hasMeta(String key, Object value, PermissionScope scope) {
        return null;
    }

    @Override
    public ObjectSearchQuery sortBy(String column, SortOrder order) {
        return null;
    }

    @Override
    public ObjectSearchQuery directLoading() {
        this.directLoading = true;
        return this;
    }

    @Override
    public ObjectSearchResult execute() {
        ObjectSearchResult cached = getObjectManager().getSearchResults().get("NyQuery",this);
        if(cached != null) return cached;
        if(directLoading){
            QueryResult result = query.execute();
            if(result.isEmpty()){
                return new DirectObjectSearchResult(this, Collections.emptyList());
            }
            List<PermissionObject> data = new ArrayList<>();
            for (QueryResultEntry entry : result) {
                int id = entry.getInt("id");
                PermissionObject object = getObjectManager().getObjects().get("ById",id);
                if(object == null) {
                    object = new DefaultPermissionObject(entry.getInt("Id")
                            ,entry.getUniqueId("AssignmentId")
                            ,entry.getString("Name")
                            ,entry.getBoolean("Disabled")
                            ,entry.getBoolean("Deleted")
                            ,DKPerms.getInstance().getObjectManager().getType(entry.getInt("Type"))
                            ,DKPerms.getInstance().getScopeManager().getScope(entry.getInt("ScopeId")));
                }
                data.add(object);
            }
            return new DirectObjectSearchResult(this, data);
        }
        throw new UnsupportedOperationException("Currently is only direct loading supported");
    }

    private DefaultPermissionObjectManager getObjectManager(){
        return (DefaultPermissionObjectManager) DKPerms.getInstance().getObjectManager();
    }
}
