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
import net.pretronic.dkperms.api.entity.Entity;
import net.pretronic.dkperms.api.migration.PermissionMigration;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import org.mcnative.common.McNative;
import org.mcnative.common.player.MinecraftPlayer;
import org.mcnative.common.player.data.PlayerDataProvider;

import java.io.File;
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
    public String getDisplayName() {
        return "DKPermsLegacy (DKPerms V1)";
    }

    @Override
    public String getName() {
        return "DKPermsLegacy";
    }

    @Override
    public boolean isAvailable() {
        return new File("plugins/DKPerms/legacy-config.yml").exists();
    }

    @Override
    public boolean migrate() throws Exception{
        migratedGroups.clear();

        connect();

        migrateGroups();

        for (Map.Entry<UUID, PermissionObject> entry : this.migratedGroups.entrySet()) {
            migrateObject(PermissionType.GROUP,entry.getKey(),entry.getValue());
        }
        DKPerms.getInstance().getLogger().info("(Migration) Migrated all groups");
        for (PermissionPlayer player : playerStorage.getPlayers()) {
            DKPerms.getInstance().getLogger().info("(Migration) Migrating user "+player.getName());
            MinecraftPlayer minecraftPlayer = McNative.getInstance().getPlayerManager().getPlayer(player.getUUID());
            if(minecraftPlayer == null){
                McNative.getInstance().getRegistry().getService(PlayerDataProvider.class)
                        .createPlayerData(player.getName(),player.getUUID(),-1,-1,-1,null);
            }

            PermissionObject object = DKPerms.getInstance().getObjectManager().getObjectByAssignment(player.getUUID());
            if(object == null){
                object = DKPerms.getInstance().getObjectManager()
                        .createObject(DKPermsConfig.OBJECT_PLAYER_SCOPE
                                , PermissionObjectType.USER_ACCOUNT,player.getName(),player.getUUID());
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
            DKPerms.getInstance().getLogger().info("(Migration) Migrating group "+group.getName());
            PermissionObject object = MigrationUtil.createOrGetGroup(group.getName());
            object.setPriority(admin,group.getPriority());

            if(group.getDescription() != null && group.getDescription().isEmpty()){
                object.getMeta().set(admin,"description",group.getDescription(),0,object.getScope(), Entity.PERMANENTLY);
            }

            if(group.getJoinPower() > 0){
                object.getMeta().set(admin,"joinPower",group.getJoinPower(),0,object.getScope(), Entity.PERMANENTLY);
            }

            object.getMeta().set(admin,"team",group.isTeam(),0,object.getScope(), Entity.PERMANENTLY);
            object.getMeta().set(admin,"default",group.isDefault(),0,object.getScope(), Entity.PERMANENTLY);

            if(group.getPlayerDesign() != null){
                object.getMeta().set(admin,"color",group.getPlayerDesign().getColor(),0,object.getScope(), Entity.PERMANENTLY);
                object.getMeta().set(admin,"chat",group.getPlayerDesign().getDisplay(),0,object.getScope(), Entity.PERMANENTLY);
                object.getMeta().set(admin,"prefix",group.getPlayerDesign().getPrefix(),0,object.getScope(), Entity.PERMANENTLY);
                object.getMeta().set(admin,"suffix",group.getPlayerDesign().getSuffix(),0,object.getScope(), Entity.PERMANENTLY);
            }
            this.migratedGroups.put(group.getUUID(),object);
            DKPerms.getInstance().getLogger().info("(Migration) Migrated group "+group.getName()+" successfully");
        }
    }

    private void migrateObject(PermissionType type, UUID uniqueId,PermissionObject object){
        List<PermissionGroupEntity> groups =  entityStorage.getPermissionEntities(type,uniqueId);
        for (PermissionGroupEntity group : groups) {
            PermissionObject newGroup = this.migratedGroups.get(group.getGroupUUID());
            if(newGroup != null){
                if(object.hasParent(object.getScope(),newGroup) == PermissionAction.NEUTRAL){
                    object.addParent(admin,object.getScope(),newGroup, PermissionAction.ALLOW,-1);
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
                    object.setPermission(admin,scope,permission.getRawPermission(),action,permission.getTimeOut());
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
                            object.setPermission(admin,worldScope,permission.getRawPermission(),action,permission.getTimeOut());
                        }
                    }
                }
            }
        }
    }
}
