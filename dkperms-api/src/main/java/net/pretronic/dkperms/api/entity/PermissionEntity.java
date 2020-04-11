/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.entity;

import net.pretronic.dkperms.api.permission.PermissionAction;

public interface PermissionEntity extends Entity {

    String[] getNodes();

    String getPermission();

    PermissionAction check(String permission);

    PermissionAction check(String[] nodes);

    boolean matches(String[] nodes);
}
