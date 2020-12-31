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
import net.pretronic.dkperms.api.logging.LogType;
import net.pretronic.dkperms.api.object.PermissionHolderFactory;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.libraries.document.Document;
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
    public void rename(PermissionObject executor, String name, String displayName) {
        Objects.requireNonNull(name,"Name can't be null");
        DKPerms.getInstance().getStorage().getObjectStorage().updateObjectType(this.id,name,displayName);
        DKPerms.getInstance().getAuditLog().createUpdateRecordAsync(executor, LogType.ENTITY_META,null,this,"name",this.name,name);
        DKPerms.getInstance().getAuditLog().createUpdateRecordAsync(executor, LogType.ENTITY_META,null,this,"displayName",this.displayName,displayName);
        this.name = name;
        this.displayName = displayName;
    }

    @Override
    public CompletableFuture<Void> renameAsync(PermissionObject executor,String name, String displayName) {
        Objects.requireNonNull(name,"Name can't be null");
        return DKPerms.getInstance().getExecutor().executeVoid(() -> rename(executor,name,displayName));
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
        return displayName;
    }

    @Override
    public Document serializeRecord() {
        Document document = Document.newDocument();
        document.set("name",this.name);
        document.set("displayName",this.displayName);
        document.set("parentAble",this.parentAble);
        return document;
    }

}
