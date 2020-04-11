/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 01.02.20, 21:35
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission.object.permission;

import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.MainObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import net.pretronic.dkperms.api.object.PermissionObject;

public class PermissionCommand extends MainObjectCommand<PermissionObject> {

    public PermissionCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("permission","permissions","perm","perms","p"));

        registerCommand(new SetCommand(owner));
        registerCommand(new UnsetCommand(owner));
        registerCommand(new ClearCommand(owner));
    }

    @Override
    public PermissionObject getObject(CommandSender sender,String name) {
        throw new UnsupportedOperationException("No objects available");
    }

}
