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
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.NotFindable;
import net.pretronic.libraries.command.command.Command;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.MainObjectCommand;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.command.object.ObjectNotFindable;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

import java.util.Arrays;

public class TrackMainCommand extends MainObjectCommand<PermissionObjectTrack> implements NotFindable, ObjectNotFindable {

    private final Command listCommand;
    private final ObjectCommand<String> createCommand;

    public TrackMainCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("track","t"));

        registerCommand(new TrackDeleteCommand(owner));
        registerCommand(new TrackRenameCommand(owner));
        registerCommand(new TrackAddCommand(owner));
        registerCommand(new TrackInsertCommand(owner));
        registerCommand(new TrackRemoveCommand(owner));
        registerCommand(new TrackClearCommand(owner));

        listCommand = new TrackListCommand(owner);
        createCommand = new TrackCreateCommand(owner);
    }

    @Override
    public PermissionObjectTrack getObject(CommandSender commandSender, String name) {
        if(name.equalsIgnoreCase("list")) return null;
        return DKPerms.getInstance().getObjectManager().getTrack(name, DKPermsConfig.OBJECT_TRACK_SCOPE);
    }

    @Override
    public void commandNotFound(CommandSender sender, String s, String[] strings) {
        sender.sendMessage(Messages.TRACK_HELP);
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
        }else{
            sender.sendMessage(Messages.TRACK_NOT_FOUND, VariableSet.create().add("name",name));
        }
    }
}
