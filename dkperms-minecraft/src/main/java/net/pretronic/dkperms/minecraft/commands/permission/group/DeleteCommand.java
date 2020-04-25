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
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.describer.DescribedHashVariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class DeleteCommand extends ObjectCommand<PermissionObject> {

    public DeleteCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("delete","d"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        if(arguments.length == 1 && arguments[0].equalsIgnoreCase("confirm")){
            object.delete(null);//@Todo add executor
            sender.sendMessage(Messages.GROUP_DELETED,new DescribedHashVariableSet()
                    .add("group",object).add("object",object));
        }else{
            sender.sendMessage(Messages.GROUP_DELETE_CONFIRM,new DescribedHashVariableSet()
                    .add("group",object).add("object",object));
        }
    }
}
