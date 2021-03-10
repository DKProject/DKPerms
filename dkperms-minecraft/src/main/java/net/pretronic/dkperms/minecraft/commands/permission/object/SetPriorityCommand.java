/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 03.05.20, 11:14
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission.object;

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class SetPriorityCommand extends ObjectCommand<PermissionObject> {

    public SetPriorityCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("setPriority","p","priority"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        if(arguments.length >= 1){
            if(GeneralUtil.isNaturalNumber(arguments[0])){
                int priority = Integer.parseInt(arguments[0]);
                object.setPriority(CommandUtil.getExecutor(sender),priority);
                sender.sendMessage(Messages.OBJECT_GROUP_PRIORITY_SET,VariableSet.create()
                        .addDescribed("group",object)
                        .addDescribed("object",object)
                        .addDescribed("priority",priority));
            }else{
                sender.sendMessage(Messages.INVALID_NUMBER,VariableSet.create()
                        .addDescribed("number",arguments[0]));
            }
        }else{
            CommandUtil.sendInvalidSyntax(sender,"priority","/perms <user/group> <name> setPriority <priority>");
        }
    }
}
