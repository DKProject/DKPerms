/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.context;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.context.PermissionContext;
import net.pretronic.dkperms.api.context.PermissionContextAssignment;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class DefaultPermissionContext implements PermissionContext {

    private final int id;
    private String name;

    public DefaultPermissionContext(int id, String name) {
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
        DKPerms.getInstance().getStorage().getContextStorage().updateContextName(this.id,name);
        this.name = name;
    }

    @Override
    public CompletableFuture<Void> renameAsync(String name) {
        Objects.requireNonNull(name,"Name can't be null");
        return DKPerms.getInstance().getExecutor().executeVoid(() -> rename(name));
    }

    @Override
    public void delete() {
        DKPerms.getInstance().getStorage().getContextStorage().deleteContext(this.id);
    }

    @Override
    public CompletableFuture<Void> deleteAsync() {
        return DKPerms.getInstance().getExecutor().executeVoid(this::delete);
    }

    @Override
    public PermissionContextAssignment assign(Object valueDKPerms) {
        return null;
    }
}
