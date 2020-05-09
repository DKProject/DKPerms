/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 07.04.20, 23:05
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.object.snapshot;

import net.pretronic.dkperms.api.graph.*;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.libraries.synchronisation.observer.Observable;
import net.pretronic.libraries.synchronisation.observer.ObserveCallback;

import java.util.List;

public interface PermissionObjectSnapshot extends Observable<PermissionObjectSnapshot,PermissionScope> {

    PermissionObject getObject();


    PermissionScope getScope();

    Graph<PermissionScope> getScopeRange();

    void setScope(PermissionScope scope);


    PermissionGraph getPermissionGraph();

    PermissionGraph getPermissionInheritanceGraph();


    GroupGraph getGroupGraph();

    GroupGraph getGroupInheritanceGraph();


    ObjectGraph getEffectedGroupGraph();

    ObjectGraph getEffectedGroupInheritanceGraph();


    ObjectMetaGraph getMetaGraph();

    ObjectMetaGraph getMetaInheritanceGraph();


    default PermissionAction isGroupSet(PermissionObject object){
        return getGroupInheritanceGraph().calculateGroup(object);
    }

    default PermissionAction isInGroup(PermissionObject object){
        return getGroupInheritanceGraph().calculateGroup(object);
    }


    default PermissionObject getHighestGroup(){
        return getEffectedGroupGraph().getHighest();
    }


    default boolean isPermissionSet(String permission){
        return getPermissionInheritanceGraph().containsPermission(permission);
    }


    default PermissionAction hasPermission(String permission){
        return getPermissionInheritanceGraph().calculatePermission(permission);
    }


    default ObjectMetaEntry getMeta(String key){
        return getMetaInheritanceGraph().calculate(key);
    }

    default boolean isMetaSet(String key){
        return getMetaInheritanceGraph().isSet(key);
    }

    default boolean isMetaSet(String key,Object value){
        return getMetaInheritanceGraph().isSet(key,value);
    }

}
