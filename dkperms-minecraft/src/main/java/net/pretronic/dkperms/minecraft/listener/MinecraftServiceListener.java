/*
 * (C) Copyright 2021 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 26.02.21, 15:31
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.listener;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.common.object.DefaultPermissionObject;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.libraries.event.Listener;
import org.mcnative.runtime.api.player.MinecraftPlayer;
import org.mcnative.runtime.api.service.event.player.MinecraftPlayerJoinEvent;
import org.mcnative.runtime.api.service.event.player.MinecraftPlayerWorldChangedEvent;
import org.mcnative.runtime.api.service.world.World;

import java.util.Collection;

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

        Collection<PermissionObject> result = DKPerms.getInstance().getObjectManager()
                .getDefaultGroups(permissionObject.getCurrentSnapshot().getScopeRange());
        if(permissionObject instanceof DefaultPermissionObject){
            permissionObject.checkDefaultGroupAssignment(result,permissionObject.getCurrentSnapshot().getScopeRange());
        }
    }
}
