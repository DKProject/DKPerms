/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 29.02.20, 13:49
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class TeamCommand extends BasicCommand {

    private final static long syncDelay = TimeUnit.MINUTES.toMillis(5);

    private final Collection<TeamTree> cached;
    private long lastSynchronised;

    public TeamCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
        cached = new ArrayList<>();
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(cached.isEmpty() || lastSynchronised+syncDelay < System.currentTimeMillis()){
            cached.clear();
            Collection<PermissionObject> result = DKPerms.getInstance().getObjectManager().search()
                    .hasMeta("team", DKPermsConfig.OBJECT_GROUP_SCOPE).directLoading().execute().getAll();
            for (PermissionObject object : result) {
                TeamTree team = new TeamTree(object,new ArrayList<>());
                cached.add(team);
                for (PermissionObject child : object.getChildren(object.getScope())) {
                    if(child.getType().equals(PermissionObjectType.USER_ACCOUNT)){
                        team.members.add(child);
                    }
                }
            }
            lastSynchronised = System.currentTimeMillis();
        }
        Collection<PermissionObject> result = DKPerms.getInstance().getObjectManager().search()
                .hasMeta("team", DKPermsConfig.OBJECT_GROUP_SCOPE).directLoading().execute().getAll();
        sender.sendMessage(Messages.TEAM, VariableSet.create().addDescribed("groups",result));
    }

    public static class TeamTree {

        private final PermissionObject group;
        private final Collection<PermissionObject> members;

        public TeamTree(PermissionObject group, Collection<PermissionObject> members) {
            this.group = group;
            this.members = members;
        }

        public PermissionObject getGroup() {
            return group;
        }

        public Collection<PermissionObject> getMembers() {
            return members;
        }
    }
}
