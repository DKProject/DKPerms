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

import net.pretronic.dkperms.api.object.group.PermissionGroupOrder;
import net.pretronic.dkperms.api.object.search.ObjectSearchResult;
import net.pretronic.dkperms.api.object.search.ObjectSearcher;
import net.pretronic.dkperms.api.scope.PermissionScope;

import java.util.Collection;

public interface PermissionObjectManager {

    PermissionObjectType getType(int id);

    PermissionObjectType getType(String name);

    PermissionObjectType getTypeOrCreate(String name);

    PermissionObjectType createType(String name);

    boolean deleteType(int id);

    boolean deleteType(String name);


    PermissionObject getObject(int id);

    PermissionObject getObject(String name);

    <T extends PermissionObject> T getObject(int id,Class<?> objectClass);

    <T extends PermissionObject> T  getObject(String name,Class<?> objectClass);


    ObjectSearchResult getObjects(PermissionObjectType type);

    ObjectSearchResult getObjects(PermissionObjectType type, PermissionScope scope);

    ObjectSearchResult getObjects(PermissionScope scope);

    ObjectSearcher search();


    PermissionObject createObject(PermissionObjectType type, int id, String name);

    boolean deleteObject(int id);

    boolean deleteObject(PermissionObject object);



    Collection<PermissionGroupOrder> getOrders();

    PermissionGroupOrder getOrder(String name);

    PermissionGroupOrder getOrder(int id);

    PermissionGroupOrder createOrder(int id);

    PermissionGroupOrder createOrder(int id, PermissionScope scope);
}
