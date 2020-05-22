/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 18.05.20, 20:50
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.object;

import java.util.concurrent.CompletableFuture;

public class BuiltInPermissionObjectType implements PermissionObjectType{

    private final int fixedId;
    private final String name;
    private final String displayName;
    private final boolean parentAble;

    private PermissionHolderFactory factory;

    protected BuiltInPermissionObjectType(int fixedId, String name, String displayName, boolean parentAble) {
        this.fixedId = fixedId;
        this.name = name;
        this.displayName = displayName;
        this.parentAble = parentAble;
    }

    @Override
    public int getId() {
        return fixedId;
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
        throw new UnsupportedOperationException("This is not possible to rename a built in type");
    }

    @Override
    public CompletableFuture<Void> renameAsync(String name, String displayName) {
        throw new UnsupportedOperationException("This is not possible to rename a built in type");
    }

    @Override
    public boolean isParentAble() {
        return parentAble;
    }

    @Override
    public boolean hasLocalHolderFactory() {
        return factory != null;
    }

    @Override
    public PermissionHolderFactory getLocalHolderFactory() {
        return factory;
    }

    @Override
    public void setLocalHolderFactory(PermissionHolderFactory factory) {
        this.factory = factory;
    }
}
