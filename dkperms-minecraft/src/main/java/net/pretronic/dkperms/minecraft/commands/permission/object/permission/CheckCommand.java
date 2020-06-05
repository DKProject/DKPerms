/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 24.05.20, 14:29
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission.object.permission;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.graph.PermissionGraph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.analyse.PermissionAnalyseResult;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.player.MinecraftPlayer;

public class CheckCommand extends ObjectCommand<PermissionObject> {

    public CheckCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("check","c","test"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        if(arguments.length < 1){
            CommandUtil.sendInvalidSyntax(sender,"permission check","/dkperms <user/group> <name> perm check <permission> [scope]");
            return;
        }

        String permission = arguments[0];
        PermissionScope scope = object.getScope();
        if(arguments.length >= 2){
           scope = CommandUtil.readScope(sender,arguments[0]);
           if(scope == null) return;
        }else if(sender instanceof MinecraftPlayer){
            scope = ((MinecraftPlayer) sender).getAs(PermissionObject.class).getCurrentSnapshot().getScope();
        }

        PermissionGraph graph;
        if(object.getCurrentSnapshot() != null && object.getCurrentSnapshot().getScope().equals(scope)){
            graph = object.getCurrentSnapshot().getPermissionInheritanceGraph();
        }else{
            Graph<PermissionScope> range = DKPerms.getInstance().getScopeManager().newGraph(scope);
            graph = object.newPermissionInheritanceGraph(range);
        }
        PermissionAnalyseResult<PermissionEntity> result = graph.analyze(permission);
        sender.sendMessage(Messages.OBJECT_PERMISSION_CHECK, VariableSet.create()
                .add("permission",permission)
                .addDescribed("object",object)
                .addDescribed("result",result));
    }
}
