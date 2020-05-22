/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 03.05.20, 18:32
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.rank;

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class SetCommand extends ObjectCommand<PermissionObject> {

    public SetCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.newBuilder()
                .name("set").aliases("s")
                .permission("dkperms.rank.change")
                .create());
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        if(arguments.length >= 1){
            PermissionObject group = CommandUtil.getGroup(sender, arguments[0]);
            if(group == null) return;

            if (CommandUtil.canChangeRank(sender, object, group)) return;

            CommandUtil.changeGroup(false,sender,object,group,arguments);
        }else{
            sender.sendMessage(Messages.RANK_HELP);
        }
    }
}
