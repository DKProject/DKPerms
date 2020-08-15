/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 06.12.19, 21:52
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.event.object;

import net.pretronic.dkperms.api.event.PermissionObjectEvent;
import net.pretronic.dkperms.api.object.PermissionObject;

public class DKPermsPermissionObjectDeleteEvent implements PermissionObjectEvent {

    private final PermissionObject executor;
    private final PermissionObject object;

    public DKPermsPermissionObjectDeleteEvent(PermissionObject executor, PermissionObject object) {
        this.executor = executor;
        this.object = object;
    }

    @Override
    public PermissionObject getExecutor() {
        return executor;
    }

    @Override
    public PermissionObject getObject() {
        return object;
    }
}
