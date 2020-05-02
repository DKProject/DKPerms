/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 26.02.20, 19:51
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.entity.PermissionGroupEntity;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.search.ObjectSearchResult;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.GeneralUtil;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

//world=test;server=server-1;serverGroup=;
public class CommandUtil {

    public static PermissionScope readScope(CommandSender sender, PermissionObject object, String[] arguments, int index){
        return readScope(sender, object.getScope(), arguments, index);
    }

    public static PermissionScope readScope(CommandSender sender, PermissionScope fallback, String[] arguments, int index){
        if(arguments.length >= index+1) {
            try{
                PermissionScope namespace = DKPerms.getInstance().getScopeManager().getNamespace(DKPermsConfig.SCOPE_NAMESPACE);
                PermissionScope scope = DKPerms.getInstance().getScopeManager().get(namespace,arguments[index]);
                if (scope == null) {
                    sender.sendMessage(Messages.SCOPE_NOTFOUND, VariableSet.create().add("scope", arguments[index]));
                    return null;
                }
                return scope;
            }catch (IllegalArgumentException exception){
                sender.sendMessage(Messages.SCOPE_INVALID, VariableSet.create().add("scope", arguments[index]));
            }
        }
        return fallback;
    }

    public static PermissionObject getGroup(CommandSender sender, String argument) {
        ObjectSearchResult result = DKPerms.getInstance().getObjectManager().getObjects(argument, DKPermsConfig.OBJECT_GROUP_TYPE);
        Collection<PermissionObject> groups = result.getAll();

        if(groups.isEmpty()){
            sender.sendMessage(Messages.GROUP_NOTFOUND, VariableSet.create()
                    .add("group", argument).add("group.name", argument));
            return null;
        }else if(groups.size() > 1){
            //@Todo more groups ask for group
            return null;
        }
        return result.iterator().next();
    }

    public static void changeGroup(boolean set,CommandSender sender, PermissionObject object,PermissionObject group,String[] arguments){
        PermissionAction action = PermissionAction.ALLOW;
        long time = -1;
        TimeUnit unit = TimeUnit.DAYS;
        PermissionScope scope = group.getScope();

        for (int i = 1; i < arguments.length; i++) {
            String argument = arguments[i];
            if(GeneralUtil.isNaturalNumber(argument)){
                time = Long.parseLong(argument);
            }else{
                PermissionAction action0 = GeneralUtil.valueOfEnumOrNull(PermissionAction.class,argument.toUpperCase());
                if(action0 == null){
                    TimeUnit unit0 = GeneralUtil.valueOfEnumOrNull(TimeUnit.class,argument.toUpperCase());//@Todo add alternative time unit options
                    if(unit0 == null){
                        try{
                            scope = DKPerms.getInstance().getScopeManager().get(argument);
                            if(scope == null){
                                sender.sendMessage(Messages.SCOPE_NOTFOUND, VariableSet.create().add("scope",argument));
                                return;
                            }
                        }catch (IllegalArgumentException exception){
                            sender.sendMessage(Messages.SCOPE_INVALID, VariableSet.create().add("scope",argument));
                            return;
                        }
                    }else unit = unit0;
                }else action = action0;
            }
        }

        PermissionGroupEntity entity;
        if(set) entity = object.setGroup(null,scope,group,action,time,unit);
        else entity = object.addGroup(null,scope,group,action,time,unit);

        sender.sendMessage(set ? Messages.OBJECT_GROUP_SET: Messages.OBJECT_GROUP_ADD,
                VariableSet.create()
                        .addDescribed("object",object)
                        .addDescribed("scope",scope)
                        .addDescribed("entity",entity)
                        .addDescribed("group",group)
                        .add("action",action)
                        .add("timeout",entity.getTimeoutFormatted())
                        .add("remaining",entity.getRemainingDuration())
                        .add("time",time)
                        .add("unit",unit));
    }

    public static void sendInvalidSyntax(CommandSender sender,String command,String usage){
        VariableSet variables = VariableSet.create();
        variables.add("command",command);
        variables.add("usage",usage);
        sender.sendMessage(Messages.COMMAND_INVALID_SYNTAX,variables);
    }

}
