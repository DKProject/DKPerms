/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 24.02.20, 20:47
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission.object.group;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.search.ObjectSearchResult;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

import java.util.Collection;

public class RemoveCommand extends ObjectCommand<PermissionObject> {

    public RemoveCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("remove","r"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        if(arguments.length >= 1){
            String name = arguments[0];

            ObjectSearchResult result = DKPerms.getInstance().getObjectManager().getObjects(name, DKPermsConfig.OBJECT_GROUP_TYPE);
            Collection<PermissionObject> groups = result.getAll();

            if(groups.isEmpty()){
                sender.sendMessage(Messages.GROUP_NOTFOUND, VariableSet.create().add("group",name));
                return;
            }else if(groups.size() > 1){
                return;
            }
            PermissionObject group = result.iterator().next();

            PermissionScope scope = CommandUtil.readScope(sender,object,arguments,1);
            if(scope == null) return;

            object.removeGroup(null,scope,group);

            VariableSet variables = VariableSet.create()
                    .add("type",object.getType().getName().toLowerCase())
                    .addDescribed("object",object)
                    .addDescribed("group",group)
                    .add("action", PermissionAction.NEUTRAL)
                    .add("timeout",DKPermsConfig.FORMAT_DATE_ENDLESSLY)
                    .add("remaining",DKPermsConfig.FORMAT_DATE_ENDLESSLY)
                    .add("scope",scope.getPath());
            sender.sendMessage(Messages.OBJECT_GROUP_REMOVE,variables);
        }else{
            CommandUtil.sendInvalidSyntax(sender,"group set","/perms <user/group> <object> meta set <key> <value> [scope]");
        }
    }
}
