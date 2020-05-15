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
    private final boolean group;
    private String name;
    private PermissionHolderFactory holderFactory;

    public DefaultPermissionObjectType(int id, String name, boolean group) {
        this.id = id;
        this.name = name;
        this.group = group;
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
    public void rename(String name) {
        Objects.requireNonNull(name,"Name can't be null");
        DKPerms.getInstance().getStorage().getObjectStorage().updateObjectType(this.id,name);
        this.name = name;
    }

    @Override
    public CompletableFuture<Void> renameAsync(String name) {
        Objects.requireNonNull(name,"Name can't be null");
        return DKPerms.getInstance().getExecutor().executeVoid(() -> rename(name));
    }

    @Override
    public boolean isGroup() {
        return group;
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
