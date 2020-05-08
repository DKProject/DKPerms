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
import net.pretronic.dkperms.api.minecraft.player.PermissionPlayer;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectManager;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.libraries.logging.PretronicLogger;
import org.mcnative.common.player.MinecraftPlayer;
import org.mcnative.common.serviceprovider.permission.PermissionHandler;
import org.mcnative.common.serviceprovider.permission.PermissionProvider;

import java.util.Collection;
import java.util.Collections;

public class DKPermsPermissionProvider implements PermissionProvider {

    private final PretronicLogger logger;
    private final PermissionObjectManager objectManager;
    private final PermissionObjectType groupType;
    private final PermissionObjectType userType;

    public DKPermsPermissionProvider() {
        this.logger = DKPerms.getInstance().getLogger();
        this.objectManager = DKPerms.getInstance().getObjectManager();
        this.groupType = objectManager.getTypeOrCreate("group",true);
        this.userType = objectManager.getTypeOrCreate("user",true);
    }

    @Override
    public Collection<MinecraftPlayer> getOperators() {
        /*
        return Iterators.map(objectManager.search().type(userType).hasMeta("operator", true)
                .search().getAllHolders(PermissionPlayer.class), player -> player);
         */
        return Collections.emptyList();//@Todo implement
    }

    @Override
    public Collection<String> getGroups() {
        //objectManager.get
        return Collections.emptyList();//@Todo implement
    }

    @Override
    public PermissionHandler getPlayerHandler(MinecraftPlayer player) {
        return player.getAs(PermissionPlayer.class);
    }

    @Override
    public boolean createGroup(String group) {
        return objectManager.createObject(null,groupType,group)
                .getHolder(DKPermsPermissionGroup.class);
    }

    @Override
    public boolean deleteGroup(String group) {
        try{
            PermissionObject result = objectManager.getObject(group,null,groupType);
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
    public void setGroupPermission(String s, String s1, boolean b) {

    }

    @Override
    public void unsetGroupPermission(String s, String s1) {

    }
}
