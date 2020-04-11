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

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;

import java.util.concurrent.TimeUnit;

public interface Entity {

    int PERMANENTLY = -1;

    int SESSION = -2;

    int ONCE = -3;


    int getId();

    PermissionAction getAction();

    void updateAction(PermissionObject executor,PermissionAction action);


    PermissionScope getScope();

    void updateScope(PermissionObject executor,PermissionScope scope);


    long getTimeout();

    void updateTimeout(PermissionObject executor,long timeout);


    default void updateDuration(PermissionObject executor,long duration, TimeUnit unit){
        updateTimeout(executor,System.currentTimeMillis()+unit.toMillis(duration));
    }

    default long getRemainingDuration(){
        if(getTimeout() > 0) return System.currentTimeMillis()-getTimeout();
        return getTimeout();
    }

    default boolean hasTimeout(){
        return getTimeout() > 0 && getTimeout() < System.currentTimeMillis();
    }


    void remove(PermissionObject executor);
}
