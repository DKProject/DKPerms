/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 15.04.20, 17:33
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission.group;

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class DeleteCommand extends ObjectCommand<PermissionObject> {

    public DeleteCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("delete","d"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        object.delete(CommandUtil.getExecutor(sender));
        sender.sendMessage(Messages.GROUP_DELETED, VariableSet.create()
                .addDescribed("group",object)
                .addDescribed("object",object));
        /*
        if(arguments.length == 1 && arguments[0].equalsIgnoreCase("confirm")){
            object.delete(null);//@Todo add executor
            sender.sendMessage(Messages.GROUP_DELETED, VariableSet.create()
                    .addDescribed("group",object)
                    .addDescribed("object",object));
        }else{
            sender.sendMessage(Messages.GROUP_DELETE_CONFIRM,VariableSet.create()
                    .addDescribed("group",object)
                    .addDescribed("object",object));
        }
         */
    }
}
