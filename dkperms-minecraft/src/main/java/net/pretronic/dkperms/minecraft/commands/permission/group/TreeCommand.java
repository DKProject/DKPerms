/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 15.04.20, 17:34
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission.group;

import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class TreeCommand extends BasicCommand {

    public TreeCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("tree","t"));
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        /*"@Todo no scope based object list available
        PermissionScope root = CommandUtil.readScope(sender, DKPermsConfig.OBJECT_GROUP_SCOPE,arguments,0);
        if(root == null) return;

        ObjectSearchResult result = DKPerms.getInstance().getObjectManager()
                .getObjects(DKPermsConfig.OBJECT_GROUP_TYPE);

        sender.sendMessage(Messages.GROUP_LIST,new DescribedHashVariableSet().add("objects",result.getAll()));

        sender.sendMessage(Messages.OBJECT_META_TREE_HEADER, new DescribedHashVariableSet()
                .add("object",object));

        TreeListBuilder<PermissionObject> builder = new TreeListBuilder<>(root,result.get);

        builder.setHeaderPrinter(scope1
                -> sender.sendMessage(Messages.OBJECT_META_TREE_SCOPE, new DescribedHashVariableSet()
                .add("scope", scope1)
                .add("spaces",buildSpaced(scope1.getLevel()))));

        builder.setDataPrinter((scope, entry) -> sender.sendMessage(Messages.OBJECT_META_TREE_ENTRY, new DescribedHashVariableSet()
                .add("entry", entry)
                .add("scope", scope)
                .add("spaces",buildSpaced(scope.getLevel()))));
        builder.process();)
        }
         */

    }
}
