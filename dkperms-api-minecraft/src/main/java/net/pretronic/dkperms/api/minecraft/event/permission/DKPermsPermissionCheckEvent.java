/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 24.05.20, 20:26
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.minecraft.event.permission;

import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.minecraft.event.DKPermsEvent;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;

public interface DKPermsPermissionCheckEvent extends DKPermsEvent {

    PermissionObject getObject();

    String getPermission();

    Graph<PermissionScope> getScopeRange();

    PermissionAction getAction();

    void setAction(PermissionAction action);

}
