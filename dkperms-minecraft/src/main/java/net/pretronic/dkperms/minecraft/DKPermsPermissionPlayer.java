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

import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.graph.ObjectMetaGraph;
import net.pretronic.dkperms.api.graph.PermissionGraph;
import net.pretronic.dkperms.api.minecraft.event.MinecraftPlayerPermissionScopeChangeEvent;
import net.pretronic.dkperms.api.minecraft.player.PermissionPlayer;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import org.mcnative.common.McNative;
import org.mcnative.common.player.MinecraftPlayer;
import org.mcnative.common.player.PlayerDesign;
import org.mcnative.common.serviceprovider.permission.PermissionHandler;
import org.mcnative.common.serviceprovider.permission.PermissionResult;

import java.util.Collection;
import java.util.function.BiFunction;

public  class DKPermsPermissionPlayer implements PermissionPlayer {

    private final PermissionObject object;
    private final DKPermsPlayerDesign design;
    private PermissionScope currentScope;

    private Graph<PermissionScope> scopes;
    private PermissionGraph currentPermissions;
    private PermissionGraph currentInheritancePermissions;
    private ObjectMetaGraph currentMeta;
    private Graph<PermissionObject> currentGroups;

    private BiFunction<MinecraftPlayer, PlayerDesign, PlayerDesign> designGetter;

    public DKPermsPermissionPlayer(PermissionObject object) {
        this.object = object;
        this.design = new DKPermsPlayerDesign();
        setCurrentScope(DKPerms.getInstance().getScopeManager().getCurrentInstanceScope());
    }

    @Override
    public PermissionObject getObject() {
        return object;
    }

    @Override
    public Graph<PermissionScope> getCurrentScopes() {
        return scopes;
    }

    @Override
    public PermissionScope getCurrentScope() {
        return currentScope;
    }

    @Override
    public void setCurrentScope(PermissionScope scope) {
        MinecraftPlayerPermissionScopeChangeEvent event = new MinecraftPlayerPermissionScopeChangeEvent(this,scope);
        McNative.getInstance().getLocal().getEventBus().callEvent(event);
        this.currentScope = event.getNewScope();

        scopes = DKPerms.getInstance().getScopeManager().newGraph(this.currentScope);
        currentPermissions = object.newPermissionGraph(scopes);
        currentInheritancePermissions = object.newPermissionInheritanceGraph(scopes);
        currentMeta = object.getMeta().newInheritanceGraph(scopes);
        currentGroups = object.newGroupGraph(scopes);
        design.update(currentMeta);

        currentPermissions.subscribeObservers();
        currentInheritancePermissions.subscribeObservers();
        currentMeta.subscribeObservers();
        currentGroups.subscribeObservers();
    }

    @Override
    public Collection<String> getGroups() {
        return Iterators.map(currentGroups, PermissionObject::getName);
    }

    @Override
    public Collection<String> getPermissions() {
        return currentPermissions.toStringList();
    }

    @Override
    public Collection<String> getEffectivePermissions() {
        return currentInheritancePermissions.toStringList();
    }

    @Override
    public PlayerDesign getDesign() {
        return getDesign(null);
    }

    @Override
    public PlayerDesign getDesign(MinecraftPlayer player) {
        if(designGetter == null) return design;
        else return designGetter.apply(player,design);
    }

    @Override
    public boolean isPermissionSet(String permission) {
        return currentInheritancePermissions.containsPermission(permission);
    }

    @Override
    public boolean isPermissionAssigned(String permission) {
        return currentPermissions.containsPermission(permission);
    }

    @Override
    public boolean hasPermission(String permission) {
        return currentInheritancePermissions.calculatePermission(permission).has();
    }

    @Override
    public PermissionResult hasPermissionExact(String permission) {
        PermissionAction result = currentInheritancePermissions.calculatePermission(permission);
        if(result == PermissionAction.NEUTRAL) return PermissionResult.NORMAL;
        else return result.has() ? PermissionResult.ALLOWED : PermissionResult.DENIED;
    }

    @Override
    public void setPermission(String permission, boolean action) {
        object.addPermission(DKPerms.getInstance().getScopeManager().getCurrentInstanceScope()
                ,permission,action? PermissionAction.ALLOW:PermissionAction.REJECT,-1);
    }

    @Override
    public void unsetPermission(String permission) {
        // object.removePermission(DKPerms.getInstance().getScopeManager().getCurrentInstanceScope(),permission);
    }

    @Override
    public void addGroup(String name) {
        PermissionObject group = DKPerms.getInstance().getObjectManager().getObject(name,null,null);
        // object.addGroup(DKPerms.getInstance().getScopeManager().getCurrentInstanceScope(),group,PermissionAction.ALLOW,-1);
    }

    @Override
    public void removeGroup(String name) {
        PermissionObject group = DKPerms.getInstance().getObjectManager().getObject(name,null,null);
        //object.removeGroup(group);
    }

    @Override
    public boolean isOperator() {//@Todo implement
        return currentMeta.isSet("operator",true);
    }

    @Override
    public void setOperator(boolean operator) {
        //object.getMeta().set("operator",operator,DKPerms.getInstance().getScopeManager().getCurrentInstanceScope());
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
}
