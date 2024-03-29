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

import net.pretronic.dkperms.api.entity.PermissionEntity;
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

public class ShowCommand extends ObjectCommand<PermissionObject> {

    public ShowCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("show","get","g"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        if(arguments.length >= 1){
            String permission = arguments[0];

            PermissionScope scope = CommandUtil.readScope(sender,object,arguments,1);
            if(scope == null) return;

            PermissionEntity entry = object.getPermission(scope,permission);

            if(entry == null){
                sender.sendMessage(Messages.OBJECT_PERMISSION_NOT_SET,VariableSet.create()
                        .add("type", object.getType().getDisplayName().toLowerCase())
                        .add("permission", permission)
                        .add("action", PermissionAction.NEUTRAL)
                        .add("timeout", DKPermsConfig.FORMAT_DATE_ENDLESSLY)
                        .add("remaining", DKPermsConfig.FORMAT_DATE_ENDLESSLY)
                        .addDescribed("object", object)
                        .addDescribed("scope",scope));
                return;
            }


            VariableSet variables = VariableSet.create()
                    .add("type", object.getType().getDisplayName().toLowerCase())
                    .addDescribed("object", object)
                    .addDescribed("scope", scope)
                    .addDescribed("entry", entry)
                    .addDescribed("entity", entry)
                    .add("permission", permission)
                    .add("action", entry.getAction())
                    .add("timeout", entry.getTimeoutFormatted())
                    .add("remaining", entry.getRemainingDurationFormatted());
            sender.sendMessage(Messages.OBJECT_PERMISSION_SHOW,variables);
        }else{
            CommandUtil.sendInvalidSyntax(sender,"permission show","/perms <user/group> <name> perm show <key> [scope]");
        }
    }
}
