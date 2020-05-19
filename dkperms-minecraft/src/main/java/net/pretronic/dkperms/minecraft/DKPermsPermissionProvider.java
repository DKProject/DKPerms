/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 04.12.19, 20:31
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectManager;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.libraries.logging.PretronicLogger;
import net.pretronic.libraries.utility.Iterators;
import org.mcnative.common.player.MinecraftPlayer;
import org.mcnative.common.serviceprovider.permission.PermissionHandler;
import org.mcnative.common.serviceprovider.permission.PermissionProvider;

import java.util.Collection;

public class DKPermsPermissionProvider implements PermissionProvider {

    private final PretronicLogger logger;
    private final PermissionObjectManager objectManager;

    public DKPermsPermissionProvider() {
        this.logger = DKPerms.getInstance().getLogger();
        this.objectManager = DKPerms.getInstance().getObjectManager();
    }

    @Override
    public Collection<MinecraftPlayer> getOperators() {
        return objectManager.search()
                .withType(PermissionObjectType.USER_ACCOUNT)
                .hasMeta("operator", true)
                .execute().getAllHolders(MinecraftPlayer.class);
    }

    @Override
    public Collection<String> getGroups() { ;
        return Iterators.map(DKPerms.getInstance().getObjectManager()
                .getObjects(PermissionObjectType.GROUP, DKPermsConfig.OBJECT_GROUP_SCOPE).getAll()
                ,PermissionObject::getName);
    }

    @Override
    public PermissionHandler getPlayerHandler(MinecraftPlayer player) {
        return new DKPermsPermissionHandler(player.getAs(PermissionObject.class));
    }

    @Override
    public boolean createGroup(String group) {
        if(objectManager.getObject(group,DKPermsConfig.OBJECT_GROUP_SCOPE,PermissionObjectType.GROUP) != null){
            objectManager.createObject(DKPermsConfig.OBJECT_GROUP_SCOPE,PermissionObjectType.GROUP,group);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteGroup(String group) {
        try{
            PermissionObject result = objectManager.getObject(group,null,PermissionObjectType.GROUP);
            if(result != null){
                objectManager.deleteObject(result);
                return true;
            }
        }catch (Exception exception){
            logger.error("Could not delete permission group "+group,exception);
        }
        return false;
    }

    @Override
    public void setGroupPermission(String group, String permission, boolean value) {
        PermissionObject object = objectManager.getObject(group,DKPermsConfig.OBJECT_GROUP_SCOPE,PermissionObjectType.GROUP);
        if(object != null){
            PermissionAction action = value ? PermissionAction.ALLOW : PermissionAction.REJECT;
            object.setPermission(DKPerms.getInstance().getObjectManager().getSuperAdministrator()
                    ,object.getScope()
                    ,permission
                    ,action
                    ,-1);
        }
    }

    @Override
    public void unsetGroupPermission(String group, String permission) {
        PermissionObject object = objectManager.getObject(group,DKPermsConfig.OBJECT_GROUP_SCOPE,PermissionObjectType.GROUP);
        if(object != null){
            object.unsetPermission(DKPerms.getInstance().getObjectManager().getSuperAdministrator()
                    ,permission);
        }
    }
}
