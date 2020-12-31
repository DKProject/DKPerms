/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 15.04.20, 17:33
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission.group;

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.search.ObjectSearchResult;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.player.MinecraftPlayer;

import java.util.List;

public class MemberCommand extends ObjectCommand<PermissionObject> {

    public MemberCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("members","m","list","players"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        PermissionScope scope = object.getScope();
        if(sender instanceof MinecraftPlayer){
            scope = ((MinecraftPlayer) sender).getAs(PermissionObject.class).getCurrentSnapshot().getScope();
        }
        int page = 1;
        for (String argument : arguments) {
            if(GeneralUtil.isNaturalNumber(argument)){
                page = Integer.parseInt(argument);
            }else{
                scope = CommandUtil.readScope(sender,argument);
                if(scope == null) return;
            }
        }
        ObjectSearchResult result = object.getChildren(scope);

        List<PermissionObject> members = result.getPage(page,25);
        sender.sendMessage(Messages.GROUP_LIST_MEMBERS,VariableSet.create()
                .addDescribed("members",members)
                .add("nextPage",page+1)
                .add("previousPage",page-1)
                .add("page",page));
    }
}
