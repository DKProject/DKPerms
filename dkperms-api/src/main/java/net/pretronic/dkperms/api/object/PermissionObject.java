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
import net.pretronic.dkperms.api.entity.ParentEntity;
import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.graph.ObjectGraph;
import net.pretronic.dkperms.api.graph.ParentGraph;
import net.pretronic.dkperms.api.graph.PermissionGraph;
import net.pretronic.dkperms.api.object.meta.ObjectMeta;
import net.pretronic.dkperms.api.object.search.ObjectSearchResult;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.permission.analyse.PermissionAnalyser;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;
import net.pretronic.libraries.synchronisation.observer.Observable;
import net.pretronic.libraries.utility.annonations.Nullable;
import net.pretronic.libraries.utility.map.Pair;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
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


    ScopeBasedDataList<ParentEntity> getAllParents();//Returns all groups

    default Collection<ParentEntity> getParents(){//Returns all root groups
        return getParents(getScope());
    }

    Collection<ParentEntity> getParents(PermissionScope scope);//Returns all groups from scope

    ScopeBasedDataList<ParentEntity> getParents(Graph<PermissionScope> range);


    default PermissionObject getHighestParent(){
        return getHighestParent(getScope());
    }

    PermissionObject getHighestParent(PermissionScope scope);


    ParentGraph newParentGraph(Graph<PermissionScope> range);

    ParentGraph newParentInheritanceGraph(Graph<PermissionScope> range);


    ObjectGraph newEffectedParentGraph(Graph<PermissionScope> range);

    ObjectGraph newEffectedParentInheritanceGraph(Graph<PermissionScope> range);


    ObjectGraph newEffectedGroupGraph(Graph<PermissionScope> range);

    ObjectGraph newEffectedGroupInheritanceGraph(Graph<PermissionScope> range);


    default ParentEntity getParent(PermissionObject object){
        return getParent(getScope(),object);
    }

    ParentEntity getParent(PermissionScope scope, PermissionObject object);


    ParentEntity setParent(PermissionObject executor, PermissionScope scope, PermissionObject group, PermissionAction action, long timeout);

    default ParentEntity setParent(PermissionObject executor, PermissionScope scope, PermissionObject group, PermissionAction action, long duration, TimeUnit unit){
        return setParent(executor,scope, group,action,duration>0?unit.toMillis(duration)+System.currentTimeMillis():duration);
    }

    default ParentEntity setParent(PermissionObject executor, PermissionScope scope, PermissionObject group, PermissionAction action, Duration duration){
        return setParent(executor,scope, group,action,duration.getSeconds(),TimeUnit.SECONDS);
    }


    ParentEntity addParent(PermissionObject executor, PermissionScope scope, PermissionObject group, PermissionAction action, long timeout);

    default ParentEntity addParent(PermissionObject executor, PermissionScope scope, PermissionObject group, PermissionAction action, Duration duration){
        return addParent(executor,scope, group,action,duration.getSeconds(),TimeUnit.SECONDS);
    }

    default ParentEntity addParent(PermissionObject executor, PermissionScope scope, PermissionObject group, PermissionAction action, long duration, TimeUnit unit){
        return addParent(executor,scope, group,action,duration>0?unit.toMillis(duration)+System.currentTimeMillis():duration);
    }


    default void removeParent(PermissionObject executor,PermissionObject group){
        removeParent(executor,getScope(),group);
    }

    void removeParent(PermissionObject executor,PermissionScope scope, PermissionObject group);

    void removeParent(PermissionObject executor, ParentEntity entity);


    default void clearParents(PermissionObject executor){
        clearParents(executor,getScope());
    }

    void clearParents(PermissionObject executor,PermissionScope scope);

    void clearAllParents(PermissionObject executor);


    ObjectSearchResult getAllChildren();

    default ObjectSearchResult getChildren(PermissionScope scope){
        return getChildren(Collections.singleton(scope));
    }

    default ObjectSearchResult getChildren(Graph<PermissionScope> range){
        return getChildren(range.traverse());
    }

    ObjectSearchResult getChildren(Collection<PermissionScope> range);


    default PermissionAction hasParent(PermissionObject group){
        return hasParent(getScope(),group);
    }

    PermissionAction hasParent(PermissionScope scope, PermissionObject group);


    Pair<PermissionObject,PermissionObject> promote(PermissionObject executor, PermissionScope scope, PermissionObjectTrack track);

    Pair<PermissionObject,PermissionObject> demote(PermissionObject executor,PermissionScope scope, PermissionObjectTrack track);


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



    PermissionAnalyser startAnalyse();


    void delete(PermissionObject executor);


    default CompletableFuture<Void> deleteAsync(PermissionObject executor){
        return DKPerms.getInstance().getExecutor().executeVoid(() -> delete(executor));
    }


    default PermissionObject clone(PermissionObject executor,String newName){
        return clone(executor, newName,getScope());
    }

    PermissionObject clone(PermissionObject executor,String newName,PermissionScope newScope);

    void cloneAssignments(PermissionObject executor,PermissionObject assigner);


    <T> T getHolder(Class<?> holderClass);


    void checkDefaultGroupAssignment(Collection<PermissionObject> groups,Graph<PermissionScope> range);
}
