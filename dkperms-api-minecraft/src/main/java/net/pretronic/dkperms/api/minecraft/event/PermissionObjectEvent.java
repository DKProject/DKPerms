/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 30.05.20, 22:09
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.minecraft.event;

import net.pretronic.dkperms.api.object.PermissionObject;

public interface PermissionObjectEvent extends DKPermsEvent {

    PermissionObject getExecutor();

    PermissionObject getObject();

}
