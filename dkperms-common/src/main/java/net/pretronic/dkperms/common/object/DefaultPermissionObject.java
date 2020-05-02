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
import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.entity.PermissionGroupEntity;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.graph.GroupGraph;
import net.pretronic.dkperms.api.graph.ObjectGraph;
import net.pretronic.dkperms.api.graph.PermissionGraph;
import net.pretronic.dkperms.api.object.PermissionGroupOrder;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.object.SyncAction;
import net.pretronic.dkperms.api.object.holder.PermissionObjectHolder;
import net.pretronic.dkperms.api.object.meta.ObjectMeta;
import net.pretronic.dkperms.api.object.snapshot.PermissionObjectSnapshot;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.permission.analyse.PermissionAnalyse;
import net.pretronic.dkperms.api.permission.analyse.track.PermissionTrackResult;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;
import net.pretronic.dkperms.common.cache.GroupCache;
import net.pretronic.dkperms.common.cache.PermissionCache;
import net.pretronic.dkperms.common.calculator.PermissionCalculator;
import net.pretronic.dkperms.common.entity.DefaultPermissionEntity;
import net.pretronic.dkperms.common.entity.DefaultPermissionGroupEntity;
import net.pretronic.dkperms.common.graph.DefaultGroupGraph;
import net.pretronic.dkperms.common.graph.DefaultObjectGraph;
import net.pretronic.dkperms.common.graph.DefaultPermissionGraph;
import net.pretronic.dkperms.common.object.meta.DefaultObjectMeta;
import net.pretronic.dkperms.common.scope.data.ArrayScopeBasedDataList;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.message.bml.variable.describer.VariableObjectToString;
import net.pretronic.libraries.synchronisation.SynchronisationCaller;
import net.pretronic.libraries.synchronisation.Synchronizable;
import net.pretronic.libraries.synchronisation.UnconnectedSynchronisationCaller;
import net.pretronic.libraries.synchronisation.observer.AbstractObservable;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.annonations.Internal;

import java.util.Collection;
import java.util.UUID;

/*
@Todo Cache reset objects
 */
public class DefaultPermissionObject extends AbstractObservable<PermissionObject,SyncAction> implements PermissionObject, VariableObjectToString, Synchronizable {

    public static SynchronisationCaller<Integer> SYNCHRONISATION_CALLER = new UnconnectedSynchronisationCaller<>(true);

    private final int id;
    private final UUID assignmentId;
    private String name;
    private int priority;
    private boolean disabled;

    private PermissionObjectType type;
    private PermissionScope scope;

    private PermissionObjectHolder holder;
    private PermissionObjectSnapshot currentSnapshot;

