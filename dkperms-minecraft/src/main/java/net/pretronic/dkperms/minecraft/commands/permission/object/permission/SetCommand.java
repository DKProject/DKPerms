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

import net.pretronic.dkperms.api.entity.Entity;
import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.commands.UpdateModifier;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.duration.DurationProcessor;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

import java.time.Duration;

public class SetCommand extends ObjectCommand<PermissionObject> {

    public SetCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("set", "s", "add", "a"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        if(arguments.length >= 1) {
            String permission = arguments[0];

            PermissionAction action = PermissionAction.ALLOW;
            Duration duration = Entity.PERMANENTLY;
            PermissionScope scope = object.getScope();
            UpdateModifier modifier = UpdateModifier.REPLACE;

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
                UpdateModifier modifier0 = UpdateModifier.parse(argument);
                if(modifier0 == null){
                    try{
                        duration = DurationProcessor.getStandard().parse(argument);
                    }catch (IllegalArgumentException ignored){
                        scope = CommandUtil.readScope(sender, argument);
                        if (scope == null) return;
                    }
                }else{
                    modifier = modifier0;
                }
            }

            PermissionEntity entity = object.getPermission(scope, permission);

            if (entity == null) {
                entity = object.setPermission(CommandUtil.getExecutor(sender),scope, permission, action,duration);
            } else {
                if(modifier == UpdateModifier.FAIL){
                    sender.sendMessage(Messages.OBJECT_PERMISSION_SET_FAILED, VariableSet.createEmpty());
                    return;
                } else duration = modifier.take(entity.getRemainingDuration(),duration);
                entity.update(CommandUtil.getExecutor(sender),action,scope,duration);
            }

            sender.sendMessage(Messages.OBJECT_PERMISSION_SET, VariableSet.create()
                    .add("type", object.getType().getDisplayName().toLowerCase())
                    .addDescribed("object", object)
                    .addDescribed("scope", scope)
                    .addDescribed("entry", entity)
                    .addDescribed("entity", entity)
                    .add("permission", permission)
                    .add("action", action)
                    .add("timeout", entity.getTimeoutFormatted())
                    .add("remaining", entity.getRemainingDurationFormatted()));
        }else{
            CommandUtil.sendInvalidSyntax(sender,"permission set","/perms <user/group> <name> perm set <(action)perm> [duration] [modifier] [scope] ");
        }
    }
}
