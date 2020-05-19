/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 02.11.19, 14:05
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.object;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionHolderFactory;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.libraries.message.bml.variable.describer.VariableObjectToString;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DefaultPermissionObjectType implements PermissionObjectType, VariableObjectToString {

    private final int id;
    private final boolean parentAble;
    private String name;
    private String displayName;
    private PermissionHolderFactory holderFactory;

    public DefaultPermissionObjectType(int id, String name,String displayName, boolean parentAble) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.parentAble = parentAble;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void rename(String name, String displayName) {
        Objects.requireNonNull(name,"Name can't be null");
        DKPerms.getInstance().getStorage().getObjectStorage().updateObjectType(this.id,name,displayName);
        this.name = name;
    }

    @Override
    public CompletableFuture<Void> renameAsync(String name, String displayName) {
        Objects.requireNonNull(name,"Name can't be null");
        return DKPerms.getInstance().getExecutor().executeVoid(() -> rename(name,displayName));
    }

    @Override
    public boolean isParentAble() {
        return parentAble;
    }

    @Override
    public boolean hasLocalHolderFactory() {
        return holderFactory != null;
    }

    @Override
    public PermissionHolderFactory getLocalHolderFactory() {
        return holderFactory;
    }

    @Override
    public void setLocalHolderFactory(PermissionHolderFactory factory) {
        holderFactory = factory;
    }

    @Override
    public String toStringVariable() {
        return name;
    }
}
