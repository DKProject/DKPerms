/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 11.05.20, 19:45
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission.user;

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class PromoteCommand extends ObjectCommand<PermissionObject> {

    public PromoteCommand(ObjectOwner owner) {
        super(owner,CommandConfiguration.name("promote","p"));
    }
    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {

    }
}
