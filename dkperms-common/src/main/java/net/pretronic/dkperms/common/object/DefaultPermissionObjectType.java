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

import net.prematic.libraries.utility.concurrent.AsyncExecutor;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObjectFactory;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.storage.DKPermsStorage;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DefaultPermissionObjectType implements PermissionObjectType {

    private final int id;
    private String name;

    private PermissionObjectFactory factory;

    public DefaultPermissionObjectType(int id, String name) {
        this.id = id;
        this.name = name;
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
    public PermissionObjectFactory getLocalFactory() {
        return factory;
    }

    @Override
    public void setLocalFactory(PermissionObjectFactory factory) {
        this.factory = factory;
    }
}
