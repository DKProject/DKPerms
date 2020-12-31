/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 30.12.20, 17:50
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft;

import net.pretronic.libraries.utility.Validate;
import org.mcnative.runtime.api.event.player.design.MinecraftPlayerDesignUpdateEvent;
import org.mcnative.runtime.api.player.MinecraftPlayer;
import org.mcnative.runtime.api.player.PlayerDesign;

public class DKPermsMinecraftPlayerDesignUpdateEvent implements MinecraftPlayerDesignUpdateEvent {

    private final MinecraftPlayer player;
    private PlayerDesign design;

    public DKPermsMinecraftPlayerDesignUpdateEvent(MinecraftPlayer player, PlayerDesign design) {
        this.player = player;
        this.design = design;
    }

    @Override
    public PlayerDesign getDesign() {
        return design;
    }

    @Override
    public void setDesign(PlayerDesign design) {
        Validate.notNull(design);
        this.design = design;
    }

    @Override
    public MinecraftPlayer getPlayer() {
        return player;
    }
}
