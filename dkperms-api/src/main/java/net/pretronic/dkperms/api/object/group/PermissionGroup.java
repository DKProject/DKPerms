/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.object.group;

import net.pretronic.dkperms.api.object.PermissionObject;

public interface PermissionGroup extends PermissionObject {

    boolean isDefault();

    void setDefault(boolean defaultGroup);

}
