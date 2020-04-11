/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 18.01.20, 22:37
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.graph;

import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.permission.PermissionAction;

import java.util.List;

public interface PermissionGraph extends Graph<PermissionEntity> {

    boolean containsPermission(String permission);

    PermissionAction calculatePermission(String permission);

    List<String> toStringList();

}
