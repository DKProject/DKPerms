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

import ch.dkrieger.permissionsystem.lib.PermissionType;
import ch.dkrieger.permissionsystem.lib.permission.data.PermissionData;

import java.util.UUID;

public interface PermissionStorage {

    PermissionData getPermissions(PermissionType type, UUID uuid);

    //add permissions
    void addPermission(PermissionType type, UUID uuid, String permission, String world, Long timeout);

    void addServerPermission(PermissionType type, UUID uuid, String server, String permission, String world, Long timeout);

    void addServerGroupPermission(PermissionType type, UUID uuid, String group, String permission, String world, Long timeout);

    //remove
    void removePermission(PermissionType type, UUID uuid, String permission, String world);

    void removeServerPermission(PermissionType type, UUID uuid, String server, String permission, String world);

    void removeServerGroupPermission(PermissionType type, UUID uuid, String group, String permission, String world);

    //clear
    void clearAllPermissions(PermissionType type, UUID uuid);

    void clearPermissions(PermissionType type, UUID uuid, String world);

    void clearServerPermission(PermissionType type, UUID uuid, String server, String world);

    void clearServerGroupPermission(PermissionType type, UUID uuid, String group, String world);

    void onTimeOutDeleteTask();

}
