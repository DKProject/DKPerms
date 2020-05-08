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

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class ShowCommand extends ObjectCommand<PermissionObject> {

    public ShowCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("show","get","g"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        if(arguments.length >= 1){
            String key = arguments[0];

            PermissionScope scope = CommandUtil.readScope(sender,object,arguments,1);
            if(scope == null) return;

            ObjectMetaEntry entry = object.getMeta().getHighest(key,scope);
            //@Todo Commands do currently only support one entry, multiple entries should be implemented in future

            VariableSet variables = VariableSet.create();
            variables.add("type",object.getType().getName().toLowerCase());
            variables.addDescribed("object",object);
            variables.add("key",key);
            variables.add("value",entry != null ? entry.getValue() : "Undefined");
            variables.addDescribed("scope",scope);
            sender.sendMessage(Messages.OBJECT_META_SHOW,variables);
        }else{
            CommandUtil.sendInvalidSyntax(sender,"meta show","/perms <user/group> <name> meta show <key> [scope]");
        }
    }
}
