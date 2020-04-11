/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 07.12.19, 18:23
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft;

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.holder.PermissionObjectHolder;
import org.mcnative.common.player.MinecraftPlayer;
import org.mcnative.common.player.PlayerDesign;
import org.mcnative.common.serviceprovider.permission.PermissionHandler;
import org.mcnative.common.serviceprovider.permission.PermissionResult;

import java.util.Collection;
import java.util.function.BiFunction;

public class DKPermsPermissionGroup implements PermissionObjectHolder {

    private final PermissionObject object;

    public DKPermsPermissionGroup(PermissionObject object) {
        this.object = object;
    }

    @Override
    public PermissionObject getObject() {
        return object;
    }

}
