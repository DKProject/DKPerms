/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 24.05.20, 13:33
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.event;

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectSnapshot;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.libraries.event.Cancellable;
import net.pretronic.libraries.utility.Validate;

public class DKPermsScopeChangeEvent implements DKPermsEvent, Cancellable {

    private final PermissionObject object;
    private final PermissionObjectSnapshot snapshot;
    private PermissionScope newScope;
    private boolean cancelled;

    public DKPermsScopeChangeEvent(PermissionObject object, PermissionObjectSnapshot snapshot, PermissionScope newScope) {
        this.object = object;
        this.snapshot = snapshot;
        this.newScope = newScope;
        this.cancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public PermissionObject getObject(){
        return object;
    }

    public PermissionObjectSnapshot getSnapshot(){
        return snapshot;
    }

    public PermissionScope getOldScope(){
        return snapshot.getScope();
    }

    public PermissionScope getNewScope(){
        return newScope;
    }

    public boolean isCurrentSnapshot(){
        return object.getCurrentSnapshot().equals(snapshot);
    }

    public void setNewScope(PermissionScope scope){
        Validate.notNull(scope);
        this.newScope = scope;
    }

}
