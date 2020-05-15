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
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

import java.util.Collection;

public class TrackListCommand extends BasicCommand {

    public TrackListCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("list","l"));
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        Collection<PermissionGroupTrack> tracks = DKPerms.getInstance().getObjectManager().getTracks(DKPermsConfig.OBJECT_TRACK_SCOPE);
        sender.sendMessage(Messages.TRACK_LIST, VariableSet.create()
                .addDescribed("tracks",tracks));
    }
}
