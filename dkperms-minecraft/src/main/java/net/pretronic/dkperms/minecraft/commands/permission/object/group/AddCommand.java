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

import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.search.ObjectSearchResult;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.dkperms.minecraft.config.Messages;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

//@Todo multiply add time with existing group
public class AddCommand extends ObjectCommand<PermissionObject> {

    public AddCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("add","a"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        if(arguments.length >= 1){
            PermissionObject group = CommandUtil.getGroup(sender, arguments[0]);
            if(group == null) return;
            CommandUtil.changeGroup(false,sender,object,group,arguments);
        }else{
            VariableSet variables = VariableSet.create();
            variables.add("command",getConfiguration().getName());
            variables.add("usage","/perms <user/group> <object> group add <name> {action} {time} {unit} {scope}");
            sender.sendMessage(Messages.COMMAND_INVALID_SYNTAX,variables);
        }
    }
}
