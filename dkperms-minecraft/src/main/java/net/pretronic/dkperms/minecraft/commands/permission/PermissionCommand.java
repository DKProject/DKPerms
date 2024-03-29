/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 19.12.20, 21:58
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission;

import net.pretronic.dkperms.minecraft.commands.AnalyseCommand;
import net.pretronic.dkperms.minecraft.commands.InfoCommand;
import net.pretronic.dkperms.minecraft.commands.MigrationCommand;
import net.pretronic.dkperms.minecraft.commands.SyncCommand;
import net.pretronic.dkperms.minecraft.commands.permission.group.GroupMainCommand;
import net.pretronic.dkperms.minecraft.commands.permission.group.ListCommand;
import net.pretronic.dkperms.minecraft.commands.permission.log.LogCommand;
import net.pretronic.dkperms.minecraft.commands.permission.track.TrackMainCommand;
import net.pretronic.dkperms.minecraft.commands.permission.user.UserMainCommand;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.NoPermissionAble;
import net.pretronic.libraries.command.NotFindable;
import net.pretronic.libraries.command.command.MainCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.player.MinecraftPlayer;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;

public class PermissionCommand extends MainCommand implements NotFindable, NoPermissionAble {

    private final InfoCommand infoCommand;

    public PermissionCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
        infoCommand = new InfoCommand(owner);

        registerCommand(new UserMainCommand(owner));
        registerCommand(new GroupMainCommand(owner));
        registerCommand(new TrackMainCommand(owner));
        registerCommand(new ListCommand(owner));
        registerCommand(new MigrationCommand(owner));
        registerCommand(new SyncCommand(owner));
        registerCommand(new LogCommand(owner));
        registerCommand(new AnalyseCommand(owner));
        registerCommand(infoCommand);
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        if(DKPermsConfig.SECURITY_RESTRICTED_ENABLED && !sender.equals(McNative.getInstance().getConsoleSender())){
            if(sender instanceof MinecraftPlayer){
                OnlineMinecraftPlayer player = ((MinecraftPlayer) sender).getAsOnlinePlayer();
                boolean ok = false;
                for (String restrictedUser : DKPermsConfig.SECURITY_RESTRICTED_USERS) {
                    if(restrictedUser.equalsIgnoreCase(player.getName())
                            || restrictedUser.equalsIgnoreCase(player.getUniqueId().toString())){
                        ok = true;
                        break;
                    }
                }
                if(!ok){
                    sender.sendMessage(Messages.COMMAND_RESTRICTED);
                }
            }
        }
        super.execute(sender, args);
    }

    @Override
    public void commandNotFound(CommandSender sender, String s, String[] strings) {
        sender.sendMessage(Messages.COMMAND_PERMS_HELP);
    }

    @Override
    public void noPermission(CommandSender sender, String s, String s1, String[] arguments) {
        infoCommand.execute(sender,arguments);
    }

}
