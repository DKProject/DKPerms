/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 24.04.20, 11:47
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.graph;

import net.pretronic.dkperms.api.entity.ParentEntity;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.permission.analyse.PermissionAnalyseResult;

public interface ParentGraph extends Graph<ParentEntity> {

    ParentEntity getParent(PermissionObject group);

    boolean containsGroup(PermissionObject group);

    PermissionAction calculateGroup(PermissionObject group);

    PermissionAnalyseResult<ParentEntity> analyze(PermissionObject group);

}
