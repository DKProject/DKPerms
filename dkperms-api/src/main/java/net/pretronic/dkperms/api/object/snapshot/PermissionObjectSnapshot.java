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

import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.entity.PermissionGroupEntity;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.graph.ObjectGraph;
import net.pretronic.dkperms.api.graph.ObjectMetaGraph;
import net.pretronic.dkperms.api.graph.PermissionGraph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;

import java.util.Collection;

public interface PermissionObjectSnapshot {

    PermissionObject getObject();


    PermissionScope getCurrentScope();

    Graph<PermissionScope> getScopeRange();

    void setCurrentScope(PermissionScope scope);


    PermissionGraph getPermissionGraph();

    PermissionGraph getPermissionInheritanceGraph();


    ObjectMetaGraph getMetaGraph();

    ObjectMetaGraph getMetaInheritanceGraph();

    ObjectMetaGraph getGroupGraph();

    ObjectGraph getGroupInheritanceGraph();


    default PermissionAction isInGroup(PermissionObject object){
        return getGroupInheritanceGraph().calculate(object);
    }

    default boolean isGroupSet(PermissionObject object){
        return getGroupInheritanceGraph().contains(object);
    }


    default boolean isInInheritanceGroup(PermissionObject object){
        return false;
    }



    PermissionObject getHighestGroup();

    Collection<PermissionGroupEntity> getGroupEntities();

    Collection<PermissionObject> getGroups();




    default Collection<PermissionEntity> getPermissionsEntities(){
        return getPermissionGraph().traverse();
    }

    default Collection<String> getPermissions(){
        return getPermissionGraph().toStringList();
    }

    default boolean isPermissionSet(String permission){
        return getPermissionInheritanceGraph().containsPermission(permission);
    }

    default PermissionAction hasPermission(String permission){
        return getPermissionInheritanceGraph().calculatePermission(permission);
    }




    ObjectMetaEntry getMetaEntries();

    ObjectMetaEntry getMeta(String key);

    boolean containsMeta(String key);

    boolean isMetaSet(String key,Object value);

}