    private final ObjectMeta meta;
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
        Validate.notNull(name);
        DKPerms.getInstance().getStorage().getObjectStorage().updateObjectName(this.id,name);
        executeSynchronisationUpdate(SyncAction.OBJECT_NAME_UPDATE,Document.newDocument().set("name",name));
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
        Validate.notNull(type);
        DKPerms.getInstance().getStorage().getObjectStorage().updateObjectType(this.id,type);
        executeSynchronisationUpdate(SyncAction.OBJECT_TYPE_UPDATE,Document.newDocument().set("type",type.getId()));
        this.type = type;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(PermissionObject executor, int priority) {
        DKPerms.getInstance().getStorage().getObjectStorage().updateObjectPriority(this.id,priority);
        executeSynchronisationUpdate(SyncAction.OBJECT_PRIORITY_UPDATE,Document.newDocument().set("priority",priority));
        this.priority = priority;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(PermissionObject executor,boolean disabled) {
        DKPerms.getInstance().getStorage().getObjectStorage().updateObjectDisabled(this.id,disabled);
        executeSynchronisationUpdate(SyncAction.OBJECT_DISABLED_UPDATE,Document.newDocument().set("disabled",disabled));
        this.disabled = disabled;
    }

    @Override
    public PermissionScope getScope() {
        return scope;
    }

    @Override
    public void setScope(PermissionObject executor,PermissionScope scope) {
        Validate.notNull(scope);
        DKPerms.getInstance().getStorage().getObjectStorage().updateObjectScope(this.id,scope);
        executeSynchronisationUpdate(SyncAction.OBJECT_SCOPE_UPDATE,Document.newDocument().set("scope",scope.getId()));
        this.scope = scope;
    }

    @Override
    public PermissionObjectHolder getHolder() {
        if(holder == null) holder = type.getLocalHolderFactory().create(this);
        return holder;
    }

    @Override
    public PermissionObjectSnapshot getCurrentSnapshot() {
        return currentSnapshot;
    }

    @Override
    public void setCurrentSnapshot(PermissionObjectSnapshot snapshot) {
        Validate.notNull(snapshot);
        if(this.type.isGroup()) throw new IllegalArgumentException("It is not possible to define a current scope for a group object");
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
    public ScopeBasedDataList<PermissionGroupEntity> getAllGroups() {
        return groupCache.getAll();
    }

    @Override
    public Collection<PermissionGroupEntity> getGroups(PermissionScope scope) {
        Validate.notNull(scope);
        return groupCache.get(scope);
    }

    @Override
    public ScopeBasedDataList<PermissionGroupEntity> getGroups(Graph<PermissionScope> range) {
        return groupCache.get(range);
    }

    @Override
    public GroupGraph newGroupGraph(Graph<PermissionScope> range) {
        return new DefaultGroupGraph(this,range,false);
    }

    @Override
    public GroupGraph newGroupInheritanceGraph(Graph<PermissionScope> range) {
        return new DefaultGroupGraph(this,range,true);
    }

    @Override
    public ObjectGraph newEffectedGroupGraph(Graph<PermissionScope> range) {
        return new DefaultObjectGraph(newGroupGraph(range));
    }

    @Override
    public ObjectGraph newEffectedGroupInheritanceGraph(Graph<PermissionScope> range) {
        return new DefaultObjectGraph(newGroupInheritanceGraph(range));
    }

    @Override
    public PermissionGroupEntity getGroup(PermissionScope scope, PermissionObject object) {
        Validate.notNull(scope,object);
        for (PermissionGroupEntity entity : getGroups(scope)){
            if(entity.getGroup().equals(object)) return entity;
        }
        return null;
    }

    @Override
    public PermissionGroupEntity setGroup(PermissionObject executor, PermissionScope scope, PermissionObject group, PermissionAction action, long timeout) {
        Validate.notNull(scope,group,action);
        if(group == this) throw new IllegalArgumentException("You can not add a group to it self");
        if(!this.scope.contains(scope)) throw new IllegalArgumentException("Scope must be an inheritance scope of main object scope");

        if(!scope.isSaved()) scope.insert();

        DKPerms.getInstance().getStorage().getGroupStorage().clearGroupReferences(id,scope.getId());
        this.groupCache.clear(scope);
        return insertPermissionGroupEntity(scope, group, action, timeout);
    }

    @Override
    public PermissionGroupEntity addGroup(PermissionObject executor,PermissionScope scope, PermissionObject group, PermissionAction action, long timeout) {
        Validate.notNull(scope,group,action);
        if(isInGroup(scope, group) != PermissionAction.NEUTRAL) throw new IllegalArgumentException("This group is already added");
        if(group == this) throw new IllegalArgumentException("You can not add a group to it self");
        if(!this.scope.contains(scope)) throw new IllegalArgumentException("Scope must be an inheritance scope of main object scope");
        if(!scope.isSaved()) scope.insert();
        return insertPermissionGroupEntity(scope, group, action, timeout);
    }

    private PermissionGroupEntity insertPermissionGroupEntity(PermissionScope scope, PermissionObject group, PermissionAction action, long timeout) {
        int entityId = DKPerms.getInstance().getStorage().getGroupStorage()
                .createGroupReference(id,scope.getId(),group.getId(),action,timeout);
        DefaultPermissionGroupEntity entity =  new DefaultPermissionGroupEntity(this,entityId,group,action,scope,timeout);
        groupCache.insert(scope,entity);
        synchronizeGroups(scope);
        return entity;
    }

    @Override
    public void removeGroup(PermissionObject executor,PermissionScope scope, PermissionObject group) {
        Validate.notNull(scope,group);
        PermissionGroupEntity entity = getGroup(scope,group);
        if(entity != null) removeGroup(executor,entity);
        synchronizeGroups(scope);
    }

    @Override
    public void removeGroup(PermissionObject executor,PermissionGroupEntity entity) {
        Validate.notNull(entity);
        this.groupCache.remove(entity.getScope(),entity);
        DKPerms.getInstance().getStorage().getGroupStorage().removeGroupReference(entity.getId());
        synchronizeGroups(entity.getScope());
    }

    @Override
    public void clearGroups(PermissionObject executor,PermissionScope scope) {
        Validate.notNull(scope);
        this.groupCache.clear(scope);
        DKPerms.getInstance().getStorage().getGroupStorage().clearGroupReferences(id,scope.getId());
        synchronizeGroups(scope);
    }

    @Override
    public void clearAllGroups(PermissionObject executor) {
        this.groupCache.clear();
        DKPerms.getInstance().getStorage().getGroupStorage().clearGroupReferences(id);
        synchronizeGroups(null);
    }

    @Override
    public PermissionAction isInGroup(PermissionScope scope, PermissionObject group) {
        Validate.notNull(scope,"Scope is null");
        Validate.notNull(group,"Group is null");
        return isInGroup(getGroups(scope),group);
    }

    @Internal
    private PermissionAction isInGroup(Collection<PermissionGroupEntity> entities, PermissionObject group){
        group.getType().checkIsGroup();
        for (PermissionGroupEntity entity : entities) if(entity.getGroup() == group) return entity.getAction();
        return PermissionAction.NEUTRAL;
    }

    @Override
    public void promote(PermissionScope scope) {
        throw new UnsupportedOperationException("Currently not implemented");
    }

    @Override
    public void promote(PermissionScope scope, PermissionGroupOrder order) {
        throw new UnsupportedOperationException("Currently not implemented");
    }

    @Override
    public void demote(PermissionScope scope) {
        throw new UnsupportedOperationException("Currently not implemented");
    }

    @Override
    public void demote(PermissionScope scope, PermissionGroupOrder order) {
        throw new UnsupportedOperationException("Currently not implemented");
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
    public PermissionEntity addPermission(PermissionObject permissionObject, PermissionScope scope, String permission,PermissionAction action, long timeout) {
        Validate.notNull(scope,permission,action);
        if(getPermission(scope,permission) != null) throw new IllegalArgumentException("This permission is already set.");
        if(!scope.isSaved()) scope.insert();
        int id = DKPerms.getInstance().getStorage().getPermissionStorage().addPermission(this.id,scope.getId(),permission,action,timeout);
        PermissionEntity entity = new DefaultPermissionEntity(this,id,permission,action,scope,timeout);
        this.permissionCache.insert(scope,entity);
        synchronizePermissions(scope);
        return entity;
    }

    @Override
    public void removePermission(PermissionObject executor,PermissionScope scope, String permission) {
        PermissionEntity entity = getPermission(scope, permission);
        if(entity != null){
            DKPerms.getInstance().getStorage().getPermissionStorage().deletePermissions(entity.getId());
            this.permissionCache.remove(scope,entity);
            synchronizePermissions(scope);
        }
    }

    @Override
    public void clearPermission(PermissionObject executor,PermissionScope scope) {
        DKPerms.getInstance().getStorage().getPermissionStorage().clearPermissions(id,scope.getId());
        this.permissionCache.clear(scope);
        synchronizePermissions(scope);
    }

    @Override
    public void clearAllPermission(PermissionObject executor) {
        DKPerms.getInstance().getStorage().getPermissionStorage().clearPermissions(id);
        this.permissionCache.clear();
        synchronizePermissions(null);
    }


    //----- Track -----

    @Override
    public PermissionTrackResult trackPermission(String permission) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public PermissionTrackResult trackGroup(PermissionObject group) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public PermissionAnalyse startAnalyse() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public PermissionObject clone(PermissionObject executor,String newName) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void delete(PermissionObject executor) {
        DKPerms.getInstance().getStorage().getObjectStorage().deleteObject(id);
    }

    @Override
    public String toStringVariable() {
        return name;
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

    //@Todo maybe optimize
    @Internal
    public void executeSynchronisationUpdate(SyncAction action,Document data){
        data.set("action", SyncAction.OBJECT_GROUP_UPDATE);
        SYNCHRONISATION_CALLER.update(getId(),data);

        callObservers(action);
    }
}
