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
import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.entity.PermissionGroupEntity;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.graph.GroupGraph;
import net.pretronic.dkperms.api.graph.ObjectGraph;
import net.pretronic.dkperms.api.graph.PermissionGraph;
import net.pretronic.dkperms.api.object.meta.ObjectMeta;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.permission.analyse.PermissionAnalyse;
import net.pretronic.dkperms.api.permission.analyse.track.PermissionTrackResult;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;
import net.pretronic.libraries.synchronisation.observer.Observable;
import net.pretronic.libraries.utility.annonations.Nullable;

import java.time.Duration;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public interface PermissionObject extends Observable<PermissionObject,SyncAction> {

    int getId();

    UUID getAssignmentId();


    String getName();

    void setName(PermissionObject executor,String name);


    String getPath();


    PermissionObjectType getType();

    void setType(PermissionObject executor,PermissionObjectType type);


    int getPriority();

    void setPriority(PermissionObject executor,int priority);


    boolean isDisabled();

    void setDisabled(PermissionObject executor,boolean disabled);


    PermissionScope getScope();

    void setScope(PermissionObject executor,PermissionScope scope);


    // ----- Current location data -----

    Object getHolder();

    void setHolder(Object holder);


    @Nullable
    PermissionObjectSnapshot getCurrentSnapshot();

    void setCurrentSnapshot(PermissionObjectSnapshot snapshot);

    PermissionObjectSnapshot newSnapshot(PermissionScope scope);

    default void setCurrentScope(PermissionScope scope){
        PermissionObjectSnapshot snapshot = getCurrentSnapshot();
        if(snapshot == null){
            snapshot = newSnapshot(scope);
            setCurrentSnapshot(snapshot);
        }else{
            snapshot.setScope(scope);
        }
    }



    ObjectMeta getMeta();


    // ----- Groups -----


    ScopeBasedDataList<PermissionGroupEntity> getAllGroups();//Returns all groups

    default Collection<PermissionGroupEntity> getGroups(){//Returns all root groups
        return getGroups(getScope());
    }

    Collection<PermissionGroupEntity> getGroups(PermissionScope scope);//Returns all groups from scope

    ScopeBasedDataList<PermissionGroupEntity> getGroups(Graph<PermissionScope> range);


    GroupGraph newGroupGraph(Graph<PermissionScope> range);

    GroupGraph newGroupInheritanceGraph(Graph<PermissionScope> range);


    ObjectGraph newEffectedGroupGraph(Graph<PermissionScope> range);

    ObjectGraph newEffectedGroupInheritanceGraph(Graph<PermissionScope> range);


    default PermissionGroupEntity getGroup(PermissionObject object){
        return getGroup(getScope(),object);
    }

    PermissionGroupEntity getGroup(PermissionScope scope, PermissionObject object);


    PermissionGroupEntity setGroup(PermissionObject executor, PermissionScope scope, PermissionObject group, PermissionAction action, long timeout);

    default PermissionGroupEntity setGroup(PermissionObject executor, PermissionScope scope, PermissionObject group, PermissionAction action, long duration, TimeUnit unit){
        return setGroup(executor,scope, group,action,duration>0?unit.toMillis(duration)+System.currentTimeMillis():duration);
    }

    default PermissionGroupEntity setGroup(PermissionObject executor, PermissionScope scope, PermissionObject group, PermissionAction action, Duration duration){
        return setGroup(executor,scope, group,action,duration.getSeconds(),TimeUnit.SECONDS);
    }


    PermissionGroupEntity addGroup(PermissionObject executor, PermissionScope scope, PermissionObject group, PermissionAction action, long timeout);

    default PermissionGroupEntity addGroup(PermissionObject executor, PermissionScope scope, PermissionObject group, PermissionAction action, Duration duration){
        return addGroup(executor,scope, group,action,duration.getSeconds(),TimeUnit.SECONDS);
    }

    default PermissionGroupEntity addGroup(PermissionObject executor, PermissionScope scope, PermissionObject group, PermissionAction action, long duration, TimeUnit unit){
        return addGroup(executor,scope, group,action,duration>0?unit.toMillis(duration)+System.currentTimeMillis():duration);
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


    PermissionObject promote(PermissionObject executor, PermissionScope scope);

    PermissionObject promote(PermissionObject executor,PermissionScope scope, PermissionGroupTrack track);

    PermissionObject demote(PermissionObject executor,PermissionScope scope);

    PermissionObject demote(PermissionObject executor,PermissionScope scope, PermissionGroupTrack track);


    // ----- Permissions -----


    ScopeBasedDataList<PermissionEntity> getAllPermissions();

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


    PermissionAction hasPermission(PermissionScope scope, String permission);


    PermissionEntity setPermission(PermissionObject executor,PermissionScope scope, String permission,PermissionAction action, long timeout);

    default PermissionEntity setPermission(PermissionObject executor, PermissionScope scope, String permission, PermissionAction action, Duration duration){
        return setPermission(executor,scope, permission,action, duration.getSeconds(), TimeUnit.SECONDS);
    }

    default PermissionEntity setPermission(PermissionObject executor,PermissionScope scope, String permission,PermissionAction action, long duration, TimeUnit unit){
        return setPermission(executor,scope, permission,action, duration>0?unit.toMillis(duration)+System.currentTimeMillis():duration);
    }


    default void unsetPermission(PermissionObject executor,String permission){
        unsetPermission(executor,getScope(),permission);
    }

    void unsetPermission(PermissionObject executor,PermissionScope scope, String permission);

    default void clearPermission(PermissionObject executor){
        clearPermission(executor,getScope());
    }

    void clearPermission(PermissionObject executor,PermissionScope scope);

    void clearAllPermission(PermissionObject executor);



    PermissionTrackResult trackPermission(String permission);

    PermissionTrackResult trackGroup(PermissionObject group);

    PermissionAnalyse startAnalyse();


    void delete(PermissionObject executor);


    default CompletableFuture<Void> deleteAsync(PermissionObject executor){
        return DKPerms.getInstance().getExecutor().executeVoid(() -> delete(executor));
    }

    default PermissionObject copy(PermissionObject executor,String newName){
        return clone(executor,newName);
    }

    PermissionObject clone(PermissionObject executor,String newName);


    <T> T getHolder(Class<?> holderClass);


}
