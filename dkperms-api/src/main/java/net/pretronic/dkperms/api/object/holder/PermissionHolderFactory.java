/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 07.12.19, 16:33
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.object.holder;

import net.pretronic.dkperms.api.object.PermissionObject;

public interface PermissionHolderFactory {

    PermissionObjectHolder create(PermissionObject object);

}
