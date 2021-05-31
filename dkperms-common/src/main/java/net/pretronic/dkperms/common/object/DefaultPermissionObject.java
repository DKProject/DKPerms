/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 02.11.19, 13:25
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.object;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.TimeoutAble;
import net.pretronic.dkperms.api.entity.Entity;
import net.pretronic.dkperms.api.entity.ParentEntity;
import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.graph.ObjectGraph;
import net.pretronic.dkperms.api.graph.ParentGraph;
import net.pretronic.dkperms.api.graph.PermissionGraph;
import net.pretronic.dkperms.api.logging.LogAction;
import net.pretronic.dkperms.api.logging.LogType;
import net.pretronic.dkperms.api.object.*;
import net.pretronic.dkperms.api.object.meta.ObjectMeta;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.api.object.search.ObjectSearchResult;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.permission.analyse.PermissionAnalyser;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedData;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;
import net.pretronic.dkperms.common.cache.GroupCache;
import net.pretronic.dkperms.common.cache.PermissionCache;
import net.pretronic.dkperms.common.calculator.ParentCalculator;
import net.pretronic.dkperms.common.calculator.PermissionCalculator;
import net.pretronic.dkperms.common.entity.DefaultPermissionEntity;
import net.pretronic.dkperms.common.entity.DefaultPermissionParentEntity;
import net.pretronic.dkperms.common.graph.DefaultObjectGraph;
import net.pretronic.dkperms.common.graph.DefaultParentGraph;
import net.pretronic.dkperms.common.graph.DefaultPermissionGraph;
import net.pretronic.dkperms.common.graph.FilteredObjectGraph;
import net.pretronic.dkperms.common.object.meta.DefaultObjectMeta;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.message.bml.variable.describer.VariableObjectToString;
import net.pretronic.libraries.synchronisation.SynchronisationCaller;
import net.pretronic.libraries.synchronisation.Synchronizable;
import net.pretronic.libraries.synchronisation.UnconnectedSynchronisationCaller;
import net.pretronic.libraries.synchronisation.observer.AbstractObservable;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.annonations.Internal;
import net.pretronic.libraries.utility.map.Pair;

import java.util.*;
import java.util.function.Predicate;

public class DefaultPermissionObject extends AbstractObservable<PermissionObject,SyncAction> implements PermissionObject, VariableObjectToString, Synchronizable {

    public static SynchronisationCaller<Integer> SYNCHRONISATION_CALLER = new UnconnectedSynchronisationCaller<>(true);

    private final int id;
    private final UUID assignmentId;
    private String name;
    private int priority;
    private boolean disabled;

    private PermissionObjectType type;
    private PermissionScope scope;

    private Object holder;
    private PermissionObjectSnapshot currentSnapshot;

    private final DefaultObjectMeta meta;
    private final GroupCache groupCache;
    private final PermissionCache permissionCache;

