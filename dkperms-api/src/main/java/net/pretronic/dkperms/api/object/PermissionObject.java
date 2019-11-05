/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.object;

import net.prematic.libraries.utility.interfaces.Castable;
import net.pretronic.dkperms.api.context.PermissionContext;
import net.pretronic.dkperms.api.context.PermissionContextAssignment;
import net.pretronic.dkperms.api.entity.PermissionContextEntity;
import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.entity.PermissionGroupEntity;
import net.pretronic.dkperms.api.object.group.PermissionGroupOrder;
import net.pretronic.dkperms.api.object.meta.ObjectMeta;
import net.pretronic.dkperms.api.permission.PermissionCheck;
import net.pretronic.dkperms.api.permission.analyse.PermissionAnalyse;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.permission.analyse.track.PermissionTrackResult;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public interface PermissionObject extends Castable<PermissionObject>{

    int getId();


    String getName();

    void setName(String name);

    String getPath();


    String getDisplayName();

    void setDisplayName(String displayName);


    String getDescription();

    void setDescription(String description);


    PermissionObjectType getType();

    void setType(PermissionObjectType type);


    int getPriority();

    void setPriority(int priority);


    boolean isDisabled();

    void setDisabled(boolean disabled);


    PermissionScope getCurrentScope();

    PermissionScope getScope();

    void setScope(PermissionScope scope);

    ObjectMeta getMeta();


    Collection<PermissionGroupEntity> getAllGroups();

    Collection<PermissionGroupEntity> getGroups();

    Collection<PermissionGroupEntity> getGroups(PermissionScope scope);

    Collection<PermissionGroupEntity> getInheritanceGroups(PermissionScope scope);

    PermissionGroupEntity addGroup(PermissionScope scope, PermissionObject group, long timeout);

    PermissionGroupEntity addGroup(PermissionScope scope, PermissionObject group, long timeout,PermissionContextAssignment... context);

    PermissionGroupEntity addGroup(PermissionScope scope, PermissionObject group, long duration, TimeUnit unit);

    PermissionGroupEntity addGroup(PermissionScope scope, PermissionObject group, long duration, TimeUnit unit,PermissionContextAssignment... context);

    boolean removeGroup(PermissionScope scope, PermissionObject group);

    boolean clearGroups(PermissionObject scope);

    boolean clearAllGroups();

    boolean isInGroup(PermissionObject group);

    boolean isInGroup(PermissionScope scope, PermissionObject group);

    boolean isInInheritanceGroup(PermissionScope scope, PermissionObject group);


    boolean promote(PermissionScope scope);

    boolean promote(PermissionScope scope, PermissionGroupOrder order);

    boolean demote(PermissionScope scope);

    boolean demote(PermissionScope scope, PermissionGroupOrder order);


    Collection<PermissionEntity> getAllPermissions();

    Collection<PermissionEntity> getPermissions();

    Collection<PermissionEntity> getPermissions(PermissionScope scope);

    Collection<PermissionEntity> getInheritancePermissions(PermissionScope scope);


    boolean hasPermission(String permission);

    boolean hasPermission(PermissionScope scope, String permission);

    boolean hasRangePermission(String basePermission, int range);

    boolean hasRangePermission(PermissionScope scope, String basePermission, int range);

    PermissionCheck hasPermission();


    PermissionEntity addPermission(PermissionScope scope, String permission, long timeout);

    PermissionEntity addPermission(PermissionScope scope, String permission, long timeout, PermissionContextAssignment... contexts);

    PermissionEntity addPermission(PermissionScope scope, String permission, long time, TimeUnit duration);

    PermissionEntity addPermission(PermissionScope scope, String permission, long time, TimeUnit duration, PermissionContextAssignment... contexts);

    boolean removePermission(PermissionScope scope, String permission);

    boolean clearPermission(PermissionScope scope);

    boolean clearAllPermission();


    Collection<PermissionContextEntity> getAllContexts();

    Collection<PermissionContextEntity> getContexts();

    Collection<PermissionContextEntity> getContexts(PermissionScope scope);

    Collection<PermissionContextEntity> getInheritanceContexts(PermissionScope scope);

    PermissionContextEntity setContext(PermissionScope scope,PermissionContext context, String value, long timeout);

    PermissionContextEntity setContext(PermissionScope scope,PermissionContext context, String value, long time, TimeUnit duration);

    boolean removeContext(PermissionScope scope,PermissionContext context);

    boolean clearContexts();

    boolean clearContexts(PermissionScope scope);

    boolean hasContext(PermissionScope scope,PermissionContext context);

    boolean hasContext(PermissionScope scope,PermissionContext context,Object value);

    boolean hasInheritanceContext(PermissionScope scope,PermissionContext context);

    boolean hasInheritanceContext(PermissionScope scope,PermissionContext context,Object value);


    PermissionTrackResult trackPermission(String permission);

    PermissionTrackResult trackGroup(PermissionObject group);

    PermissionAnalyse startAnalyse();


    default PermissionObject copy(String newName){
        return clone(newName);
    }

    PermissionObject clone(String newName);
}
