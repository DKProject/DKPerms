/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 24.05.20, 13:48
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.permission.analyse;

import net.pretronic.dkperms.api.entity.Entity;
import net.pretronic.dkperms.api.permission.PermissionAction;

import java.util.List;

public interface PermissionAnalyseResult<T extends Entity> {

    PermissionAction getFinalAction();

    List<T> getEntries();



}
