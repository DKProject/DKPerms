/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 01.02.20, 21:38
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission.object.meta;

import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.message.bml.variable.describer.DescribedHashVariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.common.tree.TreeListBuilder;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.Messages;

import java.util.Arrays;
import java.util.Collection;

public class ListCommand extends ObjectCommand<PermissionObject> {

    public ListCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("list","l"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        PermissionScope scope = CommandUtil.readScope(sender,object,arguments,0);
        if(scope == null) return;

        Collection<ObjectMetaEntry> entries = object.getMeta().getEntries(scope);

        sender.sendMessage(Messages.OBJECT_META_LIST,new DescribedHashVariableSet()
                .add("object",object)
                .add("scope",scope)
                .add("entries",entries));
    }

    private String buildSpaced(int amount){
        char[] result = new char[amount*2];
        Arrays.fill(result,' ');
        return new String(result);
    }
}
