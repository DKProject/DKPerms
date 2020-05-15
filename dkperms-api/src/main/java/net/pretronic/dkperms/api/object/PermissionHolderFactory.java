/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 10.05.20, 20:35
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.object;

public interface PermissionHolderFactory {

    Object create(PermissionObject object);

}
