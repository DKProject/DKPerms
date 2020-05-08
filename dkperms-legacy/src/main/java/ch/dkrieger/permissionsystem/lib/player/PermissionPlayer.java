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

import ch.dkrieger.permissionsystem.lib.PermissionAdapter;
import ch.dkrieger.permissionsystem.lib.PermissionType;

import java.util.UUID;

public class PermissionPlayer extends PermissionAdapter {

    private final int id;

    public PermissionPlayer(int id, String name, UUID uuid) {
        super(name,uuid,PermissionType.PLAYER);
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public void setName(String name){
        this.name = name;
    }

}
