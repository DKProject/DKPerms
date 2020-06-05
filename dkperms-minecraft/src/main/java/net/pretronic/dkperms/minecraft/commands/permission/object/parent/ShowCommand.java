/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 01.02.20, 21:38
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission.object.parent;

import net.pretronic.dkperms.api.entity.ParentEntity;
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
            PermissionObject group = CommandUtil.getGroup(sender, arguments[0]);
            if(group == null) return;

            PermissionScope scope = CommandUtil.readScope(sender,object,arguments,1);
            if(scope == null) return;

            ParentEntity entity = object.getParent(scope,group);

            if(entity == null){
                sender.sendMessage(Messages.OBJECT_GROUP_NOT_SET,VariableSet.create()
                        .add("type", object.getType().getName().toLowerCase())
                        .add("action", PermissionAction.NEUTRAL)
                        .add("timeout", DKPermsConfig.FORMAT_DATE_ENDLESSLY)
                        .add("remaining", DKPermsConfig.FORMAT_DATE_ENDLESSLY)
                        .addDescribed("object", object)
                        .addDescribed("group",group)
                        .addDescribed("scope",scope));
                return;
            }

            VariableSet variables = VariableSet.create()
                    .add("type", object.getType().getName().toLowerCase())
                    .addDescribed("object", object)
                    .addDescribed("scope", scope)
                    .addDescribed("entry", entity)
                    .addDescribed("entity", entity)
                    .addDescribed("group",group)
                    .addDescribed("action",entity.getAction())
                    .add("timeout", entity.getTimeoutFormatted())
                    .add("remaining", entity.getRemainingDurationFormatted());
            sender.sendMessage(Messages.OBJECT_GROUP_SHOW,variables);
        }else{
            CommandUtil.sendInvalidSyntax(sender,"permission show","/perms <user/group> <name> perm show <key> [scope]");
        }
    }
}
