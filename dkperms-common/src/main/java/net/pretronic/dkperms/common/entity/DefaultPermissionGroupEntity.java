/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 09.11.19, 16:49
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.entity;

import net.pretronic.dkperms.api.entity.PermissionGroupEntity;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;

import java.util.Collection;

public class DefaultPermissionGroupEntity implements PermissionGroupEntity {

    private final int id;
    private final PermissionObject group;

    private PermissionAction action;
    private PermissionScope scope;
    private long timeout;

    public DefaultPermissionGroupEntity(int id, PermissionObject group, PermissionAction action, PermissionScope scope, long timeout) {
        this.id = id;
        this.group = group;
        this.action = action;
        this.scope = scope;
        this.timeout = timeout;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public PermissionAction getAction() {
        return action;
    }

    @Override
    public void updateAction(PermissionObject executor, PermissionAction action) {

    }

    @Override
    public PermissionScope getScope() {
        return scope;
    }

    @Override
    public void updateScope(PermissionObject executor, PermissionScope scope) {

    }


    @Override
    public PermissionObject getGroup() {
        return group;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public void updateTimeout(PermissionObject executor, long timeout) {

    }

    @Override
    public void remove(PermissionObject executor) {

    }

}
