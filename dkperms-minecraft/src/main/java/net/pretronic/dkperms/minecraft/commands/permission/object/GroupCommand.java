/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 26.02.20, 19:51
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission.object;

import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.MainObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.minecraft.commands.permission.object.meta.MetaCommand;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.dkperms.minecraft.config.Messages;

public class GroupCommand extends MainObjectCommand<PermissionObject> {

    public GroupCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("group","g"));

        registerCommand(new net.pretronic.dkperms.minecraft.commands.permission.object.group.GroupCommand(owner));
        registerCommand(new MetaCommand(owner));

        setObjectNotFoundHandler((sender, value, strings) -> {
            if(value == null){
                return;//@Todo send help
            }
            sender.sendMessage(Messages.GROUP_NOTFOUND,VariableSet.create().add("group",value));
        });
    }

    @Override
    public PermissionObject getObject(CommandSender sender, String name) {//@Todo check multiples
        return DKPerms.getInstance().getObjectManager().getObjects(name, DKPermsConfig.OBJECT_GROUP_TYPE).getFirst();
    }
}
