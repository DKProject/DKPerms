/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 18.01.20, 15:23
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.calculator;

import net.pretronic.dkperms.api.entity.ParentEntity;
import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.libraries.utility.Iterators;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class ParentCalculator {

    public static ParentEntity findBestParentEntity(Graph<ParentEntity> entities, PermissionObject parent){
        return findBestParentEntity(entities.traverse(),parent);
    }

    public static ParentEntity findBestParentEntity(Collection<ParentEntity> entities, PermissionObject parent){
        ParentEntity result = null;
        for (ParentEntity entity : entities) {
            if(entity.getParent().equals(parent)){
                PermissionAction action = entity.getAction();
                if(action != PermissionAction.NEUTRAL){
                    if(action == PermissionAction.REJECT_ALWAYS) return entity;
                    else if(action == PermissionAction.ALLOW && result != null && result.getAction() != PermissionAction.REJECT){
                        result = entity;
                    } else if(action == PermissionAction.REJECT && result != null && result.getAction() != PermissionAction.ALLOW_ALWAYS){
                        result = entity;
                    } else result = entity;
                }
            }
        }
        return result;
    }

    public static PermissionAction calculate(Graph<ParentEntity> permissions, PermissionObject parent){
        return calculate(permissions.traverse(),parent);
    }

    public static PermissionAction calculate(Collection<ParentEntity> permissions, PermissionObject parent){
        ParentEntity entity = findBestParentEntity(permissions,parent);
        return entity != null ? entity.getAction() : PermissionAction.NEUTRAL;
    }

    public static boolean containsParent(Graph<ParentEntity> entities, PermissionObject parent){
        for (ParentEntity entity : entities) {
            if(entity.getParent().equals(parent)) return true;
        }
        return false;
    }

    public static List<String> toStringList(Graph<PermissionEntity> permissions){
        return Iterators.map(permissions, PermissionEntity::getPermission);
    }

    public static void orderGroups(List<PermissionObject> groups){
        groups.sort(Comparator.comparingInt(PermissionObject::getPriority));
    }

    public static void orderEntities(List<ParentEntity> groups){
        groups.sort(Comparator.comparingInt(value -> value.getParent().getPriority()));
    }
}
