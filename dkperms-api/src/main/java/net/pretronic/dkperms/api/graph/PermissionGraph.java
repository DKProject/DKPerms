/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 18.01.20, 22:37
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.graph;

import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.permission.analyse.PermissionAnalyseResult;

import java.util.List;

public interface PermissionGraph extends Graph<PermissionEntity> {

    PermissionEntity getPermission(String permission);

    boolean containsPermission(String permission);

    PermissionAction calculatePermission(String permission);

    List<String> toStringList();

    PermissionAnalyseResult<PermissionEntity> analyze(String permission);

}
