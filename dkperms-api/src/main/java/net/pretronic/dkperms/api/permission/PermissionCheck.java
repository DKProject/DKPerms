/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.permission;

import net.pretronic.dkperms.api.context.PermissionContext;
import net.pretronic.dkperms.api.scope.PermissionScope;

public interface PermissionCheck {

    PermissionCheck permission(String... permission);

    PermissionCheck scope(PermissionScope scope);

    PermissionCheck context(PermissionContext contexts);

    PermissionAction check();

    boolean has();

}
