/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 30.05.20, 15:07
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.migration.luckperms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.Context;
import net.luckperms.api.context.DefaultContextKeys;
import net.luckperms.api.model.PermissionHolder;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.*;
import net.luckperms.api.track.Track;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.migration.MigrationExecutor;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectTrack;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.dkperms.minecraft.migration.MigrationUtil;
import org.mcnative.common.McNative;
import org.mcnative.common.player.MinecraftPlayer;
import org.mcnative.common.player.data.PlayerDataProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class LuckPermsMigrationExecutor implements MigrationExecutor {

    private final PermissionObject admin;
    private final Map<String,PermissionObject> migratedGroups;

    public LuckPermsMigrationExecutor() {
        admin = DKPerms.getInstance().getObjectManager().getSuperAdministrator();
        migratedGroups = new HashMap<>();
    }

    @Override
    public boolean migrate() throws Exception {
        LuckPerms luckPerms = LuckPermsProvider.get();

        luckPerms.getGroupManager().loadAllGroups().get(30, TimeUnit.SECONDS);
        for (Group group : luckPerms.getGroupManager().getLoadedGroups()) {
            PermissionObject object = MigrationUtil.createOrGetGroup(group.getName());
            object.setPriority(admin,group.getWeight().orElse(0));
            migratedGroups.put(object.getName(),object);
        }
        for (Group group : luckPerms.getGroupManager().getLoadedGroups()) {
            migrateHolder(group,migratedGroups.get(group.getName()));
        }

        for (UUID uniqueId : luckPerms.getUserManager().getUniqueUsers().get(2, TimeUnit.MINUTES)) {
            User user = luckPerms.getUserManager().loadUser(uniqueId).get(30,TimeUnit.SECONDS);

            MinecraftPlayer minecraftPlayer = McNative.getInstance().getPlayerManager().getPlayer(user.getUniqueId());
            if(minecraftPlayer == null){
                McNative.getInstance().getRegistry().getService(PlayerDataProvider.class)
                        .createPlayerData(user.getUsername(),user.getUniqueId(),-1,-1,-1,null);
            }

            PermissionObject object = DKPerms.getInstance().getObjectManager().getObjectByAssignment(user.getUniqueId());
            if(object == null){
                object = DKPerms.getInstance().getObjectManager()
                        .createObject(DKPermsConfig.OBJECT_PLAYER_SCOPE
                                , PermissionObjectType.USER_ACCOUNT,user.getUsername(),user.getUniqueId());
            }

            migrateHolder(user,object);
        }

        luckPerms.getTrackManager().loadAllTracks().get(30,TimeUnit.SECONDS);
        for (Track track : luckPerms.getTrackManager().getLoadedTracks()) {
            PermissionObjectTrack newTrack = DKPerms.getInstance().getObjectManager().getTrack(track.getName(),DKPermsConfig.OBJECT_TRACK_SCOPE);
            if(newTrack == null) newTrack = DKPerms.getInstance().getObjectManager().createTrack(track.getName(),DKPermsConfig.OBJECT_TRACK_SCOPE);
            for (String group : track.getGroups()) {
                PermissionObject newGroup = migratedGroups.get(group);
                if(newGroup != null){
                    newTrack.addBefore(newGroup);
                }
            }
        }

        return false;
    }

    private void migrateHolder(PermissionHolder holder, PermissionObject object){
        for (Node node : holder.getNodes()) {
            long timeout = node.getExpiry() == null ? -1 : node.getExpiry().getEpochSecond()*1000;
            PermissionScope scope = extractScope(object, node);
            PermissionAction action = node.getValue() ? PermissionAction.ALLOW : PermissionAction.REJECT;

            if(node instanceof PermissionNode){
                String permission = ((PermissionNode) node).getPermission();
                object.setPermission(admin,scope,permission,action,timeout);
            }else if(node instanceof InheritanceNode){
                String groupName = ((InheritanceNode) node).getGroupName();
                PermissionObject group = migratedGroups.get(groupName);
                if(group != null){
                    object.setParent(admin,scope,group,action,timeout);
                }
            }else if(node instanceof MetaNode){
                object.getMeta().set(admin,node.getKey(),node.getValue(),0,scope,timeout);
            }else if(node instanceof ChatMetaNode){
                int priority = ((ChatMetaNode<?,?>) node).getPriority();
                object.getMeta().set(admin,node.getKey(),node.getValue(),priority,scope,timeout);
            }else if (node instanceof WeightNode){
                int weight = ((WeightNode) node).getWeight();
                object.setPriority(admin,weight);
            }
        }
    }

    private PermissionScope extractScope(PermissionObject object, Node node) {
        String server = null;
        String world = null;
        for (Context context : node.getContexts()) {
            if(context.getKey().equals(DefaultContextKeys.SERVER_KEY)){
                server = context.getKey();
            }
            if(context.getKey().equals(DefaultContextKeys.WORLD_KEY)){
                world = context.getKey();
            }
        }
        PermissionScope scope = object.getScope();

        if(server != null){
            scope = scope.getChild("server",server);
        }
        if(world != null){
            scope = scope.getChild("world",world);
        }

        return scope;
    }
}
