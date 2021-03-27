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
import net.pretronic.dkperms.api.entity.ParentEntity;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.object.search.ObjectSearchResult;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.duration.DurationProcessor;
import org.mcnative.runtime.api.player.MinecraftPlayer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;

public class CommandUtil {

    public static PermissionScope readScope(CommandSender sender, PermissionObject object, String[] arguments, int index){
        return readScope(sender, object.getScope(), arguments, index);
    }

    public static PermissionScope readScope(CommandSender sender, PermissionScope fallback, String[] arguments, int index){
        if(arguments.length > index) return readScope(sender,arguments[index]);
        return fallback;
    }

    public static PermissionScope readScope(CommandSender sender, String argument) {
        try{
            PermissionScope namespace = DKPerms.getInstance().getScopeManager().getNamespace(DKPermsConfig.SCOPE_NAMESPACE);
            PermissionScope scope = DKPerms.getInstance().getScopeManager().get(namespace,argument);
            if (scope == null) {
                sender.sendMessage(Messages.SCOPE_NOTFOUND, VariableSet.create().add("scope", argument));
                return null;
            }
            return scope;
        }catch (IllegalArgumentException exception){
            sender.sendMessage(Messages.SCOPE_INVALID, VariableSet.create().add("scope", argument));
        }
        return null;
    }

    public static PermissionObject getGroup(CommandSender sender, String argument) {
        ObjectSearchResult result = DKPerms.getInstance().getObjectManager().getObjects(argument,  PermissionObjectType.GROUP);
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
        PermissionScope scope = group.getScope();
        Duration duration = Duration.of(-1, ChronoUnit.SECONDS);
        UpdateModifier modifier = UpdateModifier.REPLACE;

        for (int i = 1; i < arguments.length; i++) {
            String argument = arguments[i];
            UpdateModifier modifier0 = UpdateModifier.parse(argument);
            if(modifier0 == null){
                try{
                    duration = DurationProcessor.getStandard().parse(argument);
                }catch (IllegalArgumentException ignored){
                    scope = CommandUtil.readScope(sender, argument);
                    if (scope == null) return;
                }
            }else{
                modifier = modifier0;
            }
        }

        ParentEntity entity = object.getParent(scope,group);

        if(entity != null){
            if(modifier == UpdateModifier.FAIL) return; //@Todo send message
            else duration = modifier.take(entity.getRemainingDuration(),duration);
        }

        if(entity == null || set){
            if(set) entity = object.setParent(CommandUtil.getExecutor(sender),scope,group,action,duration);
            else entity = object.addParent(CommandUtil.getExecutor(sender),scope,group,action,duration);
        }else{
            entity.update(CommandUtil.getExecutor(sender),action,scope,duration);
        }

        sender.sendMessage(set ? Messages.OBJECT_GROUP_SET: Messages.OBJECT_GROUP_ADD,
                VariableSet.create()
                        .addDescribed("object",object)
                        .addDescribed("scope",scope)
                        .addDescribed("entity",entity)
                        .addDescribed("group",group)
                        .add("action",action)
                        .add("timeout",entity.getTimeoutFormatted())
                        .add("remaining",entity.getRemainingDurationFormatted()));
    }

    public static void removeGroup(CommandSender sender, PermissionObject object, String[] arguments, PermissionObject group) {
        PermissionScope scope = CommandUtil.readScope(sender,object,arguments,1);
        if(scope == null) return;

        object.removeParent(CommandUtil.getExecutor(sender),scope,group);

        VariableSet variables = VariableSet.create()
                .add("type",object.getType().getDisplayName().toLowerCase())
                .addDescribed("object",object)
                .addDescribed("group",group)
                .add("action", PermissionAction.NEUTRAL)
                .add("timeout", DKPermsConfig.FORMAT_DATE_ENDLESSLY)
                .add("remaining",DKPermsConfig.FORMAT_DATE_ENDLESSLY)
                .add("scope",scope.getPath());
        sender.sendMessage(Messages.OBJECT_GROUP_REMOVE,variables);
    }

    public static void sendInvalidSyntax(CommandSender sender,String command,String usage){
        VariableSet variables = VariableSet.create();
        variables.add("command",command);
        variables.add("usage",usage);
        sender.sendMessage(Messages.COMMAND_INVALID_SYNTAX,variables);
    }

    public static boolean canChangeRank(CommandSender sender, PermissionObject object, PermissionObject group) {
        if(!sender.hasPermission("dkperms.rank.change.all")){
            if(!sender.hasPermission("dkperms.rank.change.")){
                sender.sendMessage(Messages.RANK_CHANGE_NO_PERMISSION_FOR_RANK, VariableSet.create()
                        .addDescribed("group",group));
                return true;
            }
            if(!sender.hasPermission("dkperms.rank.change.higher") && sender instanceof MinecraftPlayer){
                PermissionObject senderObject = ((MinecraftPlayer) sender).getAs(PermissionObject.class);
                PermissionObject senderGroup = senderObject.getHighestParent();
                PermissionObject assignerGroup = object.getHighestParent();

                if(senderGroup == null ||
                        (Math.max(senderObject.getPriority(),senderGroup.getPriority())
                                <= Math.max(object.getPriority(),assignerGroup != null ? assignerGroup.getPriority() : 0))){
                    sender.sendMessage(Messages.RANK_CHANGE_NO_PERMISSION_FOR_PLAYER, VariableSet.create()
                            .addDescribed("object",object)
                            .addDescribed("user",object));
                }

                return true;
            }
        }
        return false;
    }

    public static PermissionObject getExecutor(CommandSender sender){
        if(sender instanceof MinecraftPlayer){
            return ((MinecraftPlayer) sender).getAs(PermissionObject.class);
        }else {
            return DKPerms.getInstance().getObjectManager().getSuperAdministrator();
        }
    }

    public static Collection<String> completeGroups(CommandSender sender, String[] args){
        if(args.length == 0){
            return Iterators.map(DKPerms.getInstance().getObjectManager().getObjects(PermissionObjectType.GROUP, DKPermsConfig.OBJECT_GROUP_SCOPE)
                    , PermissionObject::getName);
        }else if(args.length == 1){
            return Iterators.map(DKPerms.getInstance().getObjectManager().getObjects(PermissionObjectType.GROUP, DKPermsConfig.OBJECT_GROUP_SCOPE)
                    , PermissionObject::getName
                    , object -> object.getName().toLowerCase().startsWith(args[0]));
        }else return Collections.emptyList();
    }

}
