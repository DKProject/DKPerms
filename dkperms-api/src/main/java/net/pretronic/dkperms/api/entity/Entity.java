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

import net.pretronic.dkperms.api.TimeoutAble;
import net.pretronic.dkperms.api.logging.Loggable;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public interface Entity extends TimeoutAble, Loggable {

    Duration PERMANENTLY = TimeoutAble.PERMANENTLY;

    int getId();

    PermissionObject getOwner();


    PermissionAction getAction();

    void setAction(PermissionObject executor,PermissionAction action);


    PermissionScope getScope();

    void setScope(PermissionObject executor,PermissionScope scope);


    void update(PermissionObject executor,PermissionAction action,PermissionScope scope,long timeout);

    default void update(PermissionObject executor, PermissionAction action, PermissionScope scope, Duration duration){
        update(executor, action, scope, duration.getSeconds(),TimeUnit.SECONDS);
    }

    default void update(PermissionObject executor,PermissionAction action,PermissionScope scope,long duration,TimeUnit unit){
        update(executor, action, scope, duration>0?unit.toMillis(duration)+System.currentTimeMillis():duration);
    }
}
