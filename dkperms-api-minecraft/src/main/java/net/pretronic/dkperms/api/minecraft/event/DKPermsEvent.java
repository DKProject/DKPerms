/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 24.05.20, 20:25
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.minecraft.event;

import net.pretronic.dkperms.api.DKPerms;
import org.mcnative.common.event.MinecraftEvent;

public interface DKPermsEvent extends MinecraftEvent {

    default DKPerms getDKPerms(){
        return DKPerms.getInstance();
    }

}
