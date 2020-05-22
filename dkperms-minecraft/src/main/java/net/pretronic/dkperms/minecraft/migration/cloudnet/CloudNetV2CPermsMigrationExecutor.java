/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 21.05.20, 13:45
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.migration.cloudnet;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.GroupEntityData;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.player.permission.PermissionPool;
import de.dytanic.cloudnet.lib.utility.document.Document;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.entity.Entity;
import net.pretronic.dkperms.api.migration.MigrationExecutor;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.libraries.utility.exception.OperationFailedException;
import net.pretronic.libraries.utility.map.caseintensive.CaseIntensiveHashMap;
import org.mcnative.common.McNative;
import org.mcnative.common.player.MinecraftPlayer;
import org.mcnative.common.player.data.PlayerDataProvider;

import java.util.List;
import java.util.Map;

public class CloudNetV2CPermsMigrationExecutor implements MigrationExecutor {

    private final Map<String,PermissionObject> migratedGroups;
    private final PermissionObject admin;

    public CloudNetV2CPermsMigrationExecutor() {
        migratedGroups = new CaseIntensiveHashMap<>();
        admin = DKPerms.getInstance().getObjectManager().getSuperAdministrator();
    }

    @Override
    public boolean migrate() throws Exception {
        PermissionPool pool = CloudAPI.getInstance().getPermissionPool();
        if(pool == null) throw new OperationFailedException("CloudNet permission pool is not available");

        for (PermissionGroup group : pool.getGroups().values()) {
            PermissionObject object = DKPerms.getInstance().getObjectManager()
                    .getObject(group.getName()
                            , DKPermsConfig.OBJECT_GROUP_SCOPE
                            ,DKPermsConfig.OBJECT_GROUP_TYPE);
            if(object == null){
                object = DKPerms.getInstance().getObjectManager()
                        .createObject(DKPermsConfig.OBJECT_GROUP_SCOPE
                                ,DKPermsConfig.OBJECT_GROUP_TYPE,group.getName());
            }
            object.setPriority(admin,group.getTagId());

            object.getMeta().set(admin,"color",group.getColor(),0,object.getScope(), Entity.PERMANENTLY);
            object.getMeta().set(admin,"prefix",group.getPrefix(),0,object.getScope(), Entity.PERMANENTLY);
            object.getMeta().set(admin,"suffix",group.getSuffix(),0,object.getScope(), Entity.PERMANENTLY);
            object.getMeta().set(admin,"chat",group.getDisplay(),0,object.getScope(), Entity.PERMANENTLY);
            object.getMeta().set(admin,"joinPower",group.getJoinPower(),0,object.getScope(), Entity.PERMANENTLY);
            object.getMeta().set(admin,"default",group.isDefaultGroup(),0,object.getScope(), Entity.PERMANENTLY);

            for (Map.Entry<String, Boolean> entry : group.getPermissions().entrySet()) {
                object.setPermission(admin,object.getScope()
                        ,entry.getKey()
                        ,entry.getValue() ? PermissionAction.ALLOW : PermissionAction.REJECT
                        ,Entity.PERMANENTLY);
            }

            for (Map.Entry<String, List<String>> entry : group.getServerGroupPermissions().entrySet()) {
                PermissionScope scope = object.getScope().getChild("serverGroup",entry.getKey());
                for (String permission : entry.getValue()) {
                    object.setPermission(admin
                            ,scope
                            ,permission
                            ,PermissionAction.ALLOW
                            ,Entity.PERMANENTLY);
                }
            }

            for (Map.Entry<String, Object> entry : group.getOptions().entrySet()) {
                object.getMeta().set(admin,entry.getKey(),entry.getValue(),0,object.getScope(), Entity.PERMANENTLY);
            }
            
            migratedGroups.put(group.getName(),object);
        }

        for (PermissionGroup group : pool.getGroups().values()) {
            PermissionObject object = migratedGroups.get(group.getName());
            for (String implementGroup : group.getImplementGroups()) {
                PermissionObject newGroup = migratedGroups.get(implementGroup);
                if(object != null){
                    object.addParent(admin,object.getScope(),newGroup,PermissionAction.ALLOW,Entity.PERMANENTLY);
                }
            }
        }

        Database database = CloudAPI.getInstance().getDatabaseManager().getDatabase("cloudnet_internal_players");
        if(database != null){
            database.loadDocuments();
            for(Document data : database.getDocs()){
                OfflinePlayer player = data.getObject("offlinePlayer",OfflinePlayer.TYPE);
                PermissionEntity entity = player.getPermissionEntity();

                MinecraftPlayer minecraftPlayer = McNative.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
                if(minecraftPlayer == null){
                    McNative.getInstance().getRegistry().getService(PlayerDataProvider.class)
                            .createPlayerData(player.getName(),player.getUniqueId(),-1
                                    ,player.getFirstLogin(),player.getLastLogin(),null);
                }

                PermissionObject object = DKPerms.getInstance().getObjectManager().getObjectByAssignment(player.getUniqueId());
                if(object == null){
                    object = DKPerms.getInstance().getObjectManager()
                            .createObject(DKPermsConfig.OBJECT_PLAYER_SCOPE
                                    ,DKPermsConfig.OBJECT_PLAYER_TYPE,player.getName(),player.getUniqueId());
                }

                for (Map.Entry<String, Boolean> entry : entity.getPermissions().entrySet()) {
                    object.setPermission(admin,object.getScope()
                            ,entry.getKey()
                            ,entry.getValue() ? PermissionAction.ALLOW : PermissionAction.REJECT
                            ,Entity.PERMANENTLY);
                }

                for (GroupEntityData group : entity.getGroups()) {
                    PermissionObject newGroup = migratedGroups.get(group.getGroup());
                    if(newGroup != null){
                        object.addParent(admin,object.getScope(),newGroup,PermissionAction.ALLOW,group.getTimeout());
                    }
                }
            }
        }

        return false;
    }
}
