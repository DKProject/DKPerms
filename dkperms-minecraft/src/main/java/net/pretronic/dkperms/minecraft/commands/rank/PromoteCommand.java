/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 11.05.20, 19:45
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.rank;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectTrack;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.commands.CommandUtil;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.dkperms.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import net.pretronic.libraries.utility.map.Pair;

public class PromoteCommand extends ObjectCommand<PermissionObject> {

    public PromoteCommand(ObjectOwner owner) {
        super(owner,CommandConfiguration.newBuilder()
                .name("promote").aliases("p")
                .permission("dkperms.rank.change")
                .create());
    }

    @Override
    public void execute(CommandSender sender, PermissionObject object, String[] arguments) {
        PermissionObjectTrack track = null;
        PermissionScope scope = object.getScope();

        for (String argument : arguments) {
            PermissionObjectTrack track0 = DKPerms.getInstance().getObjectManager().getTrack(argument, DKPermsConfig.OBJECT_TRACK_SCOPE);
            if (track0 == null) {
                scope = CommandUtil.readScope(sender, argument);
                if (scope == null) return;
            } else track = track0;
        }

        if(track == null) {
            track = DKPerms.getInstance().getObjectManager().getPriorityTrack(PermissionObjectType.GROUP,DKPermsConfig.OBJECT_GROUP_SCOPE);
        }

        PermissionObject group = track.getNextGroup(object);
        if (group != null && CommandUtil.canChangeRank(sender, object, group)) return;

        Pair<PermissionObject,PermissionObject> result =  object.promote(CommandUtil.getExecutor(sender),scope,track);
        sender.sendMessage(Messages.RANK_PROMOTED, VariableSet.create()
                .addDescribed("object",object)
                .addDescribed("user",object)
                .addDescribed("oldGroup",result.getKey())
                .addDescribed("newGroup",result.getValue() != null ? result.getValue() : result.getKey()));
    }
}
