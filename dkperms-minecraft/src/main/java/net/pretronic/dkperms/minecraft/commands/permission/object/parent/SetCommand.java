/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 24.02.20, 20:47
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission.object.parent;

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.Completable;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

import java.util.Collection;

//@Todo multiply add time with existing group
public class SetCommand extends ObjectCommand<PermissionObject> implements Completable {

    public SetCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("set","s"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        if(arguments.length >= 1){
            PermissionObject group = CommandUtil.getGroup(sender, arguments[0]);
            if(group == null) return;
            CommandUtil.changeGroup(true,sender,object,group,arguments);
        }else{
            VariableSet variables = VariableSet.create();
            variables.add("command","group set");
            variables.add("usage","/perms <user/group> <name> group set <name> {action} {time} {unit} {scope}");
            sender.sendMessage(Messages.COMMAND_INVALID_SYNTAX,variables);
        }
    }

    @Override
    public Collection<String> complete(CommandSender sender, String[] args) {
        return CommandUtil.completeGroups(sender,args);
    }
}
