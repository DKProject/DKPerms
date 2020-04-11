/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 08.04.20, 10:24
 * @website %web%
 *
 * %license%
 */

package ch.dkrieger.permissionsystem.lib.group;

import ch.dkrieger.permissionsystem.lib.entity.SimpleEntity;

import java.util.UUID;

public class PermissionGroupEntity extends SimpleEntity {

    private final UUID group;

    public PermissionGroupEntity(Long timeout, UUID group) {
        super(timeout);
        this.group = group;
    }

    public UUID getGroupUUID() {
        return group;
    }
}
