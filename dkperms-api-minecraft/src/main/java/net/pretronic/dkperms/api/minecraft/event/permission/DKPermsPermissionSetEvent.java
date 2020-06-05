/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 24.05.20, 20:26
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.minecraft.event.permission;

import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.minecraft.event.PermissionObjectEvent;

public interface DKPermsPermissionSetEvent extends PermissionObjectEvent {

    PermissionEntity getEntity();

}
