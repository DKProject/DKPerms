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

public class RenameCommand extends ObjectCommand<PermissionObject> {

    public RenameCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("rename","r"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        if(arguments.length >= 1){
            String name = arguments[0];
            String oldName = object.getName();
            object.setName(null,name);
            sender.sendMessage(Messages.GROUP_RENAMED,VariableSet.create()
                    .add("oldName",oldName)
                    .add("newName",name)
                    .addDescribed("group",object));
        }else{
            CommandUtil.sendInvalidSyntax(sender,"group rename","/perms group <name> rename <newName>");
        }
    }
}
