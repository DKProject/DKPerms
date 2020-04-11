/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 18.01.20, 23:06
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.graph;

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;

public interface ObjectGraph extends Graph<PermissionObject> {

    boolean contains(PermissionObject group);

    PermissionAction calculate(PermissionObject group);



}
