/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 19.01.20, 12:47
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission;

import net.pretronic.dkperms.minecraft.commands.MigrationCommand;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.NotFindable;
import net.pretronic.libraries.command.NotFoundHandler;
import net.pretronic.libraries.command.command.MainCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import net.pretronic.dkperms.minecraft.commands.permission.object.GroupCommand;
import net.pretronic.dkperms.minecraft.commands.permission.object.UserCommand;

public class PermissionCommand extends MainCommand implements NotFindable {

    public PermissionCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
        registerCommand(new UserCommand(owner));
        registerCommand(new GroupCommand(owner));
        registerCommand(new MigrationCommand(owner));
    }

    @Override
    public void commandNotFound(CommandSender sender, String s, String[] strings) {
        sender.sendMessage(Messages.COMMAND_PERMS_HELP);
    }

}
