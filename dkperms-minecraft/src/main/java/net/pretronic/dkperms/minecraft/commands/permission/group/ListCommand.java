/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 16.04.20, 18:33
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission.group;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.search.ObjectSearchResult;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class ListCommand extends BasicCommand {

    public ListCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("list","l","groups"));
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        PermissionScope scope = CommandUtil.readScope(sender, DKPermsConfig.OBJECT_GROUP_SCOPE,arguments,0);
        if(scope == null) return;

        ObjectSearchResult result = DKPerms.getInstance().getObjectManager()
                .getObjects(DKPermsConfig.OBJECT_GROUP_TYPE,DKPermsConfig.OBJECT_GROUP_SCOPE);

        sender.sendMessage(Messages.GROUP_LIST, VariableSet.create()
                .add("groups",result.getAll())
                .addDescribed("scope",scope));
    }
}
