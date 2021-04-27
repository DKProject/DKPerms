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
import net.pretronic.dkperms.api.logging.record.LogRecordQuery;
import net.pretronic.dkperms.api.logging.record.LogRecordResult;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectSnapshot;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.player.MinecraftPlayer;

import java.util.Arrays;
import java.util.List;

/*

-o Object name/id
-a Action
-e Executor

 */
public class LogCommand extends BasicCommand {

    private final static List<String> TYPES = Arrays.asList("-action","-a","-executor","-e","-type","-t","-owner","-o"
            ,"-object","-obj","-to","-t","-from","-f","-field","fl","-oldValue","-ov","-newValue","-nv");

    public LogCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("log","logs","l"));
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        LogRecordQuery query = DKPerms.getInstance().getAuditLog().search();

        int page = 0;
        if(arguments.length > 0){
            int start = 0;
            if(GeneralUtil.isNaturalNumber(arguments[0])){
                page = Integer.parseInt(arguments[0]);
                start = 1;
            }
            if(arguments.length > start){
                if(parseFilter(sender,start,arguments,query)) return;
            }
        }

        LogRecordResult result = query.execute();
        List<LogRecord> records = result.get(page,25);

        sender.sendMessage(Messages.LOG_LIST, VariableSet.create()
                .add("page",page)
                .add("nextPage",page+1)
                .addDescribed("records",records));
    }

    private boolean parseFilter(CommandSender sender,int start,String[] arguments,LogRecordQuery query){
        String type = null;
        for (int i = start; i < arguments.length; i++) {
            if(type == null){
                type = arguments[i];
                if(!TYPES.contains(type.toLowerCase())){
                    //send invalid message
                    return true;
                }
            }else{

            }
        }
        return false;
    }
}
