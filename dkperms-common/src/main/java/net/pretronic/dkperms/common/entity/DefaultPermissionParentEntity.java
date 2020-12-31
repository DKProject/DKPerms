/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 09.11.19, 16:49
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.entity;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.entity.ParentEntity;
import net.pretronic.dkperms.api.logging.LogType;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.common.object.DefaultPermissionObject;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.utility.Validate;

public class DefaultPermissionParentEntity implements ParentEntity {

    private final PermissionObject owner;
    private final int id;
    private final PermissionObject group;

    private PermissionAction action;
    private PermissionScope scope;
    private long timeout;

    public DefaultPermissionParentEntity(PermissionObject owner, int id, PermissionObject group, PermissionAction action, PermissionScope scope, long timeout) {
        Validate.notNull(owner,group,action,scope);
        this.owner = owner;
        this.id = id;
        this.group = group;
        this.action = action;
        this.scope = scope;
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
    public PermissionAction getAction() {
        return action;
    }

    @Override
    public void setAction(PermissionObject executor, PermissionAction action) {
        Validate.notNull(action);
        DKPerms.getInstance().getStorage().getParentStorage().updateParentReferenceAction(this.id,action);
        DKPerms.getInstance().getAuditLog().createUpdateRecordAsync(executor, LogType.ENTITY_PERMISSION,owner,this,"action",this.action.ordinal(),action.ordinal());
        this.action = action;
    }

    @Override
    public PermissionScope getScope() {
        return scope;
    }

    @Override
    public void setScope(PermissionObject executor, PermissionScope scope) {
        Validate.notNull(scope);
        if(!scope.isSaved()) scope.insert();
        DKPerms.getInstance().getStorage().getParentStorage().updateParentReferenceScope(this.id,scope.getId());
        DKPerms.getInstance().getAuditLog().createUpdateRecordAsync(executor, LogType.ENTITY_PERMISSION,owner,this,"scope",this.scope.getId(),scope.getId());
        this.scope = scope;
    }


    @Override
    public PermissionObject getParent() {
        return group;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public void setTimeout(PermissionObject executor, long timeout) {
        DKPerms.getInstance().getStorage().getParentStorage().updateParentReferenceTimeout(this.id,timeout);
        DKPerms.getInstance().getAuditLog().createUpdateRecordAsync(executor, LogType.ENTITY_PERMISSION,owner,this,"timeout",this.timeout,timeout);
        this.timeout = timeout;
    }

    @Override
    public void update(PermissionObject executor, PermissionAction action, PermissionScope scope, long timeout) {
        Validate.notNull(action,scope);
        if(!scope.isSaved()) scope.insert();
        DKPerms.getInstance().getStorage().getParentStorage().updateParentReference(this.id,scope.getId(),action,timeout);

        if(owner instanceof DefaultPermissionObject){
            ((DefaultPermissionObject) owner).synchronizeGroups(scope);
            if(this.scope != scope){
                ((DefaultPermissionObject) owner).synchronizeGroups(this.scope);
            }
        }

        DKPerms.getInstance().getAuditLog().createUpdateRecordAsync(executor, LogType.ENTITY_PERMISSION,owner,this,"scope",this.scope.getId(),scope.getId());
        DKPerms.getInstance().getAuditLog().createUpdateRecordAsync(executor, LogType.ENTITY_PERMISSION,owner,this,"action",this.action.ordinal(),action.ordinal());
        DKPerms.getInstance().getAuditLog().createUpdateRecordAsync(executor, LogType.ENTITY_PERMISSION,owner,this,"timeout",this.timeout,timeout);

        this.scope = scope;
        this.action = action;
        this.timeout = timeout;
    }

    @Override
    public Document serializeRecord() {
        Document document = Document.newDocument();
        document.set("parentId",this.group.getId());
        document.set("timeout",timeout);
        document.set("action",this.action.ordinal());
        document.set("scope",this.scope.getId());
        return document;
    }

}
