/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 02.11.19, 13:26
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.object;

import net.prematic.libraries.caching.ArrayCache;
import net.prematic.libraries.caching.Cache;
import net.prematic.libraries.caching.CacheQuery;
import net.prematic.libraries.utility.Iterators;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectManager;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.object.group.PermissionGroupOrder;
import net.pretronic.dkperms.api.object.search.ObjectSearchResult;
import net.pretronic.dkperms.api.object.search.ObjectSearcher;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.storage.DKPermsStorage;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultPermissionObjectManager implements PermissionObjectManager {

    private final DKPermsStorage storage;
    private Collection<PermissionObjectType> types;
    private Cache<PermissionObject> objects;

    public DefaultPermissionObjectManager() {
        this.storage = DKPerms.getInstance().getStorage();
        this.types = new ArrayList<>();
        this.objects = new ArrayCache<>();
    }

    @Override
    public Collection<PermissionObjectType> getTypes() {
        return types;
    }

    @Override
    public PermissionObjectType getType(int id) {
        return Iterators.findOne(this.types, type -> type.getId() == id);
    }

    @Override
    public PermissionObjectType getType(String name) {
        return Iterators.findOne(this.types, type -> type.getName().equalsIgnoreCase(name));
    }

    @Override
    public PermissionObjectType getTypeOrCreate(String name) {
        PermissionObjectType type = getType(name);
        if(type == null) type = createType(name);
        return type;
    }

    @Override
    public PermissionObjectType createType(String name) {
        if(getType(name) != null) throw new IllegalArgumentException("The type with the name "+name+" does already exist");
        int id = DKPerms.getInstance().getStorage().getObjectStorage().createObjectType(name);
        PermissionObjectType type = new DefaultPermissionObjectType(id,name);
        this.types.add(type);
        return type;
    }

    @Override
    public void deleteType(int id) {
        DKPerms.getInstance().getStorage().getObjectStorage().deleteObjectType(id);
        Iterators.removeOne(this.types, type -> type.getId() == id);
    }

    @Override
    public void deleteType(String name) {
        DKPerms.getInstance().getStorage().getObjectStorage().deleteObjectType(name);
        Iterators.removeOne(this.types, type -> type.getName().equalsIgnoreCase(name));
    }

    @Override
    public PermissionObject getObject(int id) {//@Todo caching
        return DKPerms.getInstance().getStorage().getObjectStorage().getObject(id);
    }

    /*

    Namespace@Minecraft;Server@Server-1\\TestObject
    Minecraft\\TestObject

     */
    @Override
    public PermissionObject getObject(String name) {//@Todo caching
        return DKPerms.getInstance().getStorage().getObjectStorage().getObject(name,-1);
    }

    @Override
    public <T extends PermissionObject> T getObject(int id, Class<?> objectClass) {
        return null;
    }

    @Override
    public <T extends PermissionObject> T getObject(String name, Class<?> objectClass) {
        return null;
    }

    @Override
    public ObjectSearchResult getObjects(PermissionObjectType type) {
        return null;
    }

    @Override
    public ObjectSearchResult getObjects(PermissionObjectType type, PermissionScope scope) {
        return null;
    }

    @Override
    public ObjectSearchResult getObjects(PermissionScope scope) {
        return null;
    }

    @Override
    public ObjectSearcher search() {
        return null;
    }

    @Override
    public PermissionObject createObject(PermissionScope scope, PermissionObjectType type, String name) {
        if(!scope.isSaved()) scope.insert();
        return DKPerms.getInstance().getStorage().getObjectStorage().createObject(scope.getId(),type.getId(),name);
    }

    @Override
    public boolean deleteObject(int id) {
        return false;
    }

    @Override
    public boolean deleteObject(PermissionObject object) {
        return false;
    }



    @Override
    public Collection<PermissionGroupOrder> getOrders() {
        return null;
    }

    @Override
    public PermissionGroupOrder getOrder(String name) {
        return null;
    }

    @Override
    public PermissionGroupOrder getOrder(int id) {
        return null;
    }

    @Override
    public PermissionGroupOrder createOrder(int id) {
        return null;
    }

    @Override
    public PermissionGroupOrder createOrder(int id, PermissionScope scope) {
        return null;
    }

    public void initialise(){
        this.types = DKPerms.getInstance().getStorage().getObjectStorage().getObjectTypes();
    }

    private final class ObjectNameLoader implements CacheQuery<PermissionObject> {

        @Override
        public boolean check(PermissionObject o, Object[] objects) {
            return false;
        }

        @Override
        public void validate(Object[] identifiers) {
            if(identifiers.length != 1) throw new IllegalArgumentException("Invalid search identifier.");
        }

        @Override
        public PermissionObject load(Object[] identifiers) {
            return null;
        }
    }

}
