/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 05.05.20, 20:26
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission.user;

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectSnapshot;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.player.MinecraftPlayer;

public class InfoCommand extends ObjectCommand<PermissionObject> {

    public InfoCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("info","i"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        PermissionScope fallback = object.getScope();
        if(sender instanceof MinecraftPlayer){
            fallback = ((MinecraftPlayer) sender).getAs(PermissionObject.class).getCurrentSnapshot().getScope();
        }
        PermissionScope scope = CommandUtil.readScope(sender,fallback,arguments,0);

        PermissionObjectSnapshot snapshot;
        if(object.getCurrentSnapshot() != null && object.getCurrentSnapshot().getScope() == scope) snapshot = object.getCurrentSnapshot();
        else snapshot = object.newSnapshot(scope);

        sender.sendMessage(Messages.USER_INFO, VariableSet.create()
                .addDescribed("snapshot",snapshot)
                .addDescribed("user", object));
    }
}
