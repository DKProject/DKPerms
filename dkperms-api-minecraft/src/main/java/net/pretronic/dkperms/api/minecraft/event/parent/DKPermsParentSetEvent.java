/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 24.05.20, 20:27
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.minecraft.event.parent;

import net.pretronic.dkperms.api.entity.ParentEntity;
import net.pretronic.dkperms.api.minecraft.event.PermissionObjectEvent;

public interface DKPermsParentSetEvent extends PermissionObjectEvent {

    ParentEntity getEntity();

}
