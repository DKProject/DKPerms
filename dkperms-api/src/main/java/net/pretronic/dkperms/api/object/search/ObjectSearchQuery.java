/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.object.search;

import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.object.search.sort.SortOrder;
import net.pretronic.dkperms.api.scope.PermissionScope;

import java.util.Collection;

public interface ObjectSearchQuery {

    ObjectSearchQuery withName(String name);

    ObjectSearchQuery withType(PermissionObjectType type);

    ObjectSearchQuery withScope(PermissionScope scope);

    ObjectSearchQuery inScope(Graph<PermissionScope> scopeRange);

    ObjectSearchQuery inheritance();


    ObjectSearchQuery hasPermission(String permission);

    ObjectSearchQuery hasPermission(String permission, PermissionScope scope);

    ObjectSearchQuery hasPermissionNot(String permission);

    ObjectSearchQuery hasPermissionNot(String permission, PermissionScope scope);


    ObjectSearchQuery hasParent(PermissionObject group);

    ObjectSearchQuery hasParent(PermissionObject group, PermissionScope scope);

    default ObjectSearchQuery hasParent(PermissionObject parent, Graph<PermissionScope> scopes){
        return hasParent(parent,scopes.traverse());
    }

    ObjectSearchQuery hasParent(PermissionObject parent, Collection<PermissionScope> scopes);


    ObjectSearchQuery hasMeta(String key);

    ObjectSearchQuery hasMeta(String key, PermissionScope scope);

    ObjectSearchQuery hasMeta(String key, Object value);

    ObjectSearchQuery hasMeta(String key, Object value, PermissionScope scope);

    ObjectSearchQuery hasMeta(String key, Object value, Graph<PermissionScope> scope);

    ObjectSearchQuery hasMeta(String key, Object value, Collection<PermissionScope> scope);


    ObjectSearchQuery sortBy(String column, SortOrder order);


    ObjectSearchQuery directLoading();

    ObjectSearchResult execute();
}
