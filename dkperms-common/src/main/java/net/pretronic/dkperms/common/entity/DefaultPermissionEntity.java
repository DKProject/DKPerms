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

import net.pretronic.libraries.utility.StringUtil;
import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;

public class DefaultPermissionEntity implements PermissionEntity {

    private final static String ALL = "*";

    private final int id;
    private final String[] nodes;

    private PermissionAction action;
    private PermissionScope scope;
    private long timeout;

    public DefaultPermissionEntity(int id, String permission, PermissionAction action, PermissionScope scope, long timeout) {
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
        return check(StringUtil.split(permission,'.'));
    }

    @Override
    public PermissionAction check(String[] nodes) {
        if(this.nodes.length < nodes.length){
            for (int i = 0; i < this.nodes.length; i++) {
                if(this.nodes[i].equals(ALL)) return action;
                else{
                    if(!this.nodes[i].equalsIgnoreCase(nodes[i])){
                        return PermissionAction.NEUTRAL;
                    }
                }
            }
            return action;
        }
        return PermissionAction.NEUTRAL;
    }

    @Override
    public boolean matches(String[] nodes) {
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
    public void updateAction(PermissionObject executor, PermissionAction action) {

    }

    @Override
    public PermissionScope getScope() {
        return scope;
    }

    @Override
    public void updateScope(PermissionObject executor, PermissionScope scope) {

    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public void updateTimeout(PermissionObject executor, long timeout) {

    }

    @Override
    public void remove(PermissionObject executor) {

    }


}
