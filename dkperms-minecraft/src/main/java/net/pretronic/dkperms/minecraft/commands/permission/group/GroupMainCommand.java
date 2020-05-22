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
import net.pretronic.dkperms.minecraft.commands.permission.object.SetPriorityCommand;
import net.pretronic.dkperms.minecraft.commands.permission.object.meta.MetaCommand;
import net.pretronic.dkperms.minecraft.commands.permission.object.parent.ParentCommand;
import net.pretronic.dkperms.minecraft.commands.permission.object.permission.PermissionCommand;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.DefinedNotFindable;
import net.pretronic.libraries.command.command.object.MainObjectCommand;
import net.pretronic.libraries.command.command.object.ObjectNotFindable;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

import java.util.Arrays;

public class GroupMainCommand extends MainObjectCommand<PermissionObject> implements ObjectNotFindable, DefinedNotFindable<PermissionObject> {

    private final ListCommand listCommand;
    private final CreateCommand createCommand;

    public GroupMainCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("group","g"));

        registerCommand(new DeleteCommand(owner));
        registerCommand(new RenameCommand(owner));

        registerCommand(new ParentCommand(owner));
        registerCommand(new MetaCommand(owner));
        registerCommand(new PermissionCommand(owner));
        registerCommand(new SetPriorityCommand(owner));

        this.listCommand = new ListCommand(owner);
        this.createCommand = new CreateCommand(owner);
    }

    @Override
    public PermissionObject getObject(CommandSender sender, String name) {//@Todo check multiples
        if(name.equalsIgnoreCase("list")) return null;
        return DKPerms.getInstance().getObjectManager().getObject(name,DKPermsConfig.OBJECT_GROUP_SCOPE, DKPermsConfig.OBJECT_GROUP_TYPE);
    }

    @Override
    public void commandNotFound(CommandSender sender, PermissionObject object, String command, String[] args) {
        if(command == null && object != null){
            sender.sendMessage(Messages.OBJECT_GROUP_INFO, VariableSet.create().addDescribed("group", object));
        }else {
            sender.sendMessage(Messages.GROUP_HELP,VariableSet.create().add("command",command));
        }
    }

    @Override
    public void objectNotFound(CommandSender sender, String name, String[] arguments) {
        if(name.equalsIgnoreCase("list") || name.equalsIgnoreCase("l")){
            this.listCommand.execute(sender,arguments);
            return;
        }else if(arguments.length >= 1
                && (arguments[0].equalsIgnoreCase("create")
                || arguments[0].equalsIgnoreCase("c"))){
            this.createCommand.execute(sender,name, Arrays.copyOfRange(arguments, 1, arguments.length));
            return;
        }
        sender.sendMessage(Messages.GROUP_NOTFOUND,VariableSet
                .create().add("group.name",name)
                .add("group",name));
    }
}
