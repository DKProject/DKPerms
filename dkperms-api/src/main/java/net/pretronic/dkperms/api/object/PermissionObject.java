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

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.entity.PermissionGroupEntity;
import net.pretronic.dkperms.api.graph.PermissionGraph;
import net.pretronic.dkperms.api.object.holder.PermissionObjectHolder;
import net.pretronic.dkperms.api.object.meta.ObjectMeta;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.permission.analyse.PermissionAnalyse;
import net.pretronic.dkperms.api.permission.analyse.track.PermissionTrackResult;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public interface PermissionObject{

    int getId();

    UUID getAssignmentId();

    boolean isDeleted();


    String getName();

    void updateName(PermissionObject executor,String name);


    String getPath();


    PermissionObjectType getType();

    void updateType(PermissionObject executor,PermissionObjectType type);


    boolean isDisabled();

    void updateDisabled(PermissionObject executor,boolean disabled);


    PermissionScope getScope();

    void updateScope(PermissionObject executor,PermissionScope scope);


    PermissionObjectHolder getHolder();

    ObjectMeta getMeta();


    // ----- Groups -----


    Collection<PermissionGroupEntity> getAllGroups();//Returns all groups

    default Collection<PermissionGroupEntity> getGroups(){//Returns all root groups
        return getGroups(getScope());
    }

    Collection<PermissionGroupEntity> getGroups(PermissionScope scope);//Returns all groups from scope

    ScopeBasedDataList<PermissionGroupEntity> getGroups(Graph<PermissionScope> range);


    Graph<PermissionGroupEntity> newGroupEntityGraph(Graph<PermissionScope> range);

    Graph<PermissionGroupEntity> newGroupEntityInheritanceGraph(Graph<PermissionScope> range);


    Graph<PermissionObject> newGroupGraph(Graph<PermissionScope> range);

    Graph<PermissionObject> newGroupInheritanceGraph(Graph<PermissionScope> range);


    default PermissionGroupEntity getGroup(PermissionObject object){
        return getGroup(getScope(),object);
    }

    PermissionGroupEntity getGroup(PermissionScope scope, PermissionObject object);


    PermissionGroupEntity addGroup(PermissionObject executor, PermissionScope scope, PermissionObject group, PermissionAction action, long timeout);

    default PermissionGroupEntity addGroup(PermissionObject executor, PermissionScope scope, PermissionObject group, PermissionAction action, long duration, TimeUnit unit){
        return addGroup(executor,scope, group,action,duration>0?unit.toMillis(duration):duration);
    }


    default void removeGroup(PermissionObject executor,PermissionObject group){
        removeGroup(executor,getScope(),group);
    }

    void removeGroup(PermissionObject executor,PermissionScope scope, PermissionObject group);

    void removeGroup(PermissionObject executor,PermissionGroupEntity entity);


    default void clearGroups(PermissionObject executor){
        clearGroups(executor,getScope());
    }

    void clearGroups(PermissionObject executor,PermissionScope scope);

    void clearAllGroups(PermissionObject executor);


    default PermissionAction isInGroup(PermissionObject group){
        return isInGroup(getScope(),group);
    }

    PermissionAction isInGroup(PermissionScope scope, PermissionObject group);


    void promote(PermissionScope scope);

    void promote(PermissionScope scope, PermissionGroupOrder order);

    void demote(PermissionScope scope);

    void demote(PermissionScope scope, PermissionGroupOrder order);


    // ----- Permissions -----


    Collection<PermissionEntity> getAllPermissions();

    default Collection<PermissionEntity> getPermissions(){
        return getPermissions(getScope());
    }

    Collection<PermissionEntity> getPermissions(PermissionScope scope);

    ScopeBasedDataList<PermissionEntity> getPermissions(Graph<PermissionScope> scopes);


    PermissionGraph newPermissionGraph(Graph<PermissionScope> range);

    PermissionGraph newPermissionInheritanceGraph(Graph<PermissionScope> range);


    default PermissionEntity getPermission(String permission){
        return getPermission(getScope(),permission);
    }

    PermissionEntity getPermission(PermissionScope scope, String permission);


    default PermissionAction hasPermission(String permission){
        return hasPermission(getScope(),permission);
    }

    PermissionAction hasPermission(PermissionScope scope, String permission);


    PermissionEntity addPermission(PermissionScope scope, String permission,PermissionAction action, long timeout);


    default PermissionEntity addPermission(PermissionScope scope, String permission,PermissionAction action, long duration, TimeUnit unit){
        return addPermission(scope, permission,action, duration>0?unit.toMillis(duration):duration);
    }


    default void removePermission(PermissionObject executor,String permission){
        removePermission(executor,getScope(),permission);
    }

    void removePermission(PermissionObject executor,PermissionScope scope, String permission);

    default void clearPermission(PermissionObject executor){
        clearPermission(executor,getScope());
    }

    void clearPermission(PermissionObject executor,PermissionScope scope);

    void clearAllPermission(PermissionObject executor);



    PermissionTrackResult trackPermission(String permission);

    PermissionTrackResult trackGroup(PermissionObject group);

    PermissionAnalyse startAnalyse();


    void delete(PermissionObject executor);

    void restore(PermissionObject executor);


    default CompletableFuture<Void> deleteAsync(PermissionObject executor){
        return DKPerms.getInstance().getExecutor().executeVoid(() -> delete(executor));
    }

    default PermissionObject copy(PermissionObject executor,String newName){
        return clone(executor,newName);
    }

    PermissionObject clone(PermissionObject executor,String newName);


    <T> T getHolder(Class<?> holderClass);


}
