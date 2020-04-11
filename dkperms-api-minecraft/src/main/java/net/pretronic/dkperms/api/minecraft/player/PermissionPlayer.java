/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.minecraft.player;

import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.holder.PermissionObjectHolder;
import net.pretronic.dkperms.api.scope.PermissionScope;
import org.mcnative.common.player.MinecraftPlayer;
import org.mcnative.common.serviceprovider.permission.PermissionHandler;

public interface PermissionPlayer extends PermissionHandler, PermissionObjectHolder {

    Graph<PermissionScope> getCurrentScopes();

    PermissionScope getCurrentScope();

    void setCurrentScope(PermissionScope scope);

}
