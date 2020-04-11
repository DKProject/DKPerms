/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.object.meta;

import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.graph.ObjectMetaGraph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;

import java.util.Collection;

public interface ObjectMeta extends Iterable<ObjectMetaEntry> {

    ObjectMetaEntry get(String key);//From root

    ObjectMetaEntry get(String key,PermissionScope scope);

    ScopeBasedDataList<ObjectMetaEntry> getEntries();

    ScopeBasedDataList<ObjectMetaEntry> getEntries(Graph<PermissionScope> scopes);

    Collection<ObjectMetaEntry> getEntries(PermissionScope scope);


    ObjectMetaGraph newGraph(Graph<PermissionScope> scopes);

    ObjectMetaGraph newInheritanceGraph(Graph<PermissionScope> scopes);


    boolean contains(String key);

    boolean contains(String key,PermissionScope scope);


    boolean isSet(String key, Object value);

    boolean isSet(PermissionScope scope,String key, Object value);


    ObjectMetaEntry set(PermissionObject executor,String key, Object value);//to root

    ObjectMetaEntry set(PermissionObject executor,String key, Object value,PermissionScope scope);

    void unset(PermissionObject executor,String key);//from root

    void unset(PermissionObject executor,String key,PermissionScope scope);


    void clear(PermissionObject executor);

    void clear(PermissionObject executor,PermissionScope scope);

}
