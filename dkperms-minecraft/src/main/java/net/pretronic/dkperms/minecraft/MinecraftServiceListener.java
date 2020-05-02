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
import org.mcnative.service.event.player.MinecraftPlayerWorldChangedEvent;

public class MinecraftServiceListener {

    @Listener
    public void onPlayerWorldChanged(MinecraftPlayerWorldChangedEvent event ){
        PermissionObject permissionObject = event.getPlayer().getAs(PermissionObject.class);
        if(permissionObject != null){
            PermissionScope scope = DKPermsConfig.SCOPE_CURRENT_INSTANCE_SCOPE
                    .getChild(DKPermsConfig.SCOPE_WORLD_KEY,event.getTo().getName());
            permissionObject.setCurrentScope(scope);
        }
    }

}
