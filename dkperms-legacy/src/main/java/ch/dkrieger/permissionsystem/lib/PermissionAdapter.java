/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 08.04.20, 10:24
 * @website %web%
 *
 * %license%
 */

package ch.dkrieger.permissionsystem.lib;

import ch.dkrieger.permissionsystem.lib.group.PermissionGroupEntity;
import ch.dkrieger.permissionsystem.lib.permission.data.PermissionData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PermissionAdapter {

    protected String name;
    protected UUID uuid;
    protected PermissionType type;
    protected PermissionData permissiondata;
    protected List<PermissionGroupEntity> groups;

    public PermissionAdapter(String name, UUID uuid, PermissionType type) {
        this(name,uuid,type,new PermissionData(),new ArrayList<>());
    }

    public PermissionAdapter(String name, UUID uuid, PermissionType type, PermissionData permissiondata, List<PermissionGroupEntity> groups) {
        this.name = name;
        this.uuid = uuid;
        this.type = type;
        this.permissiondata = permissiondata;
        this.groups = groups;
    }


    public String getName() {
        return name;
    }

    public UUID getUUID() {
        return uuid;
    }

    public PermissionType getType() {
        return type;
    }

}
