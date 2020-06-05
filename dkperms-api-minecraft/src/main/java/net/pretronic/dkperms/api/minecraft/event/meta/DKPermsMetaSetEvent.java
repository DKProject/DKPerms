/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 24.05.20, 20:27
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.minecraft.event.meta;

import net.pretronic.dkperms.api.minecraft.event.PermissionObjectEvent;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;

public interface DKPermsMetaSetEvent extends PermissionObjectEvent {

    ObjectMetaEntry getEntry();

}
