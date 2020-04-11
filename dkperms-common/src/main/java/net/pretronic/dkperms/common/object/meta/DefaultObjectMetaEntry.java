/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 02.11.19, 15:05
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.object.meta;

import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.Convert;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.api.scope.PermissionScope;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DefaultObjectMetaEntry implements ObjectMetaEntry {

    private final int id;
    private final String key;
    private final PermissionScope scope;
    private Object value;

    public DefaultObjectMetaEntry(PermissionScope scope,int id, String key, Object value) {
        this.id = id;
        this.key = key;
        this.scope = scope;
        this.value = value;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public PermissionObject getOwner() {
        throw new UnsupportedOperationException();//@Todo implement
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public PermissionScope getScope() {
        return scope;
    }

    @Override
    public String getValue() {
        return Convert.toString(value);
    }

    @Override
    public byte getByteValue() {
        return Convert.toByte(value);
    }

    @Override
    public int getIntValue() {
        return Convert.toInteger(value);
    }

    @Override
    public long getLongValue() {
        return Convert.toLong(value);
    }

    @Override
    public double getDoubleValue() {
        return Convert.toDouble(value);
    }

    @Override
    public float getFloatValue() {
        return Convert.toFloat(value);
    }

    @Override
    public boolean getBooleanValue() {
        return Convert.toBoolean(value);
    }

    @Override
    public Document getDocumentValue() {
        return DocumentFileType.JSON.getReader().read(getValue());
    }

    @Override
    public boolean equalsValue(Object value) {
        //@Todo better implementation
        return value.equals(value);
    }

    @Override
    public void setValue(Object value) {
        Objects.requireNonNull(value,"Value can't be null");
        DKPerms.getInstance().getStorage().getObjectStorage().updateMeta(id,value.toString());
        this.value = value;
    }

    @Override
    public CompletableFuture<Void> setValueAsync(Object value) {
        return DKPerms.getInstance().getExecutor().executeVoid(() -> setValue(value));
    }

    @Override
    public void delete() {

    }

    @Override
    public CompletableFuture<Void> deleteAsync() {
        return null;
    }
}
