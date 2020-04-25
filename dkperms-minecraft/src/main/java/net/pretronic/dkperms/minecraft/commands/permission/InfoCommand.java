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

import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class InfoCommand extends BasicCommand {

    public InfoCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("info","i","information","version","v"));
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {

    }
}
