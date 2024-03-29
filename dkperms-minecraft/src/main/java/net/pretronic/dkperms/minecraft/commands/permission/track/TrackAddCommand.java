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

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectTrack;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class TrackAddCommand extends ObjectCommand<PermissionObjectTrack> {

    public TrackAddCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("add","a"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObjectTrack track, String[] arguments) {
        if(arguments.length < 1){
            CommandUtil.sendInvalidSyntax(sender,"track add","");
            return;
        }

        PermissionObject group = CommandUtil.getGroup(sender,arguments[0]);
        if(group == null) return;
        if(track.contains(group)){
            sender.sendMessage(Messages.TRACK_ALREADY_CONTAINS, VariableSet.create()
                    .addDescribed("track",track)
                    .addDescribed("group",group));
            return;
        }
        track.addAfter(CommandUtil.getExecutor(sender),group);
        sender.sendMessage(Messages.TRACK_ADD, VariableSet.create()
                .addDescribed("track",track)
                .addDescribed("group",group));
    }
}
