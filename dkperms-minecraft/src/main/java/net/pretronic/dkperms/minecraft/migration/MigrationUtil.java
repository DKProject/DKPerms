/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 29.05.20, 20:54
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.migration;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;

public class MigrationUtil {

    public static PermissionObject createOrGetGroup(String name) {
        PermissionObject object = DKPerms.getInstance().getObjectManager()
                .getObject(name, DKPermsConfig.OBJECT_GROUP_SCOPE, PermissionObjectType.GROUP);
        if(object == null){
            object = DKPerms.getInstance().getObjectManager()
                    .createObject(DKPermsConfig.OBJECT_GROUP_SCOPE, PermissionObjectType.GROUP,name);
        }
        return object;
    }
}
