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

import net.pretronic.dkperms.common.scope.data.ArrayScopeBasedDataList;
import net.pretronic.libraries.message.bml.variable.describer.VariableObjectToString;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.annonations.Internal;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.entity.PermissionGroupEntity;
import net.pretronic.dkperms.api.graph.PermissionGraph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.object.PermissionGroupOrder;
import net.pretronic.dkperms.api.object.holder.PermissionObjectHolder;
import net.pretronic.dkperms.api.object.meta.ObjectMeta;
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
import net.pretronic.dkperms.common.graph.group.GroupEntityGraph;
import net.pretronic.dkperms.common.graph.group.GroupGraph;
import net.pretronic.dkperms.common.graph.permission.PermissionEntityGraph;
import net.pretronic.dkperms.common.object.meta.DefaultObjectMeta;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class DefaultPermissionObject implements PermissionObject, VariableObjectToString {

    private final int id;
    private final UUID assignmentId;
    private String name;
    private boolean disabled;
    private boolean deleted;

    private PermissionObjectType type;
    private PermissionScope scope;
    private PermissionObjectHolder holder;

    private final ObjectMeta meta;
    private final GroupCache groupCache;
    private final PermissionCache permissionCache;

    public DefaultPermissionObject(int id,UUID assignmentId, String name,boolean disabled,boolean deleted,PermissionObjectType type, PermissionScope scope) {
        this.id = id;
        this.assignmentId = assignmentId;
        this.name = name;
        this.disabled = disabled;
        this.deleted = deleted;

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
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void updateName(PermissionObject executor,String name) {
        Validate.notNull(name);
        DKPerms.getInstance().getStorage().getObjectStorage().updateObjectName(this.id,name);
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
    public void updateType(PermissionObject executor,PermissionObjectType type) {
        Validate.notNull(type);
        DKPerms.getInstance().getStorage().getObjectStorage().updateObjectType(this.id,type);
        this.type = type;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void updateDisabled(PermissionObject executor,boolean disabled) {
        DKPerms.getInstance().getStorage().getObjectStorage().updateObjectDisabled(this.id,disabled);
        this.disabled = disabled;
    }

    @Override
    public PermissionScope getScope() {
        return scope;
    }

    @Override
    public void updateScope(PermissionObject executor,PermissionScope scope) {
        Validate.notNull(scope);
        DKPerms.getInstance().getStorage().getObjectStorage().updateObjectScope(this.id,scope);
        this.scope = scope;
    }

    @Override
    public PermissionObjectHolder getHolder() {
        if(holder == null) holder = type.getLocalHolderFactory().create(this);
        return holder;
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
    public Collection<PermissionGroupEntity> getAllGroups() {
        return null;//groupCache.getAll();
    }

    @Override
    public Collection<PermissionGroupEntity> getGroups(PermissionScope scope) {
        Validate.notNull(scope);
        return groupCache.get(scope);
    }

    @Override
    public ScopeBasedDataList<PermissionGroupEntity> getGroups(Graph<PermissionScope> range) {
        return new ArrayScopeBasedDataList<>();
    }

    @Override
    public Graph<PermissionGroupEntity> newGroupEntityGraph(Graph<PermissionScope> range) {
        return new GroupEntityGraph(this,range,false);
    }

    @Override
    public Graph<PermissionGroupEntity> newGroupEntityInheritanceGraph(Graph<PermissionScope> range) {
        return new GroupEntityGraph(this,range,true);
    }

    @Override
    public Graph<PermissionObject> newGroupGraph(Graph<PermissionScope> range) {
        return new GroupGraph(newGroupEntityGraph(range));
    }

    @Override
    public Graph<PermissionObject> newGroupInheritanceGraph(Graph<PermissionScope> range) {
        return new GroupGraph(newGroupEntityInheritanceGraph(range));
    }

    @Override
    public PermissionGroupEntity getGroup(PermissionScope scope, PermissionObject object) {
        Validate.notNull(scope,object);
        for (PermissionGroupEntity entity : getGroups(scope)) if(entity.getGroup().equals(object)) return entity;
        return null;
    }

    @Override
    public PermissionGroupEntity addGroup(PermissionObject executor,PermissionScope scope, PermissionObject group, PermissionAction action, long timeout) {
        Validate.notNull(scope,group,action);
        if(isInGroup(scope, group) != PermissionAction.NEUTRAL) throw new IllegalArgumentException("This group is already added");
        if(group == this) throw new IllegalArgumentException("You can not add a group to it self");
        if(!this.scope.contains(scope)) throw new IllegalArgumentException("Scope must be an inheritance scope of main object scope");
        if(!scope.isSaved()) scope.insert();
        int entityId = DKPerms.getInstance().getStorage().getGroupStorage()
                .createGroupReference(id,scope.getId(),group.getId(),action,timeout);
        DefaultPermissionGroupEntity entity =  new DefaultPermissionGroupEntity(entityId,group,action,scope,timeout);
        groupCache.insert(scope,entity);
        return entity;
    }

    @Override
    public void removeGroup(PermissionObject executor,PermissionScope scope, PermissionObject group) {
        Validate.notNull(scope,group);
        PermissionGroupEntity entity = getGroup(scope,group);
        if(entity != null) removeGroup(executor,entity);
    }

    @Override
    public void removeGroup(PermissionObject executor,PermissionGroupEntity entity) {
        Validate.notNull(entity);
        this.groupCache.remove(entity.getScope(),entity);
        DKPerms.getInstance().getStorage().getGroupStorage().removeGroupReference(entity.getId());
    }

    @Override
    public void clearGroups(PermissionObject executor,PermissionScope scope) {
        Validate.notNull(scope);
        this.groupCache.clear(scope);
        DKPerms.getInstance().getStorage().getGroupStorage().clearGroupReferences(id,scope.getId());
    }

    @Override
    public void clearAllGroups(PermissionObject executor) {
        this.groupCache.clear();
        DKPerms.getInstance().getStorage().getGroupStorage().clearGroupReferences(id);
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
    public Collection<PermissionEntity> getAllPermissions() {
        return null;//return this.permissionCache.getAll();
    }

    @Override
    public Collection<PermissionEntity> getPermissions(PermissionScope scope) {
        Validate.notNull(scope);
        return this.permissionCache.get(scope);
    }

    @Override
    public ScopeBasedDataList<PermissionEntity> getPermissions(Graph<PermissionScope> scopes) {
        return new ArrayScopeBasedDataList<>();
    }

    @Override
    public PermissionGraph newPermissionGraph(Graph<PermissionScope> range) {
        return new PermissionEntityGraph(this,range,null);//@Todo change to empty
    }

    @Override
    public PermissionGraph newPermissionInheritanceGraph(Graph<PermissionScope> range) {
        return new PermissionEntityGraph(this,range,newGroupGraph(range));
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
    public PermissionEntity addPermission(PermissionScope scope, String permission,PermissionAction action, long timeout) {
        Validate.notNull(scope,permission,action);
        if(getPermission(scope,permission) != null) throw new IllegalArgumentException("This permission is already set.");
        if(!scope.isSaved()) scope.insert();
        int id = DKPerms.getInstance().getStorage().getPermissionStorage().addPermission(this.id,scope.getId(),permission,action,timeout);
        PermissionEntity entity = new DefaultPermissionEntity(id,permission,action,scope,timeout);
        this.permissionCache.insert(scope,entity);
        return entity;
    }

    @Override
    public void removePermission(PermissionObject executor,PermissionScope scope, String permission) {
        PermissionEntity entity = getPermission(scope, permission);
        if(entity != null){
            DKPerms.getInstance().getStorage().getPermissionStorage().deletePermissions(entity.getId());
            this.permissionCache.remove(scope,entity);
        }
    }

    @Override
    public void clearPermission(PermissionObject executor,PermissionScope scope) {
        DKPerms.getInstance().getStorage().getPermissionStorage().clearPermissions(id,scope.getId());
        this.permissionCache.clear(scope);
    }

    @Override
    public void clearAllPermission(PermissionObject executor) {
        DKPerms.getInstance().getStorage().getPermissionStorage().clearPermissions(id);
        this.permissionCache.clear();
    }


    //----- Track -----

    @Override
    public PermissionTrackResult trackPermission(String permission) {
        return null;
    }

    @Override
    public PermissionTrackResult trackGroup(PermissionObject group) {
        return null;
    }

    @Override
    public PermissionAnalyse startAnalyse() {
        return null;
    }

    @Override
    public PermissionObject clone(PermissionObject executor,String newName) {
        return null;
    }

    @Override
    public void delete(PermissionObject executor) {//@Todo uncache
        DKPerms.getInstance().getStorage().getObjectStorage().deleteObject(id);
    }

    @Override
    public void restore(PermissionObject executor) {

    }


    @Override
    public String toStringVariable() {
        return name;
    }
}
