/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.permission.analyse;

import net.prematic.libraries.utility.interfaces.ObjectOwner;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.permission.analyse.track.PermissionTrackResult;

public interface PermissionRequest {

    ObjectOwner getExecutor();

    String getType();

    String getValue();


    //PermissionUser getTarget();

    String getPermission();

    PermissionAction getAction();

    String getStackTrace();

    PermissionTrackResult track();

}
