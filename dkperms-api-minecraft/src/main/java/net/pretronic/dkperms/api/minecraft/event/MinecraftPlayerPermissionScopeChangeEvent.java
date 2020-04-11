/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 05.12.19, 20:11
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.minecraft.event;

import net.pretronic.libraries.event.Cancellable;
import net.pretronic.dkperms.api.minecraft.player.PermissionPlayer;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.scope.PermissionScope;
import org.mcnative.common.event.player.MinecraftPlayerEvent;
import org.mcnative.common.player.MinecraftPlayer;

public class MinecraftPlayerPermissionScopeChangeEvent implements MinecraftPlayerEvent, Cancellable {

    private final PermissionPlayer permissionPlayer;
    private PermissionScope newScope;
    private boolean cancelled;

    public MinecraftPlayerPermissionScopeChangeEvent(PermissionPlayer permissionPlayer, PermissionScope newScope) {
        this.permissionPlayer = permissionPlayer;
        this.newScope = newScope;
        this.cancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public MinecraftPlayer getPlayer() {
        return null;//permissionPlayer;//@Todo properly update to real player
    }

    public PermissionScope getScope(){
        return permissionPlayer.getCurrentScope();
    }

    public PermissionScope getNewScope(){
        return newScope;
    }

    public void setNewScope(PermissionScope scope){
        newScope = scope;
    }

    public PermissionPlayer getPermissionPlayer(){
        return permissionPlayer;
    }

    public PermissionObject getObject(){
        return permissionPlayer.getObject();
    }
}
