/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 13.05.20, 19:33
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission.track;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObjectTrack;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class TrackCreateCommand extends ObjectCommand<String> {

    public TrackCreateCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("create","c"));
    }

    @Override
    public void execute(CommandSender sender,String name, String[] arguments) {
        if(arguments.length < 1){
            CommandUtil.sendInvalidSyntax(sender,"track create","");
            return;
        }

        if(DKPerms.getInstance().getObjectManager().getTrack(name, DKPermsConfig.OBJECT_TRACK_SCOPE) != null){
            sender.sendMessage(Messages.TRACK_ALREADY_EXISTS,VariableSet.create().add("name",name));
            return;
        }

        PermissionObjectTrack track = DKPerms.getInstance().getObjectManager().createTrack(CommandUtil.getExecutor(sender),name, DKPermsConfig.OBJECT_TRACK_SCOPE);
        sender.sendMessage(Messages.TRACK_CREATED, VariableSet.create()
                .addDescribed("track",track));
    }
}
