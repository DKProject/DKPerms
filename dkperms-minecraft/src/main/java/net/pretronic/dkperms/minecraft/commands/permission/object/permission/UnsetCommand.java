/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 01.02.20, 21:38
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission.object.permission;

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class UnsetCommand extends ObjectCommand<PermissionObject> {

    public UnsetCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("unset","u"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        if(arguments.length >= 1){
            String permission = arguments[0];

            PermissionScope scope = CommandUtil.readScope(sender,object,arguments,1);
            if(scope == null) return;

            object.unsetPermission(null,permission);

            VariableSet variables = VariableSet.create();
            variables.add("type",object.getType().getName().toLowerCase());
            variables.addDescribed("object",object);
            variables.add("permission",permission);
            variables.add("action", PermissionAction.NEUTRAL);
            variables.addDescribed("scope",scope);
            variables.add("timeout", DKPermsConfig.FORMAT_DATE_ENDLESSLY);
            variables.add("remaining", DKPermsConfig.FORMAT_DATE_ENDLESSLY);
            sender.sendMessage(Messages.OBJECT_PERMISSION_UNSET,variables);
        }
    }
}
