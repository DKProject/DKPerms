/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 24.02.20, 20:47
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission.object.permission;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.message.bml.variable.describer.DescribedHashVariableSet;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

import java.util.concurrent.TimeUnit;

public class SetCommand extends ObjectCommand<PermissionObject> {

    public SetCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("set", "s", "add", "a"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        if(arguments.length >= 1) {
            String permission = arguments[0];

            PermissionAction action = PermissionAction.ALLOW;
            long time = -1;
            TimeUnit unit = TimeUnit.DAYS;
            PermissionScope scope = object.getScope();

            if (permission.startsWith("--")) {
                permission = permission.substring(2);
                action = PermissionAction.REJECT_ALWAYS;
            } else if (permission.startsWith("++")) {
                permission = permission.substring(2);
                action = PermissionAction.ALLOW_ALWAYS;
            } else if (permission.startsWith("-")) {
                permission = permission.substring(1);
                action = PermissionAction.REJECT;
            } else if (permission.startsWith("+")) {
                permission = permission.substring(1);
                action = PermissionAction.ALLOW;
            }

            for (int i = 1; i < arguments.length; i++) {
                String argument = arguments[i];
                if (GeneralUtil.isNaturalNumber(argument)) {
                    time = Long.parseLong(argument);
                } else {
                    PermissionAction action0 = PermissionAction.ofOrNull(argument);
                    if (action0 == null) {
                        TimeUnit unit0 = GeneralUtil.valueOfEnumOrNull(TimeUnit.class, argument.toUpperCase());//@Todo add alternative time unit options
                        if (unit0 == null) {
                            try{
                                scope = DKPerms.getInstance().getScopeManager().get(argument);
                                if(scope == null){
                                    sender.sendMessage(Messages.SCOPE_NOTFOUND, VariableSet.create().add("scope",argument));
                                    return;
                                }
                            } catch (IllegalArgumentException exception) {
                                sender.sendMessage(Messages.SCOPE_INVALID, VariableSet.create().add("scope", argument));
                                return;
                            }
                        } else unit = unit0;
                    } else action = action0;
                }
            }

            PermissionEntity entity = object.getPermission(scope, permission);

            if (entity == null) {
                entity = object.addPermission(null,scope, permission, action, time, unit);
            } else {
                entity.update(null,action,scope,time,unit);
            }

            sender.sendMessage(Messages.OBJECT_PERMISSION_SET, new DescribedHashVariableSet()
                    .add("type", object.getType().getName().toLowerCase())
                    .add("object", object)
                    .add("scope", scope)
                    .add("entry", entity)
                    .add("entity", entity)
                    .add("permission", permission)
                    .add("action", action)
                    .add("timeout", time)
                    .add("unit", unit)
                    .add("timeout", entity.getTimeoutFormatted())
                    .add("remaining", entity.getRemainingDurationFormatted()));
        }else{
            CommandUtil.sendInvalidSyntax(sender,"permission set","permission set <permission> [action] [time] [unit] [scope]");
        }
    }
}
