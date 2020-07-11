/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 21.05.20, 14:06
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.migration.cloudnet;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.*;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.entity.Entity;
import net.pretronic.dkperms.api.migration.MigrationExecutor;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.dkperms.minecraft.migration.MigrationUtil;
import net.pretronic.libraries.utility.map.caseintensive.CaseIntensiveHashMap;
import org.mcnative.common.McNative;
import org.mcnative.common.player.MinecraftPlayer;
import org.mcnative.common.player.data.PlayerDataProvider;

import java.util.Collection;
import java.util.Map;

public class CloudNetV3CPermsMigrationExecutor implements MigrationExecutor {

    private final Map<String, PermissionObject> migratedGroups;
    private final PermissionObject admin;

    public CloudNetV3CPermsMigrationExecutor() {
        migratedGroups = new CaseIntensiveHashMap<>();
        admin = DKPerms.getInstance().getObjectManager().getSuperAdministrator();
    }

    @Override
    public boolean migrate() throws Exception {
        IPermissionManagement permissionManagement = CloudNetDriver.getInstance().getPermissionManagement();
        for (IPermissionGroup group : permissionManagement.getGroups()) {
            PermissionObject object = MigrationUtil.createOrGetGroup(group.getName());

            migratePermissions(group,object);

            object.getMeta().set(admin,"color",group.getColor(),0,object.getScope(), Entity.PERMANENTLY);
            object.getMeta().set(admin,"prefix",group.getPrefix(),0,object.getScope(), Entity.PERMANENTLY);
            object.getMeta().set(admin,"suffix",group.getSuffix(),0,object.getScope(), Entity.PERMANENTLY);
            object.getMeta().set(admin,"chat",group.getDisplay(),0,object.getScope(), Entity.PERMANENTLY);
            object.getMeta().set(admin,"default",group.isDefaultGroup(),0,object.getScope(), Entity.PERMANENTLY);


            migratePermissions(group,object);

            migratedGroups.put(group.getName(),object);
        }

        for (IPermissionGroup group : permissionManagement.getGroups()) {
            PermissionObject object = migratedGroups.get(group.getName());
            for (String implementGroup : group.getGroups()) {
                PermissionObject newGroup = migratedGroups.get(implementGroup);
                if(object != null){
                    object.addParent(admin,object.getScope(),newGroup,PermissionAction.ALLOW,Entity.PERMANENTLY);
                }
            }
        }

        for (IPermissionUser user : CloudNetDriver.getInstance().getPermissionManagement().getUsers()) {
            MinecraftPlayer minecraftPlayer = McNative.getInstance().getPlayerManager().getPlayer(user.getUniqueId());
            if(minecraftPlayer == null){
                McNative.getInstance().getRegistry().getService(PlayerDataProvider.class)
                        .createPlayerData(user.getName(),user.getUniqueId(),-1,-1,-1,null);
            }

            PermissionObject object = DKPerms.getInstance().getObjectManager().getObjectByAssignment(user.getUniqueId());
            if(object == null){
                object = DKPerms.getInstance().getObjectManager()
                        .createObject(DKPermsConfig.OBJECT_PLAYER_SCOPE
                                , PermissionObjectType.USER_ACCOUNT,user.getName(),user.getUniqueId());
            }

            migratePermissions(user,object);

            for (PermissionUserGroupInfo group : user.getGroups()) {
                PermissionObject newGroup = migratedGroups.get(group.getGroup());
                if(newGroup != null){
                    object.addParent(admin,object.getScope(),newGroup,PermissionAction.ALLOW,group.getTimeOutMillis());
                }
            }
        }

        return false;
    }

    private void migratePermissions(IPermissible permissible,PermissionObject object){
        object.setPriority(admin,permissible.getPotency());

        for (Permission permission : permissible.getPermissions()) {
            object.setPermission(admin,object.getScope()
                    ,permission.getName()
                    ,PermissionAction.ALLOW
                    ,permission.getTimeOutMillis());
        }

        for (Map.Entry<String, Collection<Permission>> entry : permissible.getGroupPermissions().entrySet()) {
            PermissionScope scope = object.getScope().getChild("serverGroup",entry.getKey());
            for (Permission permission : entry.getValue()) {
                object.setPermission(admin
                        ,scope
                        ,permission.getName()
                        ,PermissionAction.ALLOW
                        ,permission.getTimeOutMillis());
            }
        }

        for (String property : permissible.getProperties()) {
            object.getMeta().set(admin,property,permissible.getProperties().getString(property)
                    ,0,object.getScope(), Entity.PERMANENTLY);
        }
    }
}
