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
import net.pretronic.dkperms.api.scope.PermissionScope;

import java.util.concurrent.TimeUnit;

public interface PermissionEntityBuilder {

    PermissionEntityBuilder permission(String... permission);

    PermissionAction action(PermissionAction action);

    PermissionEntityBuilder scope(PermissionScope scope);

    PermissionEntityBuilder timeout(long timeout);

    PermissionEntityBuilder duration(long duration, TimeUnit unit);

}
