/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 13.05.20, 19:34
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission.track;

import net.pretronic.dkperms.api.object.PermissionObjectTrack;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class TrackClearCommand extends ObjectCommand<PermissionObjectTrack> {

    public TrackClearCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("clear","cls","c"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObjectTrack track, String[] arguments) {
        track.clearGroups();
        sender.sendMessage(Messages.TRACK_CLEAR, VariableSet.create().addDescribed("track",track));
    }
}
