/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 23.05.20, 12:54
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.migration.permissionsex;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.migration.MigrationExecutor;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectTrack;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.dkperms.minecraft.migration.MigrationUtil;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.reflect.ReflectionUtil;
import org.bukkit.Bukkit;
import ru.tehkode.permissions.*;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.*;

public class PermissionsExMigrationExecutor implements MigrationExecutor {

    private final PermissionObject admin;
    private final Map<String, PermissionObject> migratedGroups;

    public PermissionsExMigrationExecutor() {
        admin = DKPerms.getInstance().getObjectManager().getSuperAdministrator();
        migratedGroups = new HashMap<>();
    }

    @Override
    public boolean migrate() throws Exception {
        PermissionManager manager = PermissionsEx.getPermissionManager();

        Set<String> ladderNames = new HashSet<>();
        for (PermissionGroup group : manager.getGroupList()) {
            PermissionObject object = MigrationUtil.createOrGetGroup(group.getName());
            object.setPriority(admin,group.getRank());
            ladderNames.add(group.getRankLadder());
            migratedGroups.put(group.getIdentifier(),object);
        }

        for (PermissionGroup group : manager.getGroupList()) {
            migrateEntity(group,migratedGroups.get(group.getIdentifier()));
        }

        for (PermissionUser user : manager.getUsers()) {
            String identifier = user.getIdentifier();
            UUID uniqueId = null;
            try {
                uniqueId = UUID.fromString(identifier);
            }catch (Exception ignored){}
            if(uniqueId == null){
                uniqueId = Bukkit.getOfflinePlayer(identifier).getUniqueId();
            }
            PermissionObject object = DKPerms.getInstance().getObjectManager().getObjectByAssignment(uniqueId);
            if(object == null){
                object = DKPerms.getInstance().getObjectManager()
                        .createObject(DKPermsConfig.OBJECT_PLAYER_SCOPE
                                , PermissionObjectType.USER_ACCOUNT,user.getName(),uniqueId);
            }
            migrateEntity(user,object);
        }

        for (String ladderName : ladderNames) {
            Map<Integer,PermissionGroup> order = new TreeMap<>(Integer::compare);
            order.putAll(manager.getRankLadder(ladderName));
            PermissionObjectTrack track = DKPerms.getInstance().getObjectManager().getTrack(ladderName,DKPermsConfig.OBJECT_TRACK_SCOPE);
            if(track == null) track = DKPerms.getInstance().getObjectManager().createTrack(ladderName,DKPermsConfig.OBJECT_TRACK_SCOPE);
            for (Map.Entry<Integer, PermissionGroup> entry : order.entrySet()) {
                PermissionObject object = migratedGroups.get(entry.getValue().getIdentifier());
                if(object != null){
                    track.addAfter(object);
                }
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private void migrateEntity(PermissionEntity entity,PermissionObject object){
        PermissionsData data = (PermissionsData) ReflectionUtil.invokeMethod(entity,"getData");

        for (Map.Entry<String, List<String>> entry : data.getPermissionsMap().entrySet()) {
            if(!entry.getValue().isEmpty()){
                String world = entry.getKey();
                PermissionScope scope = world != null ?  object.getScope().getChild("world",entry.getKey()) : object.getScope();
                for (String permission : entry.getValue()) {
                    object.setPermission(admin, scope,permission, PermissionAction.ALLOW,-1);
                }
            }
        }

        Map<String, List<String>> timedPermissions = (Map<String, List<String>>) ReflectionUtil.getFieldValue(entity,"timedPermissions");
        Map<String, Long> timedPermissionsTime = (Map<String, Long>) ReflectionUtil.getFieldValue(entity,"timedPermissionsTime");

        for (Map.Entry<String, List<String>> entry : timedPermissions.entrySet()) {
            if(!entry.getValue().isEmpty()){
                String world = entry.getKey();
                PermissionScope scope = world != null ?  object.getScope().getChild("world",entry.getKey()) : object.getScope();
                for (String permission : entry.getValue()) {
                    long timeOut = timedPermissionsTime.getOrDefault(world != null ? world+":"+permission: permission,0L);
                    object.setPermission(admin, scope,permission, PermissionAction.ALLOW,timeOut);
                }
            }
        }

        for (Map.Entry<String, Map<String, String>> entry : entity.getAllOptions().entrySet()) {
            if(!entry.getValue().isEmpty()){
                String world = entry.getKey();
                PermissionScope scope = world != null ?  object.getScope().getChild("world",entry.getKey()) : object.getScope();
                for (Map.Entry<String, String> optionPair : entry.getValue().entrySet()) {
                    if(!optionPair.getKey().startsWith("group-")){
                        object.getMeta().set(admin,optionPair.getKey(),optionPair.getValue(),0,scope,-1);
                    }
                }
            }
        }

        for (Map.Entry<String, List<PermissionGroup>> entry : entity.getAllParents().entrySet()) {
            if(!entry.getValue().isEmpty()){
                String world = entry.getKey();
                PermissionScope scope = world != null && !world.equals("global") ? object.getScope().getChild("world",entry.getKey()) : object.getScope();
                for (PermissionGroup group : entry.getValue()) {
                    PermissionObject newGroup = migratedGroups.get(group.getIdentifier());
                    if(newGroup != null){
                        long timeout = -1;
                        String expiry = entity.getOption("group-" + group.getName() + "-until", world);
                        if (expiry != null) {
                            if (GeneralUtil.isNaturalNumber(expiry)) {
                                timeout = Long.parseLong(expiry);
                            }
                        }
                        object.setParent(admin,scope,newGroup,PermissionAction.ALLOW,-timeout);
                    }
                }
            }
        }
    }
}
