/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.object.group;

import net.pretronic.dkperms.api.object.PermissionObject;

import java.util.List;

public interface PermissionGroupOrder {

    int getId();

    String getName();

    void setName();

    List<PermissionObject> getGroups();

    void addGroup(PermissionObject group);

    void addGroup(int index, PermissionObject group);

    void removeGroup(PermissionObject group);

    void clear();


    PermissionObject next(PermissionObject current);

    PermissionObject previous(PermissionObject current);

}
