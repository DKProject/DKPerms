/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 29.02.20, 13:50
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.rank;

import net.pretronic.dkperms.api.minecraft.player.PermissionPlayer;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.DefinedNotFindable;
import net.pretronic.libraries.command.command.object.MainObjectCommand;
import net.pretronic.libraries.command.command.object.ObjectNotFindable;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.McNative;
import org.mcnative.common.player.MinecraftPlayer;

public class RankCommand extends MainObjectCommand<PermissionObject> implements ObjectNotFindable, DefinedNotFindable<PermissionObject> {

    private final ListCommand listCommand;

    public RankCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
        listCommand = new ListCommand(owner);

        registerCommand(new AddCommand(owner));
        registerCommand(new SetCommand(owner));
        registerCommand(new RemoveCommand(owner));
        registerCommand(listCommand);
    }

    @Override
    public PermissionObject getObject(CommandSender sender,String name) {
        MinecraftPlayer player =  McNative.getInstance().getPlayerManager().getPlayer(name);
        if(player != null) return player.getAs(PermissionPlayer.class).getObject();
        return null;
    }

    @Override
    public void objectNotFound(CommandSender sender, String value, String[] arguments) {
        if(value == null || !sender.hasPermission("dkperms.rank.see.other")){
            if(sender instanceof MinecraftPlayer){
                listCommand.execute(sender,((MinecraftPlayer) sender).getAs(PermissionPlayer.class).getObject(),arguments);
            }else{
                sender.sendMessage(Messages.RANK_HELP);
            }
        }else{
            sender.sendMessage(Messages.USER_NOTFOUND, VariableSet.create()
                    .add("user",value).add("player",value));
        }
    }

    @Override
    public void commandNotFound(CommandSender sender, PermissionObject object, String command, String[] arguments) {
        if(!sender.hasPermission("dkperms.rank.see.other")){
            listCommand.execute(sender,object,arguments);
            return;
        }

        if(object == null || !sender.hasPermission("dkperms.rank.see.other")){
            listCommand.execute(sender,((MinecraftPlayer) sender).getAs(PermissionPlayer.class).getObject(),arguments);
            return;
        }

        if(command == null){
            if(sender instanceof MinecraftPlayer){
                listCommand.execute(sender,object,arguments);
                return;
            }
        }
        sender.sendMessage(Messages.RANK_HELP);
    }
}
