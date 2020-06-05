/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 24.05.20, 13:58
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.permission;

import net.pretronic.dkperms.api.entity.Entity;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.permission.analyse.PermissionAnalyseResult;
import net.pretronic.libraries.utility.Validate;

import java.util.List;

public class DefaultPermissionCheckResult<T extends Entity> implements PermissionAnalyseResult<T> {

    private final PermissionAction result;
    private final List<T> entries;

    public DefaultPermissionCheckResult(PermissionAction result, List<T> entries) {
        Validate.notNull(result,entries);
        this.result = result;
        this.entries = entries;
    }

    @Override
    public PermissionAction getFinalAction() {
        return result;
    }

    @Override
    public List<T> getEntries() {
        return entries;
    }
}
