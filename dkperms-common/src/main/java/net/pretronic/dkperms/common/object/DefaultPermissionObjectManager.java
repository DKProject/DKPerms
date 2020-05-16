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

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionGroupTrack;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectManager;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.object.search.ObjectSearchQuery;
import net.pretronic.dkperms.api.object.search.ObjectSearchResult;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.libraries.caching.ArrayCache;
import net.pretronic.libraries.caching.Cache;
import net.pretronic.libraries.caching.CacheQuery;
import net.pretronic.libraries.caching.synchronisation.ShadowArraySynchronizableCache;
import net.pretronic.libraries.caching.synchronisation.SynchronizableCache;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.interfaces.Initializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DefaultPermissionObjectManager implements PermissionObjectManager, Initializable<DKPerms> {

    private static final int SUPER_ADMINISTRATOR_ACCOUNT_ID = 1;
    private static final String SUPER_ADMINISTRATOR_ACCOUNT_NAME = "Super Administrator";
    private static final String SERVICE_ACCOUNT_NAME = "SERVICE_ACCOUNT";

    private final SynchronizableCache<PermissionObject,Integer> objects;
    private final Cache<ObjectSearchResult> searchResults;
    private PermissionObject superAdministrator;
    private Collection<PermissionObjectType> types;
    private final Collection<PermissionGroupTrack> tracks;//Currently pre loaded, maybe change in future

    public DefaultPermissionObjectManager() {
        this.objects = new ShadowArraySynchronizableCache<>(1000);
        this.objects.setClearOnDisconnect(true);
        this.objects.setExpireAfterAccess(30,TimeUnit.MINUTES);

        this.objects.registerQuery("ByName",new ObjectNameLoader());
        this.objects.registerQuery("ById",new ObjectIdLoader(false));
        this.objects.registerQuery("ByIdOnlyCached",new ObjectIdLoader(true));
        this.objects.registerQuery("ByAssignment",new ObjectAssignmentLoader());
        this.objects.setIdentifierQuery((o, objects) -> o.getId() == (int)objects[0]);
        //@Todo remove listener

        this.searchResults = new ArrayCache<>(100);
        this.searchResults.setExpireAfterAccess(15, TimeUnit.MINUTES);
        this.searchResults.registerQuery("ByQuery",new ObjectQueryLoader());

        this.tracks = new ArrayList<>();
    }

    public SynchronizableCache<PermissionObject,Integer> getObjects() {
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
        return search().directLoading().withType(type).withScope(scope).execute();//@Todo remove direct loading
    }

    @Override
    public ObjectSearchResult getObjects(PermissionScope scope) {
        return search().withScope(scope).execute();
    }

    @Override
    public Collection<PermissionObject> getDefaultGroups(Collection<PermissionScope> range) {
        return search().hasMeta("default",true,range).directLoading().execute().getAll();//@Todo maybe optimize
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
        this.objects.getCaller().createAndIgnore(id, Document.newDocument());
    }

    @Override
    public void deleteObject(PermissionObject executor,PermissionObject object) {
        this.objects.remove(object);
        DKPerms.getInstance().getStorage().getObjectStorage().deleteObject(object.getId());
        this.objects.getCaller().createAndIgnore(object.getId(), Document.newDocument());
    }


    @Override
    public Collection<PermissionGroupTrack> getTracks() {
        return tracks;
    }

    @Override
    public Collection<PermissionGroupTrack> getTracks(PermissionScope scope) {
        return Iterators.filter(this.tracks, track -> track.getScope().equals(scope));
    }

    @Override
    public PermissionGroupTrack getTrack(int id) {
        return Iterators.findOne(this.tracks, track -> track.getId() == id);
    }

    @Override
    public PermissionGroupTrack getTrack(String name, PermissionScope scope) {
        Validate.notNull(name);
        return Iterators.findOne(this.tracks, track -> track.getName().equalsIgnoreCase(name) && track.getScope().equals(scope));
    }

    @Override
    public PermissionGroupTrack createTrack(String name, PermissionScope scope) {
        Validate.notNull(name,scope);
        if(!scope.isSaved()) scope.insert();
        if(getTrack(name,scope) != null) throw new IllegalArgumentException("A track with the name "+name+" does already exist");
        int id = DKPerms.getInstance().getStorage().getTrackStorage().createTrack(name,scope.getId());
        PermissionGroupTrack track = new DefaultPermissionGroupTrack(id,name,scope,new ArrayList<>());
        this.tracks.add(track);
        return track;
    }

    @Override
    public void deleteTrack(PermissionGroupTrack track) {
        Validate.notNull(track);
        DKPerms.getInstance().getStorage().getTrackStorage().deleteTrack(track.getId());
        this.tracks.remove(track);
    }

    @Override
    public void sync() {
        for (PermissionObject object : this.objects.getCachedObjects()) {
            if(object instanceof DefaultPermissionObject){
                ((DefaultPermissionObject) object).clearCache();
            }
        }
        this.searchResults.clear();
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
        public boolean check(PermissionObject o, Object[] objects) {//name,scope,type
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

        private final boolean onlyCached;

        public ObjectIdLoader(boolean onlyCached) {
            this.onlyCached = onlyCached;
        }

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
            if(onlyCached) return null;
            return DKPerms.getInstance().getStorage().getObjectStorage().getObject((int) identifiers[0]);
        }
    }

    private static final class ObjectAssignmentLoader implements CacheQuery<PermissionObject> {

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
