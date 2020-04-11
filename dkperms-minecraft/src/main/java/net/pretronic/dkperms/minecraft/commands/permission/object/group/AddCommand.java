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

import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.search.ObjectSearchResult;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.dkperms.minecraft.config.Messages;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

//@Todo multiply add time with existing group
public class AddCommand extends ObjectCommand<PermissionObject> {

    public AddCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("add","a"));
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
                //@Todo more groups ask for group
                return;
            }
            PermissionObject group = result.iterator().next();
            addGroup(sender,object,group,arguments);
        }else{
            VariableSet variables = VariableSet.create();
            variables.add("command",getConfiguration().getName());
            variables.add("usage","/perms <user/group> <object> group add <name> {action} {time} {unit} {scope}");
            sender.sendMessage(Messages.COMMAND_INVALID_SYNTAX,variables);
        }
    }

    private void addGroup(CommandSender sender, PermissionObject object,PermissionObject group,String[] arguments){
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

        object.addGroup(null,scope,group,action,time,unit);
        sender.sendMessage(Messages.OBJECT_GROUP_ADD, VariableSet.create().add("scope",scope)
                .add("group",group).add("action",action).add("time",time).add("unit",unit));
    }
}
