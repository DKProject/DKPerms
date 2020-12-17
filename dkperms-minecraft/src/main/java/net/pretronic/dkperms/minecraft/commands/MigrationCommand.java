/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 15.12.20, 18:39
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.migration.PermissionMigration;
import net.pretronic.dkperms.common.object.DefaultPermissionObject;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.synchronisation.SynchronisationCaller;
import net.pretronic.libraries.synchronisation.UnconnectedSynchronisationCaller;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.McNative;

public class MigrationCommand extends BasicCommand {

    private static SynchronisationCaller<Integer> currentCaller;

    public MigrationCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("migrate","migration","m"));
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(sender.equals(McNative.getInstance().getConsoleSender())){
            if(arguments.length == 1){
                PermissionMigration migration = DKPerms.getInstance().getMigrationAssistant().getMigration(arguments[0]);
                if(migration == null){
                    sender.sendMessage("[DKPerms] Migration "+arguments[0]+" not found");
                }else{
                    if(!migration.isAvailable()){
                        sender.sendMessage("[DKPerms] Migration "+migration.getName()+" is currently not available");
                        return;
                    }
                    sender.sendMessage("[DKPerms] Starting migration of "+migration.getName());
                    sender.sendMessage("[DKPerms] This may take a while...");
                    disableSync();
                    try{
                        boolean result = migration.migrate();
                        if(result){
                            sender.sendMessage("[DKPerms] Migration was successfully");
                        }else{
                            sender.sendMessage("[DKPerms] Migration failed");
                        }
                    }catch (Exception exception){
                        exception.printStackTrace();
                        sender.sendMessage("[DKPerms] Migration failed ("+exception.getMessage()+")");
                    }
                    enableSync();
                }
            }else{
                sender.sendMessage("[DKPerms] Available migrations:");
                for (PermissionMigration migration : DKPerms.getInstance().getMigrationAssistant().getMigrations()) {
                    if(migration.isAvailable()){
                        sender.sendMessage(" - "+migration.getName());
                    }else{
                        sender.sendMessage(" - "+migration.getName()+" (Not Available)");
                    }
                }
            }
        }else{
            sender.sendMessage(Messages.SENDER_ONLY_CONSOLE);
        }
    }

    private void disableSync(){
        currentCaller = DefaultPermissionObject.SYNCHRONISATION_CALLER;
        DefaultPermissionObject.SYNCHRONISATION_CALLER = new UnconnectedSynchronisationCaller<>(true);
    }

    private void enableSync(){
        DefaultPermissionObject.SYNCHRONISATION_CALLER = currentCaller;
    }
}
