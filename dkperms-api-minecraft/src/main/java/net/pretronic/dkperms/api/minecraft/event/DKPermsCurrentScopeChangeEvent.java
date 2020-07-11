/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 24.05.20, 13:33
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.minecraft.event;

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.libraries.event.Cancellable;

public interface DKPermsCurrentScopeChangeEvent extends DKPermsEvent, Cancellable {

    PermissionObject getObject();

    PermissionScope getOldScope();

    PermissionScope getNewScope();

    void setNewScope(PermissionScope scope);

}
