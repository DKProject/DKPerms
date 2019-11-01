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

import net.pretronic.dkperms.api.context.PermissionContext;
import net.pretronic.dkperms.api.context.PermissionContextAssignment;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public interface Entity {

    int PERMANENTLY = -1;

    int SESSION = -2;


    int getId();

    PermissionAction getAction();

    void setAction(PermissionAction action);


    PermissionScope getScope();

    void setScope(PermissionScope scope);


    PermissionContextAssignment getContext(PermissionContext context);

    Collection<PermissionContextAssignment> getContextAssignment();

    boolean setContext(PermissionContext context, Object value);

    boolean removeContext(PermissionContext context);


    long getTimeout();

    void setTimeout(long timeout);

    void setDuration(long duration, TimeUnit unit);

    long getRemainingDuration();

    boolean hasTimeout();
}
