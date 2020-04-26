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

import net.pretronic.dkperms.api.object.SyncAction;
import net.pretronic.dkperms.common.object.DefaultPermissionObject;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.Convert;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.Validate;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DefaultObjectMetaEntry implements ObjectMetaEntry {

    private final PermissionObject owner;
    private final int id;
    private final String key;
    private final PermissionScope scope;
    private Object value;

    public DefaultObjectMetaEntry(PermissionObject owner,PermissionScope scope,int id, String key, Object value) {
        Validate.notNull(owner,scope,id,key,value);
        this.owner = owner;
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
        return owner;
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
        if(this.value == value) return true;
        if(this.value.equals(value)) return true;
        else if(this.value instanceof String){
            String stringValue = this.value.toString();
            if(value instanceof Byte){
                return GeneralUtil.isNaturalNumber(stringValue) && Byte.parseByte(stringValue) == (byte)value;
            }else if(value instanceof Integer){
                return GeneralUtil.isNaturalNumber(stringValue) && Integer.parseInt(stringValue) == (int)value;
            }else if(value instanceof Long){
                return GeneralUtil.isNaturalNumber(stringValue) && Long.parseLong(stringValue) == (long)value;
            }else if(value instanceof Double){
                return GeneralUtil.isNumber(stringValue) && Double.parseDouble(stringValue) == (double)value;
            }else if(value instanceof Boolean){
                return (stringValue.equalsIgnoreCase("true") && (boolean)value)
                        || (stringValue.equalsIgnoreCase("false") && !(boolean) value);
            }
        }
        return false;
    }

    @Override
    public void setValue(PermissionObject executor,Object value) {
        Objects.requireNonNull(value,"Value can't be null");
        DKPerms.getInstance().getStorage().getObjectStorage().updateMeta(id,value.toString());
        this.value = value;

        if(owner instanceof DefaultPermissionObject){
            Document data = Document.newDocument();
            if(scope != null) data.set("scope",scope.getId());
            ((DefaultPermissionObject)owner).executeSynchronisationUpdate(SyncAction.OBJECT_META_UPDATE,data);
        }
    }

    @Override
    public CompletableFuture<Void> setValueAsync(PermissionObject executor,Object value) {
        return DKPerms.getInstance().getExecutor().executeVoid(() -> setValue(executor,value));
    }
}
