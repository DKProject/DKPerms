/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 02.05.20, 13:34
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft;

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.libraries.event.Listener;
import org.mcnative.common.player.MinecraftPlayer;
import org.mcnative.service.event.player.MinecraftPlayerJoinEvent;
import org.mcnative.service.event.player.MinecraftPlayerWorldChangedEvent;
import org.mcnative.service.world.World;

public class MinecraftServiceListener {

    @Listener
    public void onPlayerJoin(MinecraftPlayerJoinEvent event){
        if(event.getOnlinePlayer().getWorld() != null){
            setWorldScope(event.getPlayer(),event.getOnlinePlayer().getWorld());
        }
    }

    @Listener
    public void onPlayerWorldChanged(MinecraftPlayerWorldChangedEvent event ){
        if(event.getTo() != null){
            setWorldScope(event.getPlayer(),event.getTo());
        }
    }

    private void setWorldScope(MinecraftPlayer player,World world){
        PermissionObject permissionObject = player.getAs(PermissionObject.class);
        PermissionScope scope = DKPermsConfig.SCOPE_CURRENT_INSTANCE_SCOPE
                .getChild(DKPermsConfig.SCOPE_WORLD_KEY,world.getName());
        permissionObject.setCurrentScope(scope);
    }

}
