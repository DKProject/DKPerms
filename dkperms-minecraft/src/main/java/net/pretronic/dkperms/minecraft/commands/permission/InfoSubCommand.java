/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 07.04.20, 15:06
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission;

import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class InfoSubCommand extends BasicCommand {

    public InfoSubCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("info","i","information","version","v"));
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {

    }
}
