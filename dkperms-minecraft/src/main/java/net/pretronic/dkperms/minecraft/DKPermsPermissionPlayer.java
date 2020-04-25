/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 18.01.20, 15:13
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.entity.PermissionGroupEntity;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.graph.ObjectMetaGraph;
import net.pretronic.dkperms.api.graph.PermissionGraph;
import net.pretronic.dkperms.api.minecraft.event.MinecraftPlayerPermissionScopeChangeEvent;
import net.pretronic.dkperms.api.minecraft.player.PermissionPlayer;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.SyncAction;
import net.pretronic.dkperms.api.object.snapshot.PermissionObjectSnapshot;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.libraries.synchronisation.observer.ObserveCallback;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.McNative;
import org.mcnative.common.player.MinecraftPlayer;
import org.mcnative.common.player.PlayerDesign;
import org.mcnative.common.serviceprovider.permission.PermissionHandler;
import org.mcnative.common.serviceprovider.permission.PermissionResult;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

//@Todo on scope change listener and event
public class DKPermsPermissionPlayer implements PermissionPlayer, ObserveCallback<PermissionObject, SyncAction> {

    private final PermissionObject object;
    private PlayerDesign design;

    public DKPermsPermissionPlayer(PermissionObject object) {
        this.object = object;
        setCurrentScope(DKPerms.getInstance().getScopeManager().getCurrentInstanceScope());
        this.design = new DKPermsPlayerDesign(object);
    }

    @Override
    public PermissionObject getObject() {
        return object;
    }

    @Override
    public PermissionObjectSnapshot getSnapshot() {
        return object.getCurrentSnapshot();
    }

    @Override
    public Graph<PermissionScope> getCurrentScopes() {
        return object.getCurrentSnapshot().getScopeRange();
    }

    @Override
    public PermissionScope getCurrentScope() {
        return object.getScope();
    }

    @Override
    public void setCurrentScope(PermissionScope scope) {
        object.setCurrentScope(scope);
        object.getCurrentSnapshot().getGroupGraph().subscribeObserver(this);
    }

    @Override
    public Collection<String> getGroups() {
        return Iterators.map(object.getCurrentSnapshot().getEffectedGroupGraph(), PermissionObject::getName);
    }

    @Override
    public Collection<String> getPermissions() {
        return object.getCurrentSnapshot().getPermissionGraph().toStringList();
    }

    @Override
    public Collection<String> getEffectivePermissions() {
        return object.getCurrentSnapshot().getPermissionInheritanceGraph().toStringList();
    }

    @Override
    public PlayerDesign getDesign() {
        return design;
    }

    @Override
    public PlayerDesign getDesign(MinecraftPlayer player) {
        return design;
    }

    @Override
    public boolean isPermissionSet(String permission) {
        return object.getCurrentSnapshot().getPermissionInheritanceGraph().containsPermission(permission);
    }

    @Override
    public boolean isPermissionAssigned(String permission) {
        return object.getCurrentSnapshot().getPermissionInheritanceGraph().containsPermission(permission);
    }

    @Override
    public boolean hasPermission(String permission) {
        return object.getCurrentSnapshot().getPermissionInheritanceGraph().calculatePermission(permission).has();
    }

    @Override
    public PermissionResult hasPermissionExact(String permission) {
        PermissionAction result = object.getCurrentSnapshot().getPermissionInheritanceGraph().calculatePermission(permission);
        if(result == PermissionAction.NEUTRAL) return PermissionResult.NORMAL;
        else return result.has() ? PermissionResult.ALLOWED : PermissionResult.DENIED;
    }

    @Override
    public void setPermission(String permission, boolean action0) {
        PermissionEntity entity = object.getPermission(DKPermsConfig.MCNATIVE_MANAGEMENT_SCOPE_PERMISSION,permission);
        PermissionAction action = action0? PermissionAction.ALLOW:PermissionAction.REJECT;
        if(entity == null){
            object.addPermission(DKPerms.getInstance().getObjectManager().getSuperAdministrator()
                    ,DKPermsConfig.MCNATIVE_MANAGEMENT_SCOPE_PERMISSION
                    ,permission,action,-1);
        }else if(!entity.getAction().equals(action)){
            entity.setAction(DKPerms.getInstance().getObjectManager().getSuperAdministrator(),action);
        }
    }

    @Override
    public void unsetPermission(String permission) {
        object.removePermission(DKPerms.getInstance().getObjectManager().getSuperAdministrator()
                ,DKPermsConfig.MCNATIVE_MANAGEMENT_SCOPE_PERMISSION
                ,permission);
    }

    @Override
    public void addGroup(String name) {
        PermissionObject group = DKPerms.getInstance().getObjectManager()
                .getObject(name,DKPermsConfig.OBJECT_GROUP_SCOPE,DKPermsConfig.OBJECT_GROUP_TYPE);

        PermissionGroupEntity entity = object.getGroup(DKPermsConfig.MCNATIVE_MANAGEMENT_SCOPE_PERMISSION,group);

        if(entity == null){
            object.addGroup(DKPerms.getInstance().getObjectManager().getSuperAdministrator()
                    ,DKPermsConfig.MCNATIVE_MANAGEMENT_SCOPE_GROUP
                    ,group,PermissionAction.ALLOW,-1);
        }else if(entity.getAction() != PermissionAction.ALLOW){
            entity.setAction(DKPerms.getInstance().getObjectManager().getSuperAdministrator(),PermissionAction.ALLOW);
        }
    }

    @Override
    public void removeGroup(String name) {
        PermissionObject group = DKPerms.getInstance().getObjectManager()
                .getObject(name,DKPermsConfig.OBJECT_GROUP_SCOPE,DKPermsConfig.OBJECT_GROUP_TYPE);
        object.removeGroup(DKPerms.getInstance().getObjectManager().getSuperAdministrator()
                ,DKPermsConfig.MCNATIVE_MANAGEMENT_SCOPE_GROUP,group);
    }

    @Override
    public boolean isOperator() {
        return DKPermsConfig.SECURITY_OPERATOR_ENABLED && object.getCurrentSnapshot()
                .getMetaInheritanceGraph().isSet("operator",true);
    }

    @Override
    public void setOperator(boolean operator) {
        if(DKPermsConfig.SECURITY_OPERATOR_ENABLED ){
            object.getMeta().set(DKPerms.getInstance().getObjectManager().getSuperAdministrator()
                    ,"operator"
                    ,operator
                    ,DKPermsConfig.MCNATIVE_MANAGEMENT_SCOPE_OPERATOR);
        }
    }

    @Override
    public boolean isCached() {
        return false;
    }

    @Override
    public boolean setCached(boolean b) {
        return false;
    }

    @Override
    public PermissionHandler reload() {
        return this;
    }

    @Override
    public void callback(PermissionObject object, SyncAction action) {
        McNative.getInstance().getScheduler().createTask(ObjectOwner.SYSTEM)
                .delay(1, TimeUnit.SECONDS)
                .execute(() -> {
                    if(McNative.getInstance().getLocal().getServerTablist() != null){
                        McNative.getInstance().getLocal().getServerTablist().update();
                    }
                });
    }
}
