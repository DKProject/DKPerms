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

import net.prematic.libraries.plugin.lifecycle.Lifecycle;
import net.prematic.libraries.plugin.lifecycle.LifecycleState;
import org.mcnative.common.plugin.MinecraftPlugin;

public class DKPermsPlugin extends MinecraftPlugin {

    @Lifecycle(state = LifecycleState.BOOTSTRAP)
    public void onBootstrap(LifecycleState state){
        getLogger().info("DKPerms is starting, please wait..");



    }

}
