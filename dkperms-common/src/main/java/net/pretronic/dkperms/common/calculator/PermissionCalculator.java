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

import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.StringUtil;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.permission.PermissionAction;

import java.util.Collection;
import java.util.List;

public class PermissionCalculator {

    public static PermissionAction calculate(Graph<PermissionEntity> permissions,String permission){
        return calculate(permissions.traverse(),permission);
    }

    public static PermissionAction calculate(Collection<PermissionEntity> permissions, String permission){
        String[] nodes = StringUtil.split(permission,'.');
        PermissionAction result = PermissionAction.NEUTRAL;
        for (PermissionEntity entity : permissions) {
            PermissionAction action = entity.check(nodes);
            if(action != PermissionAction.NEUTRAL){
                if(action == PermissionAction.REJECT_ALWAYS) return action;
                else if(action == PermissionAction.ALLOW && result != PermissionAction.REJECT) result = action;
                else if(action == PermissionAction.REJECT && result != PermissionAction.ALLOW_ALWAYS) result = action;
                else result = action;
            }
        }
        return result;
    }

    public static boolean containsPermission(Graph<PermissionEntity> permissions,String permission){
        String[] nodes = StringUtil.split(permission,'.');
        for (PermissionEntity entity : permissions) {
            if(entity.matches(nodes)) return true;
        }
        return false;
    }

    public static List<String> toStringList(Graph<PermissionEntity> permissions){
        return Iterators.map(permissions, PermissionEntity::getPermission);
    }

}
