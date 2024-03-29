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

import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.StringUtil;

import java.util.Collection;
import java.util.List;

public class PermissionCalculator {

    private final static String ALL = "*";

    public static PermissionEntity findBestPermissionEntity(Graph<PermissionEntity> permissions,String permission){
        return findBestPermissionEntity(permissions.traverse(),permission);
    }

    public static PermissionEntity findBestPermissionEntity(Collection<PermissionEntity> permissions, String permission){
        String[] nodes = StringUtil.split(permission,'.');
        PermissionEntity result = null;
        for (PermissionEntity entity : permissions) {
            if(!entity.hasTimeout()){
                PermissionAction action = entity.check(nodes);
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

    public static PermissionAction calculate(Graph<PermissionEntity> permissions,String permission){
        return calculate(permissions.traverse(),permission);
    }

    public static PermissionAction calculate(Collection<PermissionEntity> permissions, String permission){
        PermissionEntity entity = findBestPermissionEntity(permissions,permission);
        return entity != null ? entity.getAction() : PermissionAction.NEUTRAL;
    }

    public static boolean compare(String[] nodes1, String[] nodes2){
        if(nodes1.length == nodes2.length){
            for (int i = 0; i < nodes1.length; i++) {
                if(nodes1[i].equals(ALL)) return true;
                else if(!nodes1[i].equalsIgnoreCase(nodes2[i])){
                    return false;
                }
            }
            return true;
        }else if(nodes1.length < nodes2.length){
            for (int i = 0; i < nodes1.length; i++) {
                if(nodes1[i].equals(ALL)) return true;
                else if(!nodes1[i].equalsIgnoreCase(nodes2[i])){
                    return false;
                }
            }
        }
        return false;
    }

    public static boolean containsPermission(Graph<PermissionEntity> permissions,String permission){
        String[] nodes = StringUtil.split(permission,'.');
        for (PermissionEntity entity : permissions) {
            if(!entity.hasTimeout() && entity.matches(nodes)) return true;
        }
        return false;
    }

    public static List<String> toStringList(Graph<PermissionEntity> permissions){
        return Iterators.map(permissions, PermissionEntity::getPermission, entity -> !entity.hasTimeout());
    }

}
