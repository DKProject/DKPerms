/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 10.05.20, 20:21
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.object;

import net.pretronic.dkperms.api.logging.Loggable;
import net.pretronic.dkperms.api.scope.PermissionScope;

public interface PermissionObjectTrack extends Loggable {

    int getId();

    String getName();

    void setName(PermissionObject executor,String name);

    PermissionScope getScope();

    void setScope(PermissionObject executor,PermissionScope scope);


    int getPosition(PermissionObject object);


    default boolean contains(PermissionObject object){
        return getPosition(object) > 0;
    }

    void addBefore(PermissionObject executor,PermissionObject object);

    void addAfter(PermissionObject executor,PermissionObject object);

    void addGroup(PermissionObject executor,PermissionObject object, int position);


    void removeGroup(PermissionObject executor,PermissionObject object);

    void clearGroups();


    PermissionObject getFirstGroup();

    PermissionObject getLastGroup();

    PermissionObject getNextGroup(PermissionObject object);

    PermissionObject getPreviousGroup(PermissionObject object);

    int size();

    default boolean isEmpty(){
        return size() <= 0;
    }

}
