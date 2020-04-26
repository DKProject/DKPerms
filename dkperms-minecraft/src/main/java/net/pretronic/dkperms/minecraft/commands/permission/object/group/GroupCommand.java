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

import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.DefinedNotFindable;
import net.pretronic.libraries.command.command.object.MainObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import net.pretronic.dkperms.api.object.PermissionObject;

public class GroupCommand extends MainObjectCommand<PermissionObject> implements DefinedNotFindable<PermissionObject> {

    public GroupCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("group","g"));

        registerCommand(new SetCommand(owner));
        registerCommand(new AddCommand(owner));
        registerCommand(new RemoveCommand(owner));
        registerCommand(new ClearCommand(owner));
        registerCommand(new ListCommand(owner));
        registerCommand(new TreeCommand(owner));
    }

    @Override
    public PermissionObject getObject(CommandSender sender, String name) {
        throw new UnsupportedOperationException("No objects available (Objects should be forwarded)");
    }

    @Override
    public void commandNotFound(CommandSender sender, PermissionObject object, String command, String[] args) {
        sender.sendMessage(Messages.OBJECT_GROUP_HELP, VariableSet.create()
                .add("type",object.getType().getName().toLowerCase()));
    }
}
