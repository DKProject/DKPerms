/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 01.02.20, 21:38
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission.object.parent;

import net.pretronic.dkperms.api.entity.ParentEntity;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.common.tree.TreeListBuilder;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

import java.util.Arrays;

public class TreeCommand extends ObjectCommand<PermissionObject> {

    public TreeCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("tree","t"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        PermissionScope root = CommandUtil.readScope(sender,object,arguments,0);
        if(root == null) return;
        TreeListBuilder<ParentEntity> builder = new TreeListBuilder<>(root,object.getAllParents());

        sender.sendMessage(Messages.OBJECT_GROUP_TREE_HEADER, VariableSet.create()
                .add("type",object.getType().getDisplayName().toLowerCase())
                .addDescribed("object",object));

        builder.setHeaderPrinter(scope1
                -> sender.sendMessage(Messages.OBJECT_GROUP_TREE_SCOPE, VariableSet.create()
                .addDescribed("scope", scope1)
                .add("spaces",buildSpaced(scope1.getLevel()))));

        builder.setDataPrinter((scope, entry) -> sender.sendMessage(Messages.OBJECT_GROUP_TREE_ENTRY,VariableSet.create()
                .addDescribed("entry", entry)
                .addDescribed("scope", scope)
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
