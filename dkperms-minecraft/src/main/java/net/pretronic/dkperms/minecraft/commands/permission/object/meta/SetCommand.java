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

import net.pretronic.dkperms.api.entity.Entity;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.duration.DurationProcessor;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

import java.time.Duration;

public class SetCommand extends ObjectCommand<PermissionObject> {

    public SetCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("set","s"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        if(arguments.length >= 2){
            String key = arguments[0];
            String value = arguments[1].replace("__"," ");

            PermissionScope scope = object.getScope();
            Duration duration = Entity.PERMANENTLY;

            for (int i = 1; i < arguments.length; i++) {
                String argument = arguments[i];
                try{
                    duration = DurationProcessor.getStandard().parse(argument);
                }catch (IllegalArgumentException ignored){
                    scope = CommandUtil.readScope(sender, argument);
                    if (scope == null) return;
                }
            }

            //@Todo Commands do currently only support one entry, multiple entries should be implemented in future
            ObjectMetaEntry entry = object.getMeta().set(null,key,value,0,scope,duration);

            VariableSet variables = VariableSet.create();
            variables.addDescribed("entry",entry);
            variables.addDescribed("type",object.getType().getName().toLowerCase());
            variables.addDescribed("object",object);
            variables.add("key",key);
            variables.add("value",value);
            variables.add("scope",scope);
            sender.sendMessage(Messages.OBJECT_META_SET,variables);
        }else{
            CommandUtil.sendInvalidSyntax(sender,"meta set","/perms <user/group> <name> meta set <key> <value> [scope]");
        }
    }
}
