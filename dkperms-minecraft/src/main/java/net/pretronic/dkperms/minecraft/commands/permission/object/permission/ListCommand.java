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
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.describer.DescribedHashVariableSet;
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

        Collection<PermissionEntity> entries = object.getPermissions(scope);

        sender.sendMessage(Messages.OBJECT_PERMISSION_LIST,new DescribedHashVariableSet()
                .add("type",object.getType().getName().toLowerCase())
                .add("object",object)
                .add("scope",scope)
                .add("entities",entries)
                .add("entries",entries));
    }
}
