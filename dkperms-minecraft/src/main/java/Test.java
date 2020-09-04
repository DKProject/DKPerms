/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 30.08.20, 17:26
 * @website %web%
 *
 * %license%
 */

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.TimeoutAble;
import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;

import java.time.Duration;

public class Test {

    public static void main(String[] args) {

        PermissionObject object = null;

        //In this example we use the Super Administrator to change the permission. If this action is performed by a user, we recommend using that user as the executor.
        PermissionObject executor = DKPerms.getInstance().getObjectManager().getSuperAdministrator();

        String permission = "dkbans.command.ban";//The permission to set
        PermissionAction action = PermissionAction.ALLOW;//Allow the object to use this permission
        Duration duration = TimeoutAble.PERMANENTLY;//The duration, how long the permit is valid

        //Set the permission and get the created permission entity
        PermissionEntity result = object.setPermission(executor,object.getScope(),permission, action, duration);
    }
}
