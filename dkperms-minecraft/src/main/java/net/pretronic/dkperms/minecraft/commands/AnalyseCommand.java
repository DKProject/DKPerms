/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 15.12.20, 18:39
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.permission.analyse.PermissionRequest;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.Completable;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.StringUtil;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class AnalyseCommand extends BasicCommand implements Completable {

    private final List<String> COMMANDS = Arrays.asList("on","off","join","leave");

    public AnalyseCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.newBuilder()
                .name("analyse")
                .aliases("a")
                .create());
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length == 0){
            sender.sendMessage(Messages.ANALYSE_HELP);
            return;
        }
        String argument = arguments[0];
        if(StringUtil.equalsOne(argument,"on","enable")){
            DKPerms.getInstance().getAnalyser().enable();
            sender.sendMessage(Messages.ANALYSE_ENABLED);
        }else if(StringUtil.equalsOne(argument,"off","disable")){
            DKPerms.getInstance().getAnalyser().disable();
            sender.sendMessage(Messages.ANALYSE_DISABLED);
        }else if(argument.equalsIgnoreCase("join")){
            if(!DKPerms.getInstance().getAnalyser().hasListener(sender)){
                DKPerms.getInstance().getAnalyser().addListener(new PlayerRequestListener((OnlineMinecraftPlayer)sender));
            }
            sender.sendMessage(Messages.ANALYSE_JOINED);
        }else if(argument.equalsIgnoreCase("leave")){
            DKPerms.getInstance().getAnalyser().removeListener(sender);
            sender.sendMessage(Messages.ANALYSE_LEAVED);
        }else{
            sender.sendMessage(Messages.ANALYSE_HELP);
        }
    }

    @Override
    public Collection<String> complete(CommandSender sender, String[] args) {
        if(args.length == 0) return COMMANDS;
        else if(args.length == 1) return Iterators.filter(COMMANDS, command -> command.startsWith(args[0].toLowerCase()));
        return Collections.emptyList();
    }

    private static class PlayerRequestListener implements Consumer<PermissionRequest> {

        private final OnlineMinecraftPlayer player;

        public PlayerRequestListener(OnlineMinecraftPlayer player) {
            this.player = player;
        }

        @Override
        public void accept(PermissionRequest request) {

        }

        @Override
        public boolean equals(Object obj) {
            return player.equals(obj) || super.equals(obj);
        }
    }
}
