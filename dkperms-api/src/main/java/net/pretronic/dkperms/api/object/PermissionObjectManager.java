/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.object;

import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.object.search.ObjectSearchResult;
import net.pretronic.dkperms.api.object.search.ObjectSearchQuery;
import net.pretronic.dkperms.api.scope.PermissionScope;

import java.util.Collection;
import java.util.UUID;

public interface PermissionObjectManager {

    Collection<PermissionObjectType> getTypes();

    PermissionObjectType getType(int id);

    PermissionObjectType getType(String name);

    default PermissionObjectType getTypeOrCreate(String name, boolean group){
        return getTypeOrCreate(getSuperAdministrator(),name,group);
    }

    PermissionObjectType getTypeOrCreate(PermissionObject executor,String name, boolean group);

    PermissionObjectType createType(PermissionObject executor,String name, boolean group);

    void deleteType(PermissionObject executor, int id);

    void deleteType(PermissionObject executor,String name);


    PermissionObject getSuperAdministrator();

    PermissionObject getObject(int id);

    PermissionObject getObject(String name,PermissionScope scope,PermissionObjectType type);

    PermissionObject getObjectByAssignment(UUID assignmentId);


    ObjectSearchResult getObjects(String name);

    ObjectSearchResult getObjects(String name,PermissionObjectType type);


    ObjectSearchResult getObjects(PermissionObjectType type);

    ObjectSearchResult getObjects(PermissionObjectType type, PermissionScope scope);

    ObjectSearchResult getObjects(PermissionScope scope);


    default Collection<PermissionObject> getDefaultGroups(Graph<PermissionScope> range){
        return getDefaultGroups(range.traverse());
    }

    Collection<PermissionObject> getDefaultGroups(Collection<PermissionScope> range);


    ObjectSearchQuery search();

    default PermissionObject createObject(PermissionScope scope, PermissionObjectType type,String name){
        return createObject(getSuperAdministrator(),scope, type, name,null);
    }

    default PermissionObject createObject(PermissionObject executor,PermissionScope scope, PermissionObjectType type,String name){
        return createObject(executor,scope, type, name,null);
    }

    PermissionObject createObject(PermissionObject executor,PermissionScope scope, PermissionObjectType type, String name,UUID assignmentId);

    default PermissionObject createObject(PermissionScope scope, PermissionObjectType type, String name,UUID assignmentId){
        return createObject(getSuperAdministrator(),scope,type,name,assignmentId);
    }

    default void deleteObject(int id){
        deleteObject(getSuperAdministrator(),id);
    }

    void deleteObject(PermissionObject executor,int id);

    default void deleteObject(PermissionObject object){
        deleteObject(getSuperAdministrator(),object);
    }

    void deleteObject(PermissionObject executor,PermissionObject object);



   /*
    Collection<PermissionGroupOrder> getOrders();

    PermissionGroupOrder getOrder(String name);

    PermissionGroupOrder getOrder(int id);

    PermissionGroupOrder createOrder(int id);

    PermissionGroupOrder createOrder(int id, PermissionScope scope);
    */
}
