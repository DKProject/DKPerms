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

import net.pretronic.dkperms.api.object.PermissionGroupTrack;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class TrackRenameCommand extends ObjectCommand<PermissionGroupTrack> {

    public TrackRenameCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("rename","r"));
    }

    @Override
    public void execute(CommandSender sender, PermissionGroupTrack track, String[] arguments) {
        if(arguments.length < 1){
            CommandUtil.sendInvalidSyntax(sender,"track rename","");
            return;
        }
        String oldName = track.getName();
        track.setName(arguments[0]);
        sender.sendMessage(Messages.TRACK_RENAMED, VariableSet.create()
                .addDescribed("track",track)
                .add("oldName",oldName)
                .add("newName",arguments[0]));
    }
}
