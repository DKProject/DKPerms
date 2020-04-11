/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 01.02.20, 18:25
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission.object.group;

import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.MainObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import net.pretronic.dkperms.api.object.PermissionObject;

public class GroupCommand extends MainObjectCommand<PermissionObject> {

    public GroupCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("group","g"));

        registerCommand(new AddCommand(owner));
        registerCommand(new RemoveCommand(owner));
        registerCommand(new ClearCommand(owner));
    }

    @Override
    public PermissionObject getObject(CommandSender sender, String name) {
        return null;
    }
}
