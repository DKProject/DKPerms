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

import net.pretronic.libraries.caching.ArrayCache;
import net.pretronic.libraries.caching.Cache;
import net.pretronic.libraries.caching.CacheQuery;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.interfaces.Initializable;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectManager;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.object.search.ObjectSearchResult;
import net.pretronic.dkperms.api.object.search.ObjectSearchQuery;
import net.pretronic.dkperms.api.scope.PermissionScope;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DefaultPermissionObjectManager implements PermissionObjectManager, Initializable<DKPerms> {

    private static final int SUPER_ADMINISTRATOR_ACCOUNT_ID = 1;
    private static final String SUPER_ADMINISTRATOR_ACCOUNT_NAME = "Super Administrator";
    private static final String SERVICE_ACCOUNT_NAME = "SERVICE_ACCOUNT";

    private final Cache<PermissionObject> objects;
    private final Cache<ObjectSearchResult> searchResults;
    private PermissionObject superAdministrator;
    private Collection<PermissionObjectType> types;

    public DefaultPermissionObjectManager() {
        this.objects = new ArrayCache<>(1000);
        this.objects.registerQuery("ByName",new ObjectNameLoader());
        this.objects.registerQuery("ById",new ObjectIdLoader());
        this.objects.registerQuery("ByAssignment",new ObjectAssignmentLoader());
        //@Todo remove listener

        this.searchResults = new ArrayCache<>(20);
        this.searchResults.setExpireAfterAccess(1, TimeUnit.HOURS);
        this.searchResults.registerQuery("ByQuery",new ObjectQueryLoader());
    }

    public Cache<PermissionObject> getObjects() {
        return objects;
    }

    public Cache<ObjectSearchResult> getSearchResults() {
        return searchResults;
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
    public PermissionObjectType getTypeOrCreate(PermissionObject executor,String name, boolean group) {
        PermissionObjectType type = getType(name);
        if(type == null) type = createType(executor,name,group);
        return type;
    }

    @Override
    public PermissionObjectType createType(PermissionObject executor,String name, boolean group) {
        if(getType(name) != null) throw new IllegalArgumentException("The type with the name "+name+" does already exist");
        int id = DKPerms.getInstance().getStorage().getObjectStorage().createObjectType(name,group);
        PermissionObjectType type = new DefaultPermissionObjectType(id,name,group);
        this.types.add(type);
        return type;
    }

    @Override
    public void deleteType(PermissionObject executor,int id) {
        DKPerms.getInstance().getStorage().getObjectStorage().deleteObjectType(id);
        Iterators.removeOne(this.types, type -> type.getId() == id);
    }

    @Override
    public void deleteType(PermissionObject executor,String name) {
        DKPerms.getInstance().getStorage().getObjectStorage().deleteObjectType(name);
        Iterators.removeOne(this.types, type -> type.getName().equalsIgnoreCase(name));
    }

    @Override
    public PermissionObject getSuperAdministrator() {
        return superAdministrator;
    }

    @Override
    public PermissionObject getObject(int id) {
        return objects.get("ById",id);
    }

    @Override
    public PermissionObject getObject(String name,PermissionScope scope,PermissionObjectType type) {
        Validate.notNull(name,"Name is null");
        Validate.notNull(scope,"Scope is null");
        Validate.notNull(type,"Type is null");
        return objects.get("ByName",name,scope,type);
    }

    @Override
    public PermissionObject getObjectByAssignment(UUID assignmentId) {
        Validate.notNull(assignmentId);
        return objects.get("ByAssignment",assignmentId);
    }

    @Override
    public ObjectSearchResult getObjects(String name) {
        Validate.notNull(name);
        return search().withName(name).directLoading().execute();
    }

    @Override
    public ObjectSearchResult getObjects(String name, PermissionObjectType type) {
        Validate.notNull(type,name);
        return search().withName(name).withType(type).directLoading().execute();
    }

    @Override
    public ObjectSearchResult getObjects(PermissionObjectType type) {
        return search().withType(type).execute();
    }

    @Override
    public ObjectSearchResult getObjects(PermissionObjectType type, PermissionScope scope) {
        return search().withType(type).withScope(scope).execute();
    }

    @Override
    public ObjectSearchResult getObjects(PermissionScope scope) {
        return search().withScope(scope).execute();
    }

    @Override
    public ObjectSearchQuery search() {
        return DKPerms.getInstance().getStorage().getObjectStorage().createSearchQuery();
    }

    @Override
    public PermissionObject createObject(PermissionObject executor,PermissionScope scope, PermissionObjectType type, String name) {
        return createObject(executor,scope, type, name,null);
    }

    @Override
    public PermissionObject createObject(PermissionObject executor,PermissionScope scope, PermissionObjectType type, String name, UUID assignmentId) {
        Validate.notNull(scope, type,name);
        if(!scope.isSaved()) scope.insert();
        PermissionObject object = DKPerms.getInstance().getStorage().getObjectStorage().createObject(scope,type,name,assignmentId);
        this.objects.insert(object);
        return object;
    }

    @Override
    public void deleteObject(PermissionObject executor,int id) {
        this.objects.remove("ById",id);
        DKPerms.getInstance().getStorage().getObjectStorage().deleteObject(id);
    }

    @Override
    public void deleteObject(PermissionObject executor,PermissionObject object) {
        this.objects.remove(object);
        DKPerms.getInstance().getStorage().getObjectStorage().deleteObject(object.getId());
    }

    @Override
    public void initialise(DKPerms dkPerms) {
        if(types != null) throw new IllegalArgumentException("ObjectManager is already initialised");
        this.types = dkPerms.getStorage().getObjectStorage().getObjectTypes();
        this.superAdministrator = getObject(SUPER_ADMINISTRATOR_ACCOUNT_ID);
        if(this.superAdministrator == null){
            PermissionObjectType type = getTypeOrCreate(SERVICE_ACCOUNT_NAME,false);
            this.superAdministrator = createObject(dkPerms.getScopeManager().getRoot(),type,SUPER_ADMINISTRATOR_ACCOUNT_NAME);
        }
    }

    private final static class ObjectNameLoader implements CacheQuery<PermissionObject> {

        @Override
        public boolean check(PermissionObject o, Object[] objects) {
            return o.getScope().getId() == ((PermissionScope) objects[1]).getId()
                    && o.getType().getId() == ((PermissionObjectType) objects[2]).getId()
                    && o.getName().equalsIgnoreCase((String) objects[0]);
        }

        @Override
        public void validate(Object[] identifiers) {
            if(identifiers.length != 3) throw new IllegalArgumentException("Invalid search identifier.");
        }

        @Override
        public PermissionObject load(Object[] identifiers) {
            return DKPerms.getInstance().getStorage().getObjectStorage().getObject((String) identifiers[0]
                    ,((PermissionScope)identifiers[1]),((PermissionObjectType)identifiers[2]));
        }
    }

    private final static class ObjectIdLoader implements CacheQuery<PermissionObject> {

        @Override
        public boolean check(PermissionObject o, Object[] objects) {
            return o.getId() == (int)objects[0];
        }

        @Override
        public void validate(Object[] identifiers) {
            if(identifiers.length != 1) throw new IllegalArgumentException("Invalid search identifier.");
        }

        @Override
        public PermissionObject load(Object[] identifiers) {
            return DKPerms.getInstance().getStorage().getObjectStorage().getObject((int) identifiers[0]);
        }
    }

    private final static class ObjectAssignmentLoader implements CacheQuery<PermissionObject> {

        @Override
        public boolean check(PermissionObject o, Object[] objects) {
            return o.getAssignmentId() != null && o.getAssignmentId().equals(objects[0]);
        }

        @Override
        public void validate(Object[] identifiers) {
            if(identifiers.length != 1) throw new IllegalArgumentException("Invalid search identifier.");
        }

        @Override
        public PermissionObject load(Object[] identifiers) {
            return DKPerms.getInstance().getStorage().getObjectStorage().getObjectByAssignment((UUID) identifiers[0]);
        }
    }

    private final static class ObjectQueryLoader implements CacheQuery<ObjectSearchResult> {

        @Override
        public boolean check(ObjectSearchResult result, Object[] objects) {
            return result.getQuery().equals(objects[0]);
        }

        @Override
        public void validate(Object[] identifiers) {
            if(identifiers.length != 1) throw new IllegalArgumentException("Invalid search identifier.");
        }
    }


}
