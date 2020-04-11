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

import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.config.Messages;

public class CommandUtil {

    public static PermissionScope readScope(CommandSender sender, PermissionObject object, String[] arguments, int index){
        if(arguments.length >= index+1) {
            PermissionScope scope = DKPerms.getInstance().getScopeManager().get(arguments[index]);
            if (scope == null) {
                sender.sendMessage(Messages.SCOPE_NOTFOUND, VariableSet.create().add("scope", arguments[index]));
                return null;
            }
            return scope;
        }
        return object.getScope();
    }

    public static void sendInvalidSyntax(CommandSender sender,String command,String usage){
        VariableSet variables = VariableSet.create();
        variables.add("command",command);
        variables.add("usage",usage);
        sender.sendMessage(Messages.COMMAND_INVALID_SYNTAX,variables);
    }

}
