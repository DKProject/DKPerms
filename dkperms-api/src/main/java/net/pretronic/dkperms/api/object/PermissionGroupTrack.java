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

import net.pretronic.dkperms.api.scope.PermissionScope;

public interface PermissionGroupTrack {

    int getId();

    String getName();

    void setName(String name);

    PermissionScope getScope();

    void setScope(PermissionScope scope);


    int getPosition(PermissionObject object);


    default boolean contains(PermissionObject object){
        return getPosition(object) > 0;
    }

    void addBefore(PermissionObject object);

    void addAfter(PermissionObject object);

    void addGroup(PermissionObject object, int position);


    void removeGroup(PermissionObject object);

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
