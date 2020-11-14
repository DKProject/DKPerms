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
import net.pretronic.databasequery.api.query.SearchOrder;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.databasequery.api.query.type.FindQuery;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.object.search.ObjectSearchQuery;
import net.pretronic.dkperms.api.object.search.ObjectSearchResult;
import net.pretronic.dkperms.api.object.search.sort.SortColumn;
import net.pretronic.dkperms.api.object.search.sort.SortOrder;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.common.object.DefaultPermissionObject;
import net.pretronic.dkperms.common.object.DefaultPermissionObjectManager;
import net.pretronic.dkperms.common.object.search.DirectObjectSearchResult;
import net.pretronic.libraries.utility.Validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

//@Todo check if caching is working
public class PDQObjectSearchQuery implements ObjectSearchQuery {

    private final FindQuery query;
    private final DatabaseCollection objectCollection;
    private final DatabaseCollection metaCollection;
    private final DatabaseCollection parentCollection;

    private boolean inheritance;
    private boolean directLoading;

    public PDQObjectSearchQuery(FindQuery query, DatabaseCollection objectCollection, DatabaseCollection metaCollection,DatabaseCollection parentCollection) {
        this.query = query;
        this.objectCollection = objectCollection;
        this.metaCollection = metaCollection;
        this.parentCollection = parentCollection;

        this.inheritance = false;
        this.directLoading = false;
    }

    public FindQuery getQuery() {
        return query;
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
        throw new UnsupportedOperationException("Inheritance searching is currently not supported");
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
    public ObjectSearchQuery hasParent(PermissionObject parent) {
        query.join(parentCollection).on(objectCollection,"Id",parentCollection,"ObjectId")
                .where("ParentId",parent.getId());
        return this;
    }

    @Override
    public ObjectSearchQuery hasParent(PermissionObject parent, PermissionScope scope) {
        query.join(parentCollection).on(objectCollection,"Id",parentCollection,"ObjectId").and(query -> query
                        .where("ParentId",parent.getId())
                        .where(parentCollection.getName()+".ScopeId"));
        return this;
    }

    @Override
    public ObjectSearchQuery hasParent(PermissionObject parent, Collection<PermissionScope> scopes) {
        query.join(parentCollection).on(objectCollection,"Id",parentCollection,"ObjectId").and(query -> query
                        .where("ParentId",parent.getId())
                        .whereIn("ParentId", scopes, (Function<PermissionScope, Object>) PermissionScope::getId));
        return this;
    }

    @Override
    public ObjectSearchQuery hasMeta(String key) {
        query.join(metaCollection).on(objectCollection,"Id",metaCollection,"ObjectId")
                .where("Key",key);
        return this;
    }

    @Override
    public ObjectSearchQuery hasMeta(String key, PermissionScope scope) {
        query.join(metaCollection).on(objectCollection,"Id",metaCollection,"ObjectId")
                .where("Key",key);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj instanceof PDQObjectSearchQuery){
            PDQObjectSearchQuery query = (PDQObjectSearchQuery) obj;
            return this.query.equals(query.getQuery()) && inheritance == query.inheritance;
        }
        return false;
    }

    @Override
    public ObjectSearchQuery hasMeta(String key, Object value) {
        query.join(metaCollection).on(objectCollection,"Id",metaCollection,"ObjectId")
                .where("Key",key)
                .where("Value",value.toString());
        return this;
    }

    @Override
    public ObjectSearchQuery hasMeta(String key, Object value, PermissionScope scope) {
        query.join(metaCollection).on(objectCollection,"Id",metaCollection,"ObjectId")
                .where("Key",key).where("Value",value.toString())
                .where(metaCollection.getName()+".ScopeId",scope.getId());
        return this;
    }

    @Override
    public ObjectSearchQuery hasMeta(String key, Object value, Graph<PermissionScope> scope) {
        return hasMeta(key, value, scope.traverse());
    }

    @Override
    public ObjectSearchQuery hasMeta(String key, Object value, Collection<PermissionScope> scope) {
        query.join(metaCollection).on(objectCollection,"Id",metaCollection,"ObjectId")
                .where("Key",key).where("Value",value.toString())
                .whereIn(metaCollection.getName() + ".ScopeId", scope,PermissionScope::getId);
        return this;
    }

    @Override
    public ObjectSearchQuery sortBy(String column, SortOrder order0) {
        Validate.notNull(column,order0);

        SearchOrder order = order0 == SortOrder.ASC ? SearchOrder.ASC : SearchOrder.DESC;

        if(column.equals(SortColumn.NAME)){
            query.orderBy("Name",order);
        }else if(column.equals(SortColumn.ID)){
            query.orderBy("Id",order);
        }else if(column.equals(SortColumn.PRIORITY)){
            query.orderBy("Priority",order);
        }else throw new IllegalArgumentException("Invalid order column");
        return this;
    }

    @Override
    public ObjectSearchQuery directLoading() {
        this.directLoading = true;
        return this;
    }

    @Override
    public ObjectSearchResult execute() {
        directLoading();//Auto enable, passive loading not integrated yet

        ObjectSearchResult cached = getObjectManager().getSearchResults().get("ByQuery",this);
        if(cached != null) return cached;
        if(directLoading){
            QueryResult result = query.execute();
            if(result.isEmpty()){
                return new DirectObjectSearchResult(this, Collections.emptyList());
            }
            List<PermissionObject> data = new ArrayList<>();
            for (QueryResultEntry entry : result) {
                int id = entry.getInt(objectCollection.getName()+".Id");
                PermissionObject object = getObjectManager().getObjects().get("ByIdOnlyCached",id);
                if(object == null) {
                    object = new DefaultPermissionObject(id
                            ,entry.getUniqueId("AssignmentId")
                            ,entry.getString("Name")
                            ,entry.getBoolean("Disabled")
                            ,entry.getInt("Priority")
                            ,DKPerms.getInstance().getObjectManager().getType(entry.getInt("TypeId"))
                            ,DKPerms.getInstance().getScopeManager().getScope(entry.getInt("ScopeId")));
                    getObjectManager().getObjects().insert(object);
                }
                data.add(object);
            }

            ObjectSearchResult searchResult = new DirectObjectSearchResult(this, data);
            getObjectManager().getSearchResults().insert(searchResult);
            return searchResult;
        }
        throw new UnsupportedOperationException("Currently is only direct loading supported");
    }

    private DefaultPermissionObjectManager getObjectManager(){
        return (DefaultPermissionObjectManager) DKPerms.getInstance().getObjectManager();
    }
}
