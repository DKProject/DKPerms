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

import java.util.List;
import java.util.UUID;

public interface PermissionGroupStorage {

    List<PermissionGroup> loadGroups();

    PermissionGroup createGroup(String name);

    void deleteGroup(UUID uuid);

    void setSetting(UUID uuid, String identifier, Object value);

    List<UUID> getPlayers(PermissionGroup group);

}
