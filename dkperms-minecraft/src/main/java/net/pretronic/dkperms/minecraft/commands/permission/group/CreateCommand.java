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

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class CreateCommand extends ObjectCommand<String> {

    public CreateCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("create", "c"));
    }

    @Override
    public void execute(CommandSender sender, String name, String[] arguments) {
        PermissionScope scope = CommandUtil.readScope(sender, DKPermsConfig.OBJECT_GROUP_SCOPE,arguments,0);
        if(scope == null) return;
        PermissionObject object = DKPerms.getInstance().getObjectManager().getObject(name,scope, DKPermsConfig.OBJECT_GROUP_TYPE);
        if(object != null){
            sender.sendMessage(Messages.GROUP_ALREADY_EXISTS, VariableSet.create()
                    .add("name",name));
            return;
        }

        object = DKPerms.getInstance().getObjectManager().createObject(scope,DKPermsConfig.OBJECT_GROUP_TYPE,name);

        sender.sendMessage(Messages.GROUP_CREATED, VariableSet.create()
                .addDescribed("group",object)
                .addDescribed("object",object));
    }
}
