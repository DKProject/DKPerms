/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 01.02.20, 21:38
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission.object.group;

import net.pretronic.dkperms.api.entity.PermissionGroupEntity;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

import java.util.Collection;

public class ListCommand extends ObjectCommand<PermissionObject> {

    public ListCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("list","l"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        PermissionScope scope = CommandUtil.readScope(sender,object,arguments,0);
        if(scope == null) return;

        Collection<PermissionGroupEntity> entries = object.getGroups(scope);

        sender.sendMessage(Messages.OBJECT_GROUP_LIST, VariableSet.create()
                .add("type",object.getType().getName().toLowerCase())
                .addDescribed("object",object)
                .addDescribed("scope",scope)
                .add("entries",entries));
    }
}
