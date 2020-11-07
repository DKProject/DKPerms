/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 01.02.20, 21:38
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission.object.meta;

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class ClearCommand extends ObjectCommand<PermissionObject> {

    public ClearCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("clear","c"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        PermissionScope scope = CommandUtil.readScope(sender,object,arguments,0);
        if(scope == null) return;

        object.getMeta().clear(CommandUtil.getExecutor(sender),scope);

        VariableSet variables = VariableSet.create();
        variables.addDescribed("object",object).addDescribed("scope",scope);
        sender.sendMessage(Messages.OBJECT_META_CLEAR,variables);
    }
}
