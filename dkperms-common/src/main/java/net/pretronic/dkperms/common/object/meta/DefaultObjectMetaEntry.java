/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 04.05.20, 18:36
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.object.meta;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.SyncAction;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.common.object.DefaultPermissionObject;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.Convert;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.Validate;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DefaultObjectMetaEntry implements ObjectMetaEntry {

    private final PermissionObject owner;
    private final int id;
    private final String key;
    private PermissionScope scope;
    private Object value;
    private int priority;
    private long timeout;

    public DefaultObjectMetaEntry(PermissionObject owner, PermissionScope scope, int id, String key, Object value,int priority,long timeout) {
        Validate.notNull(owner,scope,id,key,value);
        this.owner = owner;
        this.id = id;
        this.key = key;
        this.scope = scope;
        this.value = value;
        this.priority = priority;
        this.timeout = timeout;
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
    public int getPriority() {
        return priority;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public void setTimeout(PermissionObject executor, long timeout) {
        this.timeout = timeout;
        DKPerms.getInstance().getStorage().getObjectStorage().updateMetaTimeout(id,timeout);
        executeMetaSync(scope);
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
    public void setScope(PermissionObject executor, PermissionScope scope) {
        Validate.notNull(scope);
        if(!scope.isSaved()) scope.insert();
        DKPerms.getInstance().getStorage().getObjectStorage().updateMetaScope(id,scope.getId());
        executeMetaSync(scope);
        executeMetaSync(this.scope);
        this.scope = scope;
    }

    @Override
    public void setValue(PermissionObject executor,Object value) {
        Objects.requireNonNull(value,"Value can't be null");
        DKPerms.getInstance().getStorage().getObjectStorage().updateMetaValue(id,value.toString());
        this.value = value;
        executeMetaSync(scope);
    }

    @Override
    public CompletableFuture<Void> setValueAsync(PermissionObject executor,Object value) {
        return DKPerms.getInstance().getExecutor().executeVoid(() -> setValue(executor,value));
    }

    @Override
    public void setPriority(PermissionObject executor, int priority) {
        DKPerms.getInstance().getStorage().getObjectStorage().updateObjectPriority(id,priority);
        this.priority = priority;
        executeMetaSync(scope);
    }

    @Override
    public CompletableFuture<Void> setPriorityAsync(PermissionObject executor, int priority) {
        return DKPerms.getInstance().getExecutor().executeVoid(() -> setPriority(executor,priority));
    }

    @Override
    public void update(PermissionObject executor, Object value, int priority, PermissionScope scope, long timeout) {
        Validate.notNull(value,scope);
        DKPerms.getInstance().getStorage().getObjectStorage().updateMeta(id,scope.getId(),value.toString(),priority,timeout);
        executeMetaSync(scope);
        if(scope != this.scope) executeMetaSync(this.scope);

        this.value = value;
        this.scope = scope;
        this.priority = priority;
        this.timeout = timeout;
    }

    private void executeMetaSync(PermissionScope scope){
        if(owner instanceof DefaultPermissionObject){
            Document data = Document.newDocument();
            if(scope != null) data.set("scope",scope.getId());
            ((DefaultPermissionObject)owner).executeSynchronisationUpdate(SyncAction.OBJECT_META_UPDATE,data);
        }
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
}
