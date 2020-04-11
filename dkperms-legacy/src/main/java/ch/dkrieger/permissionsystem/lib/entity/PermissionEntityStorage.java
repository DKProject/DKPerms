/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 08.04.20, 10:24
 * @website %web%
 *
 * %license%
 */

package ch.dkrieger.permissionsystem.lib.entity;

import ch.dkrieger.permissionsystem.lib.PermissionType;
import ch.dkrieger.permissionsystem.lib.group.PermissionGroupEntity;

import java.util.List;
import java.util.UUID;

public interface PermissionEntityStorage {

    List<PermissionGroupEntity> getPermissionEntities(PermissionType type, UUID uuid);

    void setEntity(PermissionType type, UUID uuid, UUID group, Long timeout);

    void addEntity(PermissionType type, UUID uuid, UUID group, Long timeout);

    void removeEntity(PermissionType type, UUID uuid, UUID group);

    void clearEntity(PermissionType type, UUID uuid);

}
