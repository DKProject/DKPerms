/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft;

import org.mcnative.common.player.MinecraftPlayer;
import org.mcnative.common.player.PlayerDesign;
import org.mcnative.common.player.permission.PermissionHandler;

import java.util.Collection;
import java.util.function.BiFunction;

public class DKPermsPermissionHandler implements PermissionHandler {

    public Collection<MinecraftPlayer> getOperators() {
        return null;
    }

    public Collection<String> getGroups(MinecraftPlayer minecraftPlayer) {
        return null;
    }

    public Collection<String> getPermissions(MinecraftPlayer minecraftPlayer) {
        return null;
    }

    public Collection<String> getAllPermissions(MinecraftPlayer minecraftPlayer) {
        return null;
    }

    public PlayerDesign getPlayerDesign(MinecraftPlayer minecraftPlayer) {
        return null;
    }

    public boolean isPermissionSet(MinecraftPlayer minecraftPlayer, String s) {
        return false;
    }

    public boolean hasPermission(MinecraftPlayer minecraftPlayer, String s) {
        return false;
    }

    public boolean isOperator(MinecraftPlayer minecraftPlayer) {
        return false;
    }

    public void addPermission(MinecraftPlayer minecraftPlayer, String s) {

    }

    public void removePermission(MinecraftPlayer minecraftPlayer, String s) {

    }

    public void setOperator(MinecraftPlayer minecraftPlayer, boolean b) {

    }

    public void setPlayerDesignGetter(BiFunction<MinecraftPlayer, PlayerDesign, PlayerDesign> biFunction) {

    }
}
