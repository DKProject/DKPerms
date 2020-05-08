/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 24.02.20, 20:47
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission.object.group;

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class RemoveCommand extends ObjectCommand<PermissionObject> {

    public RemoveCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("remove","r","unset","u"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        if(arguments.length >= 1){
            PermissionObject group = CommandUtil.getGroup(sender, arguments[0]);
            if(group == null) return;

            CommandUtil.removeGroup(sender, object, arguments, group);
        }else{
            CommandUtil.sendInvalidSyntax(sender,"group set","/perms <user/group> <name> group set <key> <value> [scope]");
        }
    }
}
