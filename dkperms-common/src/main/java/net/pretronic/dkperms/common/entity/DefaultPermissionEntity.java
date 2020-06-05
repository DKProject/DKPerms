/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 09.11.19, 15:28
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.entity;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.common.calculator.PermissionCalculator;
import net.pretronic.dkperms.common.object.DefaultPermissionObject;
import net.pretronic.libraries.utility.StringUtil;
import net.pretronic.libraries.utility.Validate;

public class DefaultPermissionEntity implements PermissionEntity {

    private final PermissionObject owner;
    private final int id;
    private final String[] nodes;

    private PermissionAction action;
    private PermissionScope scope;
    private long timeout;

    public DefaultPermissionEntity(PermissionObject owner,int id, String permission, PermissionAction action, PermissionScope scope, long timeout) {
        Validate.notNull(owner,permission,action,scope);
        this.owner = owner;
        this.id = id;
        this.action = action;
        this.scope = scope;
        this.timeout = timeout;
        this.nodes = StringUtil.split(permission,'.');
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public PermissionObject getOwner(){
        return owner;
    }

    @Override
    public String[] getNodes() {
        return nodes;
    }

    @Override
    public String getPermission() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < nodes.length; i++) {
            if(i != 0) builder.append('.');
            builder.append(nodes[i]);
        }
        return builder.toString();
    }

    @Override
    public PermissionAction check(String permission) {
        Validate.notNull(permission);
        return check(StringUtil.split(permission,'.'));
    }

    @Override
    public PermissionAction check(String[] nodes) {
        Validate.notNull((Object) nodes);
        boolean result = PermissionCalculator.compare(this.nodes,nodes);
        return result ? action : PermissionAction.NEUTRAL;
    }

    @Override
    public boolean matches(String[] nodes) {
        Validate.notNull((Object) nodes);
        if(nodes.length == this.nodes.length){
            for (int i = 0; i < nodes.length; i++) {
                if(!nodes[i].equalsIgnoreCase(this.nodes[i])) return false;
            }
        }
        return true;
    }

    @Override
    public PermissionAction getAction() {
        return action;
    }

    @Override
    public void setAction(PermissionObject executor, PermissionAction action) {
        DKPerms.getInstance().getStorage().getPermissionStorage()
                .updatePermissionTimeout(id,action.ordinal());
        this.action = action;
    }

    @Override
    public PermissionScope getScope() {
        return scope;
    }

    @Override
    public void setScope(PermissionObject executor, PermissionScope scope) {
        DKPerms.getInstance().getStorage().getPermissionStorage()
                .updatePermissionScope(id,scope.getId());
        this.scope = scope;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public void setTimeout(PermissionObject executor, long timeout) {
        DKPerms.getInstance().getStorage().getPermissionStorage()
                .updatePermissionTimeout(id,timeout);
        this.timeout = timeout;
    }

    @Override
    public void update(PermissionObject executor, PermissionAction action, PermissionScope scope, long timeout) {
        Validate.notNull(action,scope);//@Todo add executor

        DKPerms.getInstance().getStorage().getPermissionStorage()
                .updatePermission(id,scope.getId(),action,timeout);

        if(owner instanceof DefaultPermissionObject){
            ((DefaultPermissionObject) owner).synchronizePermissions(scope);
            if(this.scope != scope){
                ((DefaultPermissionObject) owner).synchronizePermissions(this.scope);
            }
        }

        this.action = action;
        this.scope = scope;
        this.timeout = timeout;
    }
}
