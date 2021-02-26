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

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.event.EventPriority;
import net.pretronic.libraries.event.Listener;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.event.player.login.MinecraftPlayerLoginEvent;
import org.mcnative.runtime.api.player.ConnectedMinecraftPlayer;

public class PlayerListener {

    @Listener(priority = EventPriority.LOWEST)
    public void onPlayerJoin(MinecraftPlayerLoginEvent event){
        event.getPlayer().getPermissionHandler();//Trigger loading
        if(DKPermsConfig.SERVER_JOIN_REQUIRED_PERMISSION_ENABLED){
            if(!event.getPlayer().hasPermission(DKPermsConfig.SERVER_JOIN_REQUIRED_PERMISSION_PERMISSION)){
                event.setCancelReason(Messages.SERVER_JOIN_NOT_PERMITTED, VariableSet.create()
                        .addDescribed("server",McNative.getInstance().getLocal()));
                event.setCancelled(true);
                return;
            }
        }

        if(DKPermsConfig.SERVER_JOIN_REQUIRED_JOINPOWER > 0){
            PermissionObject object = event.getPlayer().getAs(PermissionObject.class);
            ObjectMetaEntry entry = object.getCurrentSnapshot().getMeta("joinpower");
            if(entry == null || entry.getIntValue() < DKPermsConfig.SERVER_JOIN_REQUIRED_JOINPOWER){
                event.setCancelReason(Messages.SERVER_JOIN_NOT_PERMITTED, VariableSet.create()
                        .addDescribed("server",McNative.getInstance().getLocal()));
                event.setCancelled(true);
                return;
            }
        }

        if(DKPermsConfig.SERVER_JOIN_FULL_ENABLED
                && McNative.getInstance().getLocal().getOnlineCount() >= McNative.getInstance().getLocal().getMaxPlayerCount()){

            if(event.getPlayer().hasPermission(DKPermsConfig.SERVER_JOIN_FULL_PERMISSION)){
                if(DKPermsConfig.SERVER_JOIN_FULL_PRIORITY_KICK && !kickLowestPlayer(event.getPlayer().getDesign().getPriority())){
                    event.setCancelled(true);
                    event.setCancelReason(Messages.SERVER_JOIN_FULL_NO_KICK, VariableSet.create()
                            .add("playerCount",McNative.getInstance().getLocal().getMaxPlayerCount())
                            .addDescribed("server",McNative.getInstance().getLocal()));
                }else{
                    event.setCancelled(false);
                }
            }else{
                event.setCancelled(true);
                event.setCancelReason(Messages.SERVER_JOIN_FULL, VariableSet.create()
                        .add("playerCount",McNative.getInstance().getLocal().getMaxPlayerCount())
                        .addDescribed("server",McNative.getInstance().getLocal()));
            }
        }
    }

    private boolean kickLowestPlayer(int playerPriority){
        ConnectedMinecraftPlayer nextPlayer = null;
        int bestPriority = Integer.MIN_VALUE;//10
        for (ConnectedMinecraftPlayer player : McNative.getInstance().getLocal().getConnectedPlayers()) {
            int priority = player.getDesign().getPriority();
            if(priority > bestPriority){
                bestPriority = priority;
                nextPlayer = player;
            }
        }

        if(bestPriority > playerPriority){
            nextPlayer.disconnect(Messages.SERVER_JOIN_FULL_KICK);
            return true;
        }
        return false;
    }

}
