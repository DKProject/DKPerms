/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.permission.analyse.track;

import net.pretronic.dkperms.api.permission.PermissionAction;

import java.util.List;

public interface PermissionTrackResult {

    PermissionAction getFinalAction();

    List<PermissionTrackEntry> getEntries();



}
