/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 05.05.20, 20:26
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission.log;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.logging.record.LogRecord;
import net.pretronic.dkperms.api.logging.record.LogRecordResult;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectSnapshot;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.player.MinecraftPlayer;

import java.util.List;

/*

-o Object name/id
-a Action
-e Executor

 */
public class LogListCommand extends ObjectCommand<PermissionObject> {

    public LogListCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("log","logs","l"));
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        LogRecordResult result = DKPerms.getInstance().getAuditLog().search().execute();
        List<LogRecord> records = result.get(0,25);

        sender.sendMessage(Messages.LOG_LIST, VariableSet.create()
                .add("page",1)
                .add("nextPage",2)
                .addDescribed("records",records));
    }
}
