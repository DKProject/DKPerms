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

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;

import java.util.concurrent.TimeUnit;

public interface Entity {

    int PERMANENTLY = -1;

    int SESSION = -2;

    int ONCE = -3;


    int getId();

    PermissionObject getOwner();


    PermissionAction getAction();

    void setAction(PermissionObject executor,PermissionAction action);


    PermissionScope getScope();

    void setScope(PermissionObject executor,PermissionScope scope);


    long getTimeout();

    void setTimeout(PermissionObject executor,long timeout);


    default void setDuration(PermissionObject executor,long duration, TimeUnit unit){
        setTimeout(executor,System.currentTimeMillis()+unit.toMillis(duration));
    }

    default long getRemainingDuration(){
        if(getTimeout() > 0) return System.currentTimeMillis()-getTimeout();
        return getTimeout();
    }

    default boolean hasTimeout(){
        return getTimeout() > 0 && getTimeout() < System.currentTimeMillis();
    }

    default String getTimeoutFormatted(){
        return DKPerms.getInstance().getFormatter().formatDateTime(getTimeout());
    }

    default String getRemainingDurationFormatted(){
        return DKPerms.getInstance().getFormatter().formatRemainingDuration(getTimeout());
    }

    void update(PermissionObject executor,PermissionAction action,PermissionScope scope,long timeout);

    default void update(PermissionObject executor,PermissionAction action,PermissionScope scope,long duration,TimeUnit unit){
        update(executor, action, scope, duration>0?unit.toMillis(duration):duration);
    }
}
