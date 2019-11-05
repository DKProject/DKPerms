/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 02.11.19, 13:25
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.object;

import net.pretronic.dkperms.api.context.PermissionContext;
import net.pretronic.dkperms.api.context.PermissionContextAssignment;
import net.pretronic.dkperms.api.entity.PermissionContextEntity;
import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.entity.PermissionGroupEntity;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.object.group.PermissionGroupOrder;
import net.pretronic.dkperms.api.object.meta.ObjectMeta;
import net.pretronic.dkperms.api.permission.PermissionCheck;
import net.pretronic.dkperms.api.permission.analyse.PermissionAnalyse;
import net.pretronic.dkperms.api.permission.analyse.track.PermissionTrackResult;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.common.object.meta.DefaultObjectMeta;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class DefaultPermissionObject implements PermissionObject {

    private final int id;
    private String name;
    private String displayName;
    private String description;

    private PermissionObjectType type;
    private PermissionScope scope;
    private final ObjectMeta meta;

    public DefaultPermissionObject(int id, String name, String displayName, String description) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.description = description;

        this.meta = new DefaultObjectMeta(this);
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
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPath() {
        return scope.getPath()+"\\"+name ;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public PermissionObjectType getType() {
        return type;
    }

    @Override
    public void setType(PermissionObjectType type) {

    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void setPriority(int priority) {

    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public void setDisabled(boolean disabled) {

    }

    @Override
    public PermissionScope getCurrentScope() {
        return null;
    }

    @Override
    public PermissionScope getScope() {
        return scope;
    }

    @Override
    public void setScope(PermissionScope scope) {

    }

    @Override
    public ObjectMeta getMeta() {
        return meta;
    }

    @Override
    public Collection<PermissionGroupEntity> getAllGroups() {
        return null;
    }

    @Override
    public Collection<PermissionGroupEntity> getGroups() {
        return null;
    }

    @Override
    public Collection<PermissionGroupEntity> getGroups(PermissionScope scope) {
        return null;
    }

    @Override
    public Collection<PermissionGroupEntity> getInheritanceGroups(PermissionScope scope) {
        return null;
    }

    @Override
    public PermissionGroupEntity addGroup(PermissionScope scope, PermissionObject group, long timeout) {
        return null;
    }

    @Override
    public PermissionGroupEntity addGroup(PermissionScope scope, PermissionObject group, long timeout, PermissionContextAssignment... context) {
        return null;
    }

    @Override
    public PermissionGroupEntity addGroup(PermissionScope scope, PermissionObject group, long duration, TimeUnit unit) {
        return null;
    }

    @Override
    public PermissionGroupEntity addGroup(PermissionScope scope, PermissionObject group, long duration, TimeUnit unit, PermissionContextAssignment... context) {
        return null;
    }

    @Override
    public boolean removeGroup(PermissionScope scope, PermissionObject group) {
        return false;
    }

    @Override
    public boolean clearGroups(PermissionObject scope) {
        return false;
    }

    @Override
    public boolean clearAllGroups() {
        return false;
    }

    @Override
    public boolean isInGroup(PermissionObject group) {
        return false;
    }

    @Override
    public boolean isInGroup(PermissionScope scope, PermissionObject group) {
        return false;
    }

    @Override
    public boolean isInInheritanceGroup(PermissionScope scope, PermissionObject group) {
        return false;
    }

    @Override
    public boolean promote(PermissionScope scope) {
        return false;
    }

    @Override
    public boolean promote(PermissionScope scope, PermissionGroupOrder order) {
        return false;
    }

    @Override
    public boolean demote(PermissionScope scope) {
        return false;
    }

    @Override
    public boolean demote(PermissionScope scope, PermissionGroupOrder order) {
        return false;
    }

    @Override
    public Collection<PermissionEntity> getAllPermissions() {
        return null;
    }

    @Override
    public Collection<PermissionEntity> getPermissions() {
        return null;
    }

    @Override
    public Collection<PermissionEntity> getPermissions(PermissionScope scope) {
        return null;
    }

    @Override
    public Collection<PermissionEntity> getInheritancePermissions(PermissionScope scope) {
        return null;
    }

    @Override
    public boolean hasPermission(String permission) {
        return false;
    }

    @Override
    public boolean hasPermission(PermissionScope scope, String permission) {
        return false;
    }

    @Override
    public boolean hasRangePermission(String basePermission, int range) {
        return false;
    }

    @Override
    public boolean hasRangePermission(PermissionScope scope, String basePermission, int range) {
        return false;
    }

    @Override
    public PermissionCheck hasPermission() {
        return null;
    }

    @Override
    public PermissionEntity addPermission(PermissionScope scope, String permission, long timeout) {
        return null;
    }

    @Override
    public PermissionEntity addPermission(PermissionScope scope, String permission, long timeout, PermissionContextAssignment... contexts) {
        return null;
    }

    @Override
    public PermissionEntity addPermission(PermissionScope scope, String permission, long time, TimeUnit duration) {
        return null;
    }

    @Override
    public PermissionEntity addPermission(PermissionScope scope, String permission, long time, TimeUnit duration, PermissionContextAssignment... contexts) {
        return null;
    }

    @Override
    public boolean removePermission(PermissionScope scope, String permission) {
        return false;
    }

    @Override
    public boolean clearPermission(PermissionScope scope) {
        return false;
    }

    @Override
    public boolean clearAllPermission() {
        return false;
    }

    @Override
    public Collection<PermissionContextEntity> getAllContexts() {
        return null;
    }

    @Override
    public Collection<PermissionContextEntity> getContexts() {
        return null;
    }

    @Override
    public Collection<PermissionContextEntity> getContexts(PermissionScope scope) {
        return null;
    }

    @Override
    public Collection<PermissionContextEntity> getInheritanceContexts(PermissionScope scope) {
        return null;
    }

    @Override
    public PermissionContextEntity setContext(PermissionScope scope, PermissionContext context, String value, long timeout) {
        return null;
    }

    @Override
    public PermissionContextEntity setContext(PermissionScope scope, PermissionContext context, String value, long time, TimeUnit duration) {
        return null;
    }

    @Override
    public boolean removeContext(PermissionScope scope, PermissionContext context) {
        return false;
    }

    @Override
    public boolean clearContexts() {
        return false;
    }

    @Override
    public boolean clearContexts(PermissionScope scope) {
        return false;
    }

    @Override
    public boolean hasContext(PermissionScope scope, PermissionContext context) {
        return false;
    }

    @Override
    public boolean hasContext(PermissionScope scope, PermissionContext context, Object value) {
        return false;
    }

    @Override
    public boolean hasInheritanceContext(PermissionScope scope, PermissionContext context) {
        return false;
    }

    @Override
    public boolean hasInheritanceContext(PermissionScope scope, PermissionContext context, Object value) {
        return false;
    }

    @Override
    public PermissionTrackResult trackPermission(String permission) {
        return null;
    }

    @Override
    public PermissionTrackResult trackGroup(PermissionObject group) {
        return null;
    }

    @Override
    public PermissionAnalyse startAnalyse() {
        return null;
    }

    @Override
    public PermissionObject clone(String newName) {
        return null;
    }

    @Override
    public <N extends PermissionObject> N getAs(Class<N> aClass) {
        return null;
    }

    private class CacheEntry {

        private int scopeId;
        private Collection<PermissionGroupEntity> entities;
        private Collection<PermissionGroupEntity> groups;

    }
}
