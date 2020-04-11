/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 08.04.20, 10:24
 * @website %web%
 *
 * %license%
 */

package ch.dkrieger.permissionsystem.lib.player;

import java.util.Collection;
import java.util.UUID;

public interface PermissionPlayerStorage {

    Collection<PermissionPlayer> getPlayers() throws Exception;

    PermissionPlayer getPermissionPlayer(UUID uuid) throws Exception;

    PermissionPlayer getPermissionPlayer(String name) throws Exception;

    PermissionPlayer createPermissionPlayer(UUID uuid, String name);

    void updateName(UUID uuid, String name);

}
