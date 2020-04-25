/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 24.04.20, 11:47
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.graph;

import net.pretronic.dkperms.api.entity.PermissionGroupEntity;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;

public interface GroupGraph extends Graph<PermissionGroupEntity> {

    PermissionGroupEntity getGroup(PermissionObject group);

    boolean containsGroup(PermissionObject group);

    PermissionAction calculateGroup(PermissionObject group);

}
