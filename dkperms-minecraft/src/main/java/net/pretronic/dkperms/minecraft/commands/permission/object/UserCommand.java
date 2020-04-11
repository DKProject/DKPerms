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

import net.pretronic.dkperms.api.minecraft.player.PermissionPlayer;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.minecraft.commands.permission.object.group.GroupCommand;
import net.pretronic.dkperms.minecraft.commands.permission.object.meta.MetaCommand;
import net.pretronic.dkperms.minecraft.commands.permission.object.permission.PermissionCommand;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.DefinedNotFindable;
import net.pretronic.libraries.command.command.object.MainObjectCommand;
import net.pretronic.libraries.command.command.object.ObjectNotFindable;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.message.bml.variable.describer.DescribedHashVariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.McNative;
import org.mcnative.common.player.MinecraftPlayer;

public class UserCommand extends MainObjectCommand<PermissionObject> implements ObjectNotFindable, DefinedNotFindable<PermissionObject> {

    public UserCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("user","u","player","p"));

        registerCommand(new GroupCommand(owner));
        registerCommand(new MetaCommand(owner));
        registerCommand(new PermissionCommand(owner));
    }

    @Override
    public PermissionObject getObject(CommandSender sender,String name) {
        MinecraftPlayer player =  McNative.getInstance().getPlayerManager().getPlayer(name);
        if(player != null) return player.getAs(PermissionPlayer.class).getObject();
        return null;
    }

    @Override
    public void objectNotFound(CommandSender sender, String value, String[] strings) {
        if(value == null){
            System.out.println("VALUE NULL");
            return;
        }
        sender.sendMessage(Messages.USER_NOTFOUND,VariableSet.create().add("player",value));
    }

    @Override
    public void commandNotFound(CommandSender sender, PermissionObject object, String command, String[] strings) {
        if(command == null | object != null) {
            sender.sendMessage(Messages.OBJECT_USER_INFO, new DescribedHashVariableSet().add("user", object));
        }else{
            sender.sendMessage("HRLP");
        }
    }
}
