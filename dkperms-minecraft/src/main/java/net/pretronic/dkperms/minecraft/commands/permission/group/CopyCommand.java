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

public class CopyCommand extends ObjectCommand<PermissionObject> {

    public CopyCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("copy","clone","c"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        if(arguments.length < 1){
            CommandUtil.sendInvalidSyntax(sender,"group copy","/dkperms group <name> copy <newName>");
            return;
        }
        PermissionObject newObject = object.clone(CommandUtil.getExecutor(sender),arguments[0]);
        sender.sendMessage(Messages.GROUP_COPY, VariableSet.create()
                .addDescribed("group",object)
                .addDescribed("newObject",newObject)
                .addDescribed("object",object));
    }
}
