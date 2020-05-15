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
import net.pretronic.dkperms.api.object.PermissionGroupTrack;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class TrackDeleteCommand extends ObjectCommand<PermissionGroupTrack> {

    public TrackDeleteCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("delete","d"));
    }

    @Override
    public void execute(CommandSender sender, PermissionGroupTrack track, String[] arguments) {
        DKPerms.getInstance().getObjectManager().deleteTrack(track);
        sender.sendMessage(Messages.TRACK_DELETED, VariableSet.create()
                .addDescribed("track",track));
    }
}
