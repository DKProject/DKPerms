/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 08.04.20, 10:24
 * @website %web%
 *
 * %license%
 */

package ch.dkrieger.permissionsystem.lib.permission.data;

import ch.dkrieger.permissionsystem.lib.permission.PermissionEntity;
import ch.dkrieger.permissionsystem.lib.utils.GeneralUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionData extends SimplePermissionData {

    private final Map<String,SimplePermissionData> serverPermissions, groupPermissions;

    public PermissionData() {
        this.serverPermissions = new ConcurrentHashMap<>();
        this.groupPermissions = new ConcurrentHashMap<>();
    }

    public Map<String, SimplePermissionData> getServerPermissions() {
        return serverPermissions;
    }

    public Map<String, SimplePermissionData> getGroupPermissions() {
        return groupPermissions;
    }

}
