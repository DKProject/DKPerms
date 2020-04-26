/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 07.04.20, 20:32
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.migration;

import ch.dkrieger.permissionsystem.lib.PermissionAdapter;
import ch.dkrieger.permissionsystem.lib.PermissionSystem;
import ch.dkrieger.permissionsystem.lib.PermissionType;
import ch.dkrieger.permissionsystem.lib.entity.PermissionEntityStorage;
import ch.dkrieger.permissionsystem.lib.group.PermissionGroup;
import ch.dkrieger.permissionsystem.lib.group.PermissionGroupEntity;
import ch.dkrieger.permissionsystem.lib.group.PermissionGroupStorage;
import ch.dkrieger.permissionsystem.lib.permission.PermissionEntity;
import ch.dkrieger.permissionsystem.lib.permission.PermissionStorage;
import ch.dkrieger.permissionsystem.lib.permission.data.PermissionData;
import ch.dkrieger.permissionsystem.lib.permission.data.SimplePermissionData;
import ch.dkrieger.permissionsystem.lib.player.PermissionPlayer;
import ch.dkrieger.permissionsystem.lib.player.PermissionPlayerStorage;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.migration.PermissionMigration;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import org.mcnative.common.McNative;
import org.mcnative.common.player.MinecraftPlayer;
import org.mcnative.common.player.data.PlayerDataProvider;

import java.util.*;

public class DKPermsLegacyMigration implements PermissionMigration {

    private PermissionPlayerStorage playerStorage;
    private PermissionStorage permissionStorage;
    private PermissionEntityStorage entityStorage;
    private PermissionGroupStorage groupStorage;

    private final Map<UUID,PermissionObject> migratedGroups;
    private final PermissionObject admin;

    public DKPermsLegacyMigration() {
        migratedGroups = new HashMap<>();
        admin = DKPerms.getInstance().getObjectManager().getSuperAdministrator();
    }

    @Override
    public String getName() {
        return "DKPermsLegacy";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public boolean migrate() throws Exception{
        migratedGroups.clear();

        connect();

        migrateGroups();

        for (Map.Entry<UUID, PermissionObject> entry : this.migratedGroups.entrySet()) {
            migrateObject(PermissionType.GROUP,entry.getKey(),entry.getValue());
        }

        for (PermissionPlayer player : playerStorage.getPlayers()) {
            MinecraftPlayer minecraftPlayer =  McNative.getInstance().getPlayerManager().getPlayer(player.getUUID());
            if(minecraftPlayer == null){
                McNative.getInstance().getRegistry().getService(PlayerDataProvider.class)
                        .createPlayerData(player.getName(),player.getUUID(),-1,-1,-1,null);
            }

            PermissionObject object = DKPerms.getInstance().getObjectManager().getObjectByAssignment(player.getUUID());
            if(object == null){
                object = DKPerms.getInstance().getObjectManager()
                        .createObject(DKPermsConfig.OBJECT_PLAYER_SCOPE
                                ,DKPermsConfig.OBJECT_PLAYER_TYPE,player.getName(),player.getUUID());
            }
            migrateObject(PermissionType.PLAYER,player.getUUID(),object);
        }

        migratedGroups.clear();
        PermissionSystem.getInstance().disable();
        return true;
    }

    private void connect(){
        PermissionSystem system = new PermissionSystem();
        this.playerStorage = system.getPlayerStorage();
        this.groupStorage = system.getGroupStorage();
        this.entityStorage = system.getEntityStorage();
        this.permissionStorage = system.getPermissionStorage();
    }

    private void migrateGroups(){
        Collection<PermissionGroup> groups = groupStorage.loadGroups();
        for (PermissionGroup group : groups) {
            PermissionObject object = DKPerms.getInstance().getObjectManager()
                    .getObject(group.getName()
                    ,DKPermsConfig.OBJECT_GROUP_SCOPE
                    ,DKPermsConfig.OBJECT_GROUP_TYPE);
            if(object == null){
                object = DKPerms.getInstance().getObjectManager()
                        .createObject(DKPermsConfig.OBJECT_GROUP_SCOPE
                                ,DKPermsConfig.OBJECT_GROUP_TYPE,group.getName());
            }
            object.setPriority(admin,group.getPriority());

            if(group.getDescription() != null && group.getDescription().isEmpty()){
                object.getMeta().set(admin,"description",group.getDescription());
            }

            if(group.getJoinPower() > 0){
                object.getMeta().set(admin,"joinPower",group.getJoinPower());
            }

            object.getMeta().set(admin,"team",group.isTeam());
            object.getMeta().set(admin,"default",group.isDefault());

            if(group.getPlayerDesign() != null){
                object.getMeta().set(admin,"color",group.getPlayerDesign().getColor());
                object.getMeta().set(admin,"chat",group.getPlayerDesign().getDisplay());
                object.getMeta().set(admin,"prefix",group.getPlayerDesign().getPrefix());
                object.getMeta().set(admin,"suffix",group.getPlayerDesign().getSuffix());
            }
            this.migratedGroups.put(group.getUUID(),object);
        }
    }

    private void migrateObject(PermissionType type, UUID uniqueId,PermissionObject object){
        List<PermissionGroupEntity> groups =  entityStorage.getPermissionEntities(type,uniqueId);
        for (PermissionGroupEntity group : groups) {
            PermissionObject newGroup = this.migratedGroups.get(group.getGroupUUID());
            if(newGroup != null){
                if(object.isInGroup(object.getScope(),newGroup) == PermissionAction.NEUTRAL){
                    object.addGroup(admin,object.getScope(),newGroup, PermissionAction.ALLOW,-1);
                }
            }
        }

        PermissionData data = permissionStorage.getPermissions(type,uniqueId);

        //Global data
        migrateData(object,object.getScope(),data);

        for (Map.Entry<String, SimplePermissionData> group : data.getGroupPermissions().entrySet()) {
            PermissionScope scope = object.getScope().getChild("serverGroup",group.getKey());
            migrateData(object,scope,group.getValue());
        }

        for (Map.Entry<String, SimplePermissionData> group : data.getServerPermissions().entrySet()) {
            PermissionScope scope = object.getScope().getChild("server",group.getKey());
            migrateData(object,scope,group.getValue());
        }
    }

    private void migrateData(PermissionObject object, PermissionScope scope,SimplePermissionData data){
        for (PermissionEntity permission : data.getPermissions()) {
            if(!permission.hasTimeOut()){
                PermissionAction action = permission.isNegative() ? PermissionAction.REJECT : PermissionAction.ALLOW;
                if(object.getPermission(scope,permission.getRawPermission()) == null){
                    object.addPermission(admin,scope,permission.getRawPermission(),action,permission.getTimeOut());
                }
            }
        }
        if(data.getWorldPermissions().size() > 0){
            for (Map.Entry<String, List<PermissionEntity>> entry : data.getWorldPermissions().entrySet()) {
                PermissionScope worldScope = scope.getChild("world",entry.getKey());
                for (PermissionEntity permission : entry.getValue()) {
                    if(!permission.hasTimeOut()){
                        PermissionAction action = permission.isNegative() ? PermissionAction.REJECT : PermissionAction.ALLOW;
                        if(object.getPermission(worldScope,permission.getRawPermission()) == null){
                            object.addPermission(admin,worldScope,permission.getRawPermission(),action,permission.getTimeOut());
                        }
                    }
                }
            }
        }
    }

}
