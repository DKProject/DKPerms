/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 19.04.20, 13:31
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class SyncCommand extends BasicCommand {

    public SyncCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("synchronize","s","sync"));
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        DKPerms.getInstance().getObjectManager().sync();
        sender.sendMessage(Messages.SYNCHRONIZED);
    }
}
