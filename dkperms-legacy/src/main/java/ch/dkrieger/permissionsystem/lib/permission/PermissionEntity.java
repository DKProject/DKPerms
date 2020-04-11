/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 08.04.20, 10:24
 * @website %web%
 *
 * %license%
 */

package ch.dkrieger.permissionsystem.lib.permission;

import ch.dkrieger.permissionsystem.lib.entity.SimpleEntity;

public class PermissionEntity extends SimpleEntity {

    private final String permission;

    public PermissionEntity(Long timeout, String permission) {
        super(timeout);
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    public boolean isNegative(){
        return permission.startsWith("-");
    }

    public String getRawPermission(){
        if(permission.startsWith("-")) return permission.substring(1);
        else return permission;
    }
}
