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
import net.pretronic.dkperms.api.event.object.DKPermsPermissionObjectCreateEvent;
import net.pretronic.dkperms.api.event.object.DKPermsPermissionObjectDeleteEvent;
import net.pretronic.dkperms.api.logging.LogType;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectManager;
import net.pretronic.dkperms.api.object.PermissionObjectTrack;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.object.search.ObjectSearchQuery;
import net.pretronic.dkperms.api.object.search.ObjectSearchResult;
import net.pretronic.dkperms.api.object.search.sort.SortColumn;
import net.pretronic.dkperms.api.object.search.sort.SortOrder;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.libraries.caching.ArrayCache;
import net.pretronic.libraries.caching.Cache;
import net.pretronic.libraries.caching.CacheQuery;
import net.pretronic.libraries.caching.synchronisation.ShadowArraySynchronizableCache;
import net.pretronic.libraries.caching.synchronisation.SynchronizableCache;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.exception.OperationFailedException;
import net.pretronic.libraries.utility.interfaces.Initializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class DefaultPermissionObjectManager implements PermissionObjectManager, Initializable<DKPerms> {

    private static final int SUPER_ADMINISTRATOR_ACCOUNT_ID = 1;
    private static final String SUPER_ADMINISTRATOR_ACCOUNT_NAME = "Super Administrator";

    private final SynchronizableCache<PermissionObject,Integer> objects;
    private final Cache<ObjectSearchResult> searchResults;
    private final Collection<PermissionObjectTrack> tracks;//Currently pre loaded, maybe change in future

    private DKPerms dkperms;
    private PermissionObject superAdministrator;
    private Collection<PermissionObjectType> types;

    public DefaultPermissionObjectManager(Predicate<PermissionObject> removeListener) {
        this.objects = new ShadowArraySynchronizableCache<>(1000);
        this.objects.setClearOnDisconnect(true);
        this.objects.setExpireAfterAccess(10,TimeUnit.MINUTES);

        this.objects.registerQuery("ByName",new ObjectNameLoader());
        this.objects.registerQuery("ById",new ObjectIdLoader(false));
        this.objects.registerQuery("ByIdOnlyCached",new ObjectIdLoader(true));
        this.objects.registerQuery("ByAssignment",new ObjectAssignmentLoader());
        this.objects.setIdentifierQuery((o, objects) -> o.getId() == (int)objects[0]);
        this.objects.setRemoveListener(removeListener);

        this.searchResults = new ArrayCache<>(100);
        this.searchResults.setExpireAfterAccess(10, TimeUnit.MINUTES);
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
    public PermissionObjectType getTypeOrCreate(PermissionObject executor,String name,String displayName, boolean group) {
        PermissionObjectType type = getType(name);
        if(type == null) type = createType(executor,name,displayName,group);
        return type;
    }

    @Override
    public PermissionObjectType createType(PermissionObject executor,String name,String displayName, boolean group) {
        Validate.notNull(group,displayName,name);
        if(this.superAdministrator != null) Validate.notNull(executor);
        if(getType(name) != null) throw new IllegalArgumentException("The type with the name "+name+" does already exist");
        int id = dkperms.getStorage().getObjectStorage().createObjectType(name,displayName,group);
        PermissionObjectType type = new DefaultPermissionObjectType(id,name,displayName,group);
        this.types.add(type);
        dkperms.getAuditLog().createCreateRecordAsync(executor,LogType.OBJECT_TYPE,null,type);
        return type;
    }

    @Override
    public void deleteType(PermissionObject executor,int id) {
        Validate.notNull(executor);
        PermissionObjectType type = getType(id);
        if(type != null) deleteType(executor,type);
    }

    @Override
    public void deleteType(PermissionObject executor,String name) {
        Validate.notNull(executor);
        PermissionObjectType type = getType(name);
        if(type != null) deleteType(executor,type);
    }

    @Override
    public void deleteType(PermissionObject executor, PermissionObjectType type) {
        Validate.notNull(executor);
        if(type != null){
            dkperms.getStorage().getObjectStorage().deleteObjectType(type.getId());
            Iterators.removeOne(this.types, type0 -> type0.getId() == type.getId());
            dkperms.getAuditLog().createDeleteRecordAsync(executor,LogType.OBJECT_TYPE,null,type);
        }
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
    public Collection<PermissionObject> getDefaultGroups(Collection<PermissionScope> range) {
        return search().withType(PermissionObjectType.GROUP)
                .hasMeta("default",true,range)
                .directLoading().execute().getAll();
    }

    @Override
    public ObjectSearchQuery search() {
        return dkperms.getStorage().getObjectStorage().createSearchQuery();
    }

    @Override
    public PermissionObject createObject(PermissionObject executor,PermissionScope scope, PermissionObjectType type, String name) {
        return createObject(executor,scope, type, name,null);
    }

    @Override
    public PermissionObject createObject(PermissionObject executor,PermissionScope scope, PermissionObjectType type, String name, UUID assignmentId) {
        Validate.notNull(scope, type,name);
        if(superAdministrator != null) Validate.notNull(executor);
        if(!scope.isSaved()) scope.insert();
        if(getObject(name,scope,type) != null) throw new IllegalArgumentException("There is already an object with the same name in the same scope");
        PermissionObject object = DKPerms.getInstance().getStorage().getObjectStorage().createObject(scope,type,name,assignmentId);
        this.objects.insert(object);
        this.dkperms.getAuditLog().createCreateRecordAsync(executor == null ? object : executor,LogType.OBJECT,object,object);
        this.dkperms.getEventBus().callEvent(new DKPermsPermissionObjectCreateEvent(executor,object));
        return object;
    }

    @Override
    public void deleteObject(PermissionObject executor,int id) {
        Validate.notNull(executor);
        PermissionObject object = getObject(id);
        if(object == null) return;
        deleteObject(executor,object);
    }

    @Override
    public void deleteObject(PermissionObject executor,PermissionObject object) {
        this.objects.remove(object);
        this.dkperms.getStorage().getObjectStorage().deleteObject(object.getId());
        this.objects.getCaller().createAndIgnore(object.getId(), Document.newDocument());
        this.dkperms.getAuditLog().createDeleteRecordAsync(executor,LogType.OBJECT,object,object);
        this.dkperms.getEventBus().callEvent(new DKPermsPermissionObjectDeleteEvent(executor,object));
    }

    @Override
    public PermissionObjectTrack getPriorityTrack(PermissionObjectType type) {
        List<PermissionObject> result = search()
                .withType(type)
                .sortBy(SortColumn.PRIORITY, SortOrder.ASC)
                .directLoading().execute().getAll();
        return new PriorityObjectTrack(null,result);
    }

    @Override
    public PermissionObjectTrack getPriorityTrack(PermissionObjectType type, PermissionScope scope) {
        List<PermissionObject> result = search()
                .withType(type)
                .withScope(scope)
                .sortBy(SortColumn.PRIORITY, SortOrder.ASC)
                .directLoading().execute().getAll();
        return new PriorityObjectTrack(null,result);
    }

    @Override
    public Collection<PermissionObjectTrack> getTracks() {
        return tracks;
    }

    @Override
    public Collection<PermissionObjectTrack> getTracks(PermissionScope scope) {
        return Iterators.filter(this.tracks, track -> track.getScope().equals(scope));
    }

    @Override
    public PermissionObjectTrack getTrack(int id) {
        return Iterators.findOne(this.tracks, track -> track.getId() == id);
    }

    @Override
    public PermissionObjectTrack getTrack(String name, PermissionScope scope) {
        Validate.notNull(name);
        return Iterators.findOne(this.tracks, track -> track.getName().equalsIgnoreCase(name) && track.getScope().equals(scope));
    }

    @Override
    public PermissionObjectTrack createTrack(PermissionObject executor,String name, PermissionScope scope) {
        Validate.notNull(name,scope);
        if(!scope.isSaved()) scope.insert();
        if(getTrack(name,scope) != null) throw new IllegalArgumentException("A track with the name "+name+" does already exist");
        int id = dkperms.getStorage().getTrackStorage().createTrack(name,scope.getId());
        PermissionObjectTrack track = new DefaultPermissionObjectTrack(id,name,scope,new ArrayList<>());
        this.tracks.add(track);
        DKPerms.getInstance().getAuditLog().createCreateRecordAsync(executor, LogType.TRACK,null,track);
        return track;
    }

    @Override
    public void deleteTrack(PermissionObject executor,PermissionObjectTrack track) {
        Validate.notNull(track);
        dkperms.getStorage().getTrackStorage().deleteTrack(track.getId());
        this.tracks.remove(track);
        DKPerms.getInstance().getAuditLog().createDeleteRecordAsync(executor, LogType.TRACK,null,track);
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
        this.dkperms = dkPerms;
        this.types = dkPerms.getStorage().getObjectStorage().getObjectTypes();

        findOrCreateType(PermissionObjectType.SERVICE_ACCOUNT);
        findOrCreateType(PermissionObjectType.USER_ACCOUNT);
        findOrCreateType(PermissionObjectType.GROUP);

        this.superAdministrator = getObject(SUPER_ADMINISTRATOR_ACCOUNT_ID);
        if(this.superAdministrator == null){
            this.superAdministrator = createObject(dkPerms.getScopeManager().getRoot(),PermissionObjectType.USER_ACCOUNT,SUPER_ADMINISTRATOR_ACCOUNT_NAME);
        }

        this.tracks.addAll(DKPerms.getInstance().getStorage().getTrackStorage().getTracks());
    }

    private void findOrCreateType(PermissionObjectType builtInType){
        for (PermissionObjectType type : types) {
            if(type.getId() == builtInType.getId()){
                types.remove(type);
                types.add(builtInType);
                return;
            }
        }
        types.add(builtInType);
        int id = dkperms.getStorage().getObjectStorage()
                .createObjectType(builtInType.getName(),builtInType.getDisplayName(),builtInType.isParentAble());
        if(id != builtInType.getId()){
            throw new OperationFailedException("Builtin type structure mismatch (Contact the Pretronic Support)");
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