    public DefaultPermissionObject(int id,UUID assignmentId, String name,boolean disabled,int priority,PermissionObjectType type, PermissionScope scope) {
        Validate.notNull(type,scope,name);
        if(id <= 0) throw new IllegalArgumentException("Received invalid object id");
        this.id = id;
        this.assignmentId = assignmentId;
        this.name = name;
        this.disabled = disabled;
        this.priority = priority;

        this.type = type;
        this.scope = scope;

        this.meta = new DefaultObjectMeta(this);
        this.groupCache = new GroupCache(this);
        this.permissionCache = new PermissionCache(this);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public UUID getAssignmentId() {
        return assignmentId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(PermissionObject executor,String name) {
        Validate.notNull(executor,name);
        DKPerms.getInstance().getStorage().getObjectStorage().updateObjectName(this.id,name);
        executeSynchronisationUpdate(SyncAction.OBJECT_NAME_UPDATE,Document.newDocument().set("name",name));
        DKPerms.getInstance().getAuditLog().createUpdateRecordAsync(executor, LogType.OBJECT,this,this,"Name",this.name,name);
        this.name = name;
    }

    @Override
    public String getPath() {
        return scope.getPath()+"\\"+name ;
    }

    @Override
    public PermissionObjectType getType() {
        return type;
    }

    @Override
    public void setType(PermissionObject executor,PermissionObjectType type) {
        Validate.notNull(executor,type);
        DKPerms.getInstance().getStorage().getObjectStorage().updateObjectType(this.id,type);
        executeSynchronisationUpdate(SyncAction.OBJECT_TYPE_UPDATE,Document.newDocument().set("type",type.getId()));
        DKPerms.getInstance().getAuditLog().createRecordAsync(executor, LogType.OBJECT, LogAction.UPDATE,this,this,"Type",this.type.getId(),type.getId());
        this.type = type;
    }

    @Override
    public int getPriority() {
        return priority;
    }


    @Override
    public void setPriority(PermissionObject executor, int priority) {
        Validate.notNull(executor);
        DKPerms.getInstance().getStorage().getObjectStorage().updateObjectPriority(this.id,priority);
        executeSynchronisationUpdate(SyncAction.OBJECT_PRIORITY_UPDATE,Document.newDocument().set("priority",priority));
        DKPerms.getInstance().getAuditLog().createRecordAsync(executor, LogType.OBJECT, LogAction.UPDATE,this,this,"Priority",this.priority,priority);
        this.priority = priority;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(PermissionObject executor,boolean disabled) {
        Validate.notNull(executor);
        DKPerms.getInstance().getStorage().getObjectStorage().updateObjectDisabled(this.id,disabled);
        executeSynchronisationUpdate(SyncAction.OBJECT_DISABLED_UPDATE,Document.newDocument().set("disabled",disabled));
        DKPerms.getInstance().getAuditLog().createRecordAsync(executor, LogType.OBJECT, LogAction.UPDATE,this,this,"Disabled",this.disabled,disabled);
        this.disabled = disabled;
    }

    @Override
    public PermissionScope getScope() {
        return scope;
    }

    @Override
    public void setScope(PermissionObject executor,PermissionScope scope) {
        Validate.notNull(executor,scope);
        DKPerms.getInstance().getStorage().getObjectStorage().updateObjectScope(this.id,scope);
        executeSynchronisationUpdate(SyncAction.OBJECT_SCOPE_UPDATE,Document.newDocument().set("scope",scope.getId()));
        DKPerms.getInstance().getAuditLog().createRecordAsync(executor, LogType.OBJECT, LogAction.UPDATE,this,this,"Scope",this.scope.getId(),scope.getId());
        this.scope = scope;
    }

    @Override
    public Object getHolder() {
        if(holder == null && type.hasLocalHolderFactory()) holder = type.getLocalHolderFactory().create(this);
        return holder;
    }

    @Override
    public boolean isHolderAssigned() {
        return holder != null;
    }

    @Override
    public void setHolder(Object holder) {
        Validate.notNull(holder);
        this.holder = holder;
    }

    @Override
    public PermissionObjectSnapshot getCurrentSnapshot() {
        return currentSnapshot;
    }

    @Override
    public void setCurrentSnapshot(PermissionObjectSnapshot snapshot) {
        Validate.notNull(snapshot);
        this.currentSnapshot = snapshot;
    }

    @Override
    public PermissionObjectSnapshot newSnapshot(PermissionScope scope) {
        return new DefaultPermissionObjectSnapshot(this,scope);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getHolder(Class<?> holderClass) {
        return (T) getHolder();
    }

    @Override
    public ObjectMeta getMeta() {
        return meta;
    }

    // ----- Groups -----

    @Override
    public ScopeBasedDataList<ParentEntity> getAllParents() {
        return groupCache.getAll();
    }

    @Override
    public Collection<ParentEntity> getParents(PermissionScope scope) {
        Validate.notNull(scope);
        return groupCache.get(scope);
    }

    @Override
    public ScopeBasedDataList<ParentEntity> getParents(Graph<PermissionScope> range) {
        return groupCache.get(range);
    }

    @Override
    public PermissionObject getHighestParent(PermissionScope scope) {
        PermissionObject best = null;
        for (ParentEntity entity : getParents(scope)) {
            if(best == null || best.getPriority() < entity.getParent().getPriority()){
                best = entity.getParent();
            }
        }
        return best;
    }

    @Override
    public ParentGraph newParentGraph(Graph<PermissionScope> range) {
        return new DefaultParentGraph(this,range,false);
    }

    @Override
    public ParentGraph newParentInheritanceGraph(Graph<PermissionScope> range) {
        return new DefaultParentGraph(this,range,true);
    }

    @Override
    public ObjectGraph newEffectedParentGraph(Graph<PermissionScope> range) {
        return new DefaultObjectGraph(newParentGraph(range));
    }

    @Override
    public ObjectGraph newEffectedParentInheritanceGraph(Graph<PermissionScope> range) {
        return new DefaultObjectGraph(newParentInheritanceGraph(range));
    }

    @Override
    public ObjectGraph newEffectedGroupGraph(Graph<PermissionScope> range) {
        return new FilteredObjectGraph(newEffectedParentGraph(range),PermissionObjectType.GROUP);
    }

    @Override
    public ObjectGraph newEffectedGroupInheritanceGraph(Graph<PermissionScope> range) {
        return new FilteredObjectGraph(newEffectedParentInheritanceGraph(range),PermissionObjectType.GROUP);
    }

    @Override
    public ParentEntity getParent(PermissionScope scope, PermissionObject object) {
        Validate.notNull(scope,object);
        for (ParentEntity entity : getParents(scope)){
            if(entity.getParent().equals(object)) return entity;
        }
        return null;
    }

    @Override
    public ParentEntity setParent(PermissionObject executor, PermissionScope scope, PermissionObject group, PermissionAction action, long timeout) {
        Validate.notNull(executor,scope,group,action);
        if(group == this) throw new IllegalArgumentException("You can not add a group to it self");
        if(!this.scope.contains(scope)) throw new IllegalArgumentException("Scope must be an inheritance scope of main object scope");

        if(!scope.isSaved()) scope.insert();

        DKPerms.getInstance().getStorage().getParentStorage().clearParentReferences(id,scope.getId());
        this.groupCache.clear(scope);
        return insertPermissionGroupEntity(executor,scope, group, action, timeout);
    }

    @Override
    public ParentEntity addParent(PermissionObject executor, PermissionScope scope, PermissionObject group, PermissionAction action, long timeout) {
        Validate.notNull(executor,scope,group,action);
        if(hasParent(scope, group) != PermissionAction.NEUTRAL) throw new IllegalArgumentException("This group is already added");
        if(group == this) throw new IllegalArgumentException("You can not add a group to it self");
        if(!this.scope.contains(scope)) throw new IllegalArgumentException("Scope must be an inheritance scope of main object scope");
        if(!scope.isSaved()) scope.insert();
        return insertPermissionGroupEntity(executor,scope, group, action, timeout);
    }

    private ParentEntity insertPermissionGroupEntity(PermissionObject executor, PermissionScope scope, PermissionObject group, PermissionAction action, long timeout) {
        int entityId = DKPerms.getInstance().getStorage().getParentStorage()
                .createParentReference(id,scope.getId(),group.getId(),action,timeout);
        DefaultPermissionParentEntity entity =  new DefaultPermissionParentEntity(this,entityId,group,action,scope,timeout);
        groupCache.insert(scope,entity);
        synchronizeGroups(scope);
        DKPerms.getInstance().getAuditLog().createCreateRecordAsync(executor, LogType.ENTITY_PARENT,this,entity);
        return entity;
    }

    @Override
    public void removeParent(PermissionObject executor,PermissionScope scope, PermissionObject group) {
        Validate.notNull(scope,group);
        ParentEntity entity = getParent(scope,group);
        if(entity != null) removeParent(executor,entity);
        synchronizeGroups(scope);
    }

    @Override
    public void removeParent(PermissionObject executor, ParentEntity entity) {
        Validate.notNull(entity);
        this.groupCache.remove(entity.getScope(),entity);
        DKPerms.getInstance().getStorage().getParentStorage().removeParentReference(entity.getId());
        synchronizeGroups(entity.getScope());
        DKPerms.getInstance().getAuditLog().createDeleteRecordAsync(executor, LogType.ENTITY_PARENT,this,entity);
    }

    @Override
    public void clearParents(PermissionObject executor,PermissionScope scope) {
        Validate.notNull(scope);

        for (ParentEntity parent : getParents(scope)) {
            DKPerms.getInstance().getAuditLog().createDeleteRecordAsync(executor, LogType.ENTITY_PARENT,this,parent);
        }


        this.groupCache.clear(scope);
        DKPerms.getInstance().getStorage().getParentStorage().clearParentReferences(id,scope.getId());
        synchronizeGroups(scope);
    }

    @Override
    public void clearAllParents(PermissionObject executor) {
        this.groupCache.clear();

        for (ScopeBasedData<ParentEntity> allParent : getAllParents()) {
            for (ParentEntity entity : allParent.getData()) {
                DKPerms.getInstance().getAuditLog().createDeleteRecordAsync(executor, LogType.ENTITY_PARENT, this,entity);
            }
        }

        DKPerms.getInstance().getStorage().getParentStorage().clearParentReferences(id);
        synchronizeGroups(null);
    }

    @Override
    public ObjectSearchResult getAllChildren() {
        return DKPerms.getInstance().getObjectManager().search().hasParent(this).execute();
    }

    @Override
    public ObjectSearchResult getChildren(Collection<PermissionScope> range) {
        return DKPerms.getInstance().getObjectManager().search().hasParent(this).execute();
    }

    @Override
    public PermissionAction hasParent(PermissionScope scope, PermissionObject group) {
        Validate.notNull(scope,"Scope is null");
        Validate.notNull(group,"Group is null");
        return hasParent(getParents(scope),group);
    }

    @Internal
    private PermissionAction hasParent(Collection<ParentEntity> entities, PermissionObject group){
        group.getType().checkParentAble();
        for (ParentEntity entity : entities) if(entity.getParent() == group) return entity.getAction();
        return PermissionAction.NEUTRAL;
    }

    @Override
    public Pair<PermissionObject,PermissionObject> promote(PermissionObject executor, PermissionScope scope, PermissionObjectTrack track) {
        Validate.notNull(scope,track);
        if(track.isEmpty()) throw new IllegalArgumentException("Track is empty");
        List<ParentEntity> groups = new ArrayList<>(getParents(scope));
        ParentCalculator.orderEntities(groups);
        Collections.reverse(groups);
        for (ParentEntity entity : groups) {
            if(entity.getAction().has() && track.contains(entity.getParent())){
                removeParent(executor,entity);
                PermissionObject object = track.getNextGroup(entity.getParent());
                if(object == null) return new Pair<>(entity.getParent(),null);
                addParent(executor,entity.getScope(),object,entity.getAction(),entity.getTimeout());
                return new Pair<>(entity.getParent(),object);
            }
        }
        PermissionObject object = track.getFirstGroup();
        addParent(executor,scope,object,PermissionAction.ALLOW, Entity.PERMANENTLY);
        return new Pair<>(null,object);
    }

    @Override
    public Pair<PermissionObject,PermissionObject> demote(PermissionObject executor,PermissionScope scope, PermissionObjectTrack track) {
        Validate.notNull(scope,track);
        if(track.isEmpty()) throw new IllegalArgumentException("Track is empty");
        List<ParentEntity> groups = new ArrayList<>(getParents(scope));
        ParentCalculator.orderEntities(groups);
        Collections.reverse(groups);
        for (ParentEntity entity : groups) {
            if(entity.getAction().has() && track.contains(entity.getParent())){
                removeParent(executor,entity);
                PermissionObject object = track.getPreviousGroup(entity.getParent());
                if(object == null) return new Pair<>(entity.getParent(),null);
                addParent(executor,entity.getScope(),object,entity.getAction(),entity.getTimeout());
                return new Pair<>(entity.getParent(),object);
            }
        }
        return new Pair<>(null,null);
    }

    // ----- Permissions -----

    @Override
    public ScopeBasedDataList<PermissionEntity> getAllPermissions() {
        return this.permissionCache.getAll();
    }

    @Override
    public Collection<PermissionEntity> getPermissions(PermissionScope scope) {
        Validate.notNull(scope);
        return this.permissionCache.get(scope);
    }

    @Override
    public ScopeBasedDataList<PermissionEntity> getPermissions(Graph<PermissionScope> scopes) {
        return this.permissionCache.get(scopes);
    }

    @Override
    public PermissionGraph newPermissionGraph(Graph<PermissionScope> range) {
        return new DefaultPermissionGraph(this,range,null);
    }

    @Override
    public PermissionGraph newPermissionInheritanceGraph(Graph<PermissionScope> range) {
        return new DefaultPermissionGraph(this,range,newEffectedGroupInheritanceGraph(range));
    }

    @Override
    public PermissionEntity getPermission(PermissionScope scope, String permission) {
        for (PermissionEntity entity : getPermissions(scope)) if(entity.getPermission().equalsIgnoreCase(permission)) return entity;
        return null;
    }

    @Override
    public PermissionAction hasPermission(PermissionScope scope, String permission) {
        return PermissionCalculator.calculate(getPermissions(scope),permission);
    }

    @Override
    public PermissionEntity setPermission(PermissionObject executor, PermissionScope scope, String permission,PermissionAction action, long timeout) {
        Validate.notNull(scope,permission,action);
        if(getPermission(scope,permission) != null) throw new IllegalArgumentException("This permission is already set.");
        if(!scope.isSaved()) scope.insert();
        int id = DKPerms.getInstance().getStorage().getPermissionStorage().addPermission(this.id,scope.getId(),permission,action,timeout);
        PermissionEntity entity = new DefaultPermissionEntity(this,id,permission,action,scope,timeout);
        this.permissionCache.insert(scope,entity);
        synchronizePermissions(scope);
        DKPerms.getInstance().getAuditLog().createCreateRecordAsync(executor, LogType.ENTITY_PERMISSION,this,entity);
        return entity;
    }

    @Override
    public void unsetPermission(PermissionObject executor,PermissionScope scope, String permission) {
        PermissionEntity entity = getPermission(scope, permission);
        if(entity != null){
            DKPerms.getInstance().getStorage().getPermissionStorage().deletePermissions(entity.getId());
            this.permissionCache.remove(scope,entity);
            synchronizePermissions(scope);
            DKPerms.getInstance().getAuditLog().createDeleteRecordAsync(executor, LogType.ENTITY_PERMISSION,this,entity);
        }
    }

    @Override
    public void clearPermission(PermissionObject executor,PermissionScope scope) {
        for (PermissionEntity entity : getPermissions(scope)) {
            DKPerms.getInstance().getAuditLog().createDeleteRecordAsync(executor, LogType.ENTITY_PERMISSION,this,entity);
        }
        DKPerms.getInstance().getStorage().getPermissionStorage().clearPermissions(id,scope.getId());
        this.permissionCache.clear(scope);
        synchronizePermissions(scope);
    }

    @Override
    public void clearAllPermission(PermissionObject executor) {
        for (ScopeBasedData<PermissionEntity> allPermission : getAllPermissions()) {
            for (PermissionEntity entity : allPermission.getData()) {
                DKPerms.getInstance().getAuditLog().createDeleteRecordAsync(executor, LogType.ENTITY_PERMISSION,this,entity);
            }
        }
        DKPerms.getInstance().getStorage().getPermissionStorage().clearPermissions(id);
        this.permissionCache.clear();
        synchronizePermissions(null);
    }

    @Override
    public PermissionAnalyser startAnalyse() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public PermissionObject clone(PermissionObject executor, String newName, PermissionScope newScope) {
        Validate.notNull(executor,newName,newScope);
        PermissionObject newObject = DKPerms.getInstance().getObjectManager().createObject(newScope,type,newName,getAssignmentId());
        newObject.setPriority(executor,priority);
        newObject.setDisabled(executor,disabled);
        cloneAssignments(executor,newObject);
        return newObject;
    }

    @Override
    public void cloneAssignments(PermissionObject executor, PermissionObject assigner) {
        for (ScopeBasedData<ParentEntity> data : groupCache.getAll()) {
            for (ParentEntity entity : data.getData()) {
                assigner.addParent(executor,entity.getScope(),entity.getParent(),entity.getAction(),entity.getTimeout());
            }
        }

        for (ScopeBasedData<PermissionEntity> data : permissionCache.getAll()) {
            for (PermissionEntity entity : data.getData()) {
                assigner.setPermission(executor,entity.getScope(),entity.getPermission(),entity.getAction(),entity.getTimeout());
            }
        }

        for (ScopeBasedData<ObjectMetaEntry> data : meta.getEntries()) {
            for (ObjectMetaEntry entity : data.getData()) {
                assigner.getMeta().add(executor,entity.getKey(),entity.getValue(),entity.getPriority(),entity.getScope(),entity.getTimeout());
            }
        }
    }

    @Override
    public void delete(PermissionObject executor) {
        DKPerms.getInstance().getObjectManager().deleteObject(this);
    }

    @Override
    public String toStringVariable() {
        return name;
    }

    @Override
    public Document serializeRecord() {
        Document document = Document.newDocument();
        document.set("name",this.name);
        document.set("assignmentId",this.assignmentId);
        document.set("priority",this.priority);
        document.set("disabled",this.disabled);
        document.set("type",this.type.getId());
        document.set("scope",this.scope.getId());
        return document;
    }

    @Override
    public void onUpdate(Document data) {
        SyncAction action = SyncAction.of(data.getInt("action"));
        if(action != null) {
           if(action == SyncAction.OBJECT_NAME_UPDATE){
               this.name = data.getString("name");
           }else if(action == SyncAction.OBJECT_PRIORITY_UPDATE){
               this.priority = data.getInt("priority");
           }else if(action == SyncAction.OBJECT_DISABLED_UPDATE){
               this.disabled = data.getBoolean("disabled");
           }else if(action == SyncAction.OBJECT_TYPE_UPDATE){
               this.type = DKPerms.getInstance().getObjectManager().getType(data.getInt("type"));
           }else if(action == SyncAction.OBJECT_PERMISSION_UPDATE){
               int scopeId = data.getInt("scope");
               if(scopeId != 0) this.permissionCache.reset(data.getInt("scope"));
               else this.permissionCache.reset();
           }else if(action == SyncAction.OBJECT_GROUP_UPDATE){
               int scopeId = data.getInt("scope");
               if(scopeId != 0) this.groupCache.reset(data.getInt("scope"));
               else this.groupCache.reset();
           }else if(action == SyncAction.OBJECT_META_UPDATE){
               int scopeId = data.getInt("scope");
               if(scopeId != 0) this.meta.getCache().reset(data.getInt("scope"));
               else this.meta.getCache().reset();
           }
           callObservers(action);
        }
    }

    @Internal
    public void synchronizeGroups(PermissionScope scope){
        Document data = Document.newDocument();
        if(scope != null) data.set("scope",scope.getId());
        executeSynchronisationUpdate(SyncAction.OBJECT_GROUP_UPDATE,data);
    }

    @Internal
    public void synchronizePermissions(PermissionScope scope){
        Document data = Document.newDocument();
        if(scope != null) data.set("scope",scope.getId());
        executeSynchronisationUpdate(SyncAction.OBJECT_PERMISSION_UPDATE,data);
    }

    @Internal
    public void executeSynchronisationUpdate(SyncAction action,Document data){
        data.set("action", action.ordinal());
        SYNCHRONISATION_CALLER.updateAndIgnore(getId(),data);
        callObservers(action);
    }

    @Internal
    public void clearCache(){
        this.groupCache.clearCache();
        this.permissionCache.clearCache();
        this.meta.getCache().clearCache();
        callObservers(SyncAction.OBJECT_RELOAD);
    }

    @Override
    public void checkDefaultGroupAssignment(Collection<PermissionObject> groups,Graph<PermissionScope> range){
        for (PermissionObject group : groups) {
            ScopeBasedDataList<ObjectMetaEntry> entries = group.getMeta().getEntries(range);
            for (ScopeBasedData<ObjectMetaEntry> entry : entries) {
                for (ObjectMetaEntry meta : entry.getData()) {
                    if(meta.getKey().equalsIgnoreCase("defaultAssign")){
                        if(getParent(entry.getScope(),group) == null){
                            addParent(DKPerms.getInstance().getObjectManager().getSuperAdministrator()
                            ,entry.getScope(),group,PermissionAction.ALLOW_ALWAYS,Entity.PERMANENTLY);
                        }
                    }
                }
            }
        }
    }

    public void checkTimedOutObjects(){
        boolean toRemove = false;
        for (ScopeBasedData<ParentEntity> cachedEntry : groupCache.getCachedEntries()) {
            List<ParentEntity> result = Iterators.remove(cachedEntry.getData(), TimeoutAble::hasTimeout);
            if(!result.isEmpty()){
                for (ParentEntity entity : result) {
                    DKPerms.getInstance().getStorage().getParentStorage().removeParentReference(entity.getId());
                }
                toRemove = true;
            }
        }
        System.out.println("TIMEOUT CHECK "+toRemove);
        if(toRemove) callObservers(SyncAction.OBJECT_GROUP_UPDATE);
    }
}
