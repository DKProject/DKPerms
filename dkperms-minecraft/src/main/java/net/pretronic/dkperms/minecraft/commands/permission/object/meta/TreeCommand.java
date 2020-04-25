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
import net.pretronic.libraries.message.bml.variable.describer.DescribedHashVariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.common.tree.TreeListBuilder;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.Messages;

import java.util.Arrays;

public class TreeCommand extends ObjectCommand<PermissionObject> {

    public TreeCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("tree","t"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        PermissionScope root = CommandUtil.readScope(sender,object,arguments,0);
        if(root == null) return;
        TreeListBuilder<ObjectMetaEntry> builder = new TreeListBuilder<>(root,object.getMeta().getEntries());

        sender.sendMessage(Messages.OBJECT_META_TREE_HEADER, new DescribedHashVariableSet()
                .add("type",object.getType().getName().toLowerCase())
                .add("object",object));

        builder.setHeaderPrinter(scope1
                -> sender.sendMessage(Messages.OBJECT_META_TREE_SCOPE, new DescribedHashVariableSet()
                .add("scope", scope1)
                .add("spaces",buildSpaced(scope1.getLevel()))));

        builder.setDataPrinter((scope, entry) -> sender.sendMessage(Messages.OBJECT_META_TREE_ENTRY, new DescribedHashVariableSet()
                .add("entry", entry)
                .add("scope", scope)
                .add("spaces",buildSpaced(scope.getLevel()))));
        builder.process();
    }

    private String buildSpaced(int amount){
        if(amount < 1) return "";
        char[] result = new char[amount*2];
        Arrays.fill(result,' ');
        return new String(result);
    }
}
