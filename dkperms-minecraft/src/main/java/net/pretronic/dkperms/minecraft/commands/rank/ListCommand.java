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

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.player.MinecraftPlayer;

import java.util.List;

public class ListCommand extends ObjectCommand<PermissionObject> {

    public ListCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.newBuilder()
                .name("list").aliases("l")
                .permission("dkperms.rank.see.other")
                .create());
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        PermissionScope fallback = object.getScope();
        if(sender instanceof MinecraftPlayer){
            fallback = ((MinecraftPlayer) sender).getAs(PermissionObject.class).getCurrentSnapshot().getScope();
        }
        PermissionScope scope = CommandUtil.readScope(sender,fallback,arguments,0);
        Graph<PermissionScope> range = DKPerms.getInstance().getScopeManager().newGraph(scope);

        List<PermissionObject> entries = object.newEffectedGroupGraph(range).traverseReversed();

        sender.sendMessage(Messages.RANK_LIST, VariableSet.create()
                .add("type",object.getType().getName().toLowerCase())
                .addDescribed("object",object)
                .addDescribed("scope",scope)
                .add("groups",entries));
    }
}
