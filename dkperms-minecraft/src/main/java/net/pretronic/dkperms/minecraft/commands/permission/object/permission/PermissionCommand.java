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

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.DefinedNotFindable;
import net.pretronic.libraries.command.command.object.MainObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class PermissionCommand extends MainObjectCommand<PermissionObject> implements DefinedNotFindable<PermissionObject> {

    public PermissionCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("permission","permissions","perm","perms","p"));

        registerCommand(new SetCommand(owner));
        registerCommand(new UnsetCommand(owner));
        registerCommand(new ClearCommand(owner));
        registerCommand(new ShowCommand(owner));
        registerCommand(new ListCommand(owner));
        registerCommand(new TreeCommand(owner));
        registerCommand(new CheckCommand(owner));
    }

    @Override
    public PermissionObject getObject(CommandSender sender,String name) {
        throw new UnsupportedOperationException("No objects available (Objects should be forwarded)");
    }

    @Override
    public void commandNotFound(CommandSender sender, PermissionObject object, String command, String[] args) {
        sender.sendMessage(Messages.OBJECT_PERMISSION_HELP, VariableSet.create()
                .add("type",object.getType().getDisplayName().toLowerCase()));
    }
}
