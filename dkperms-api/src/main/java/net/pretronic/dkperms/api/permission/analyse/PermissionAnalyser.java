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

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public interface PermissionAnalyser {

    Collection<String> getIncludedPermissions();

    Collection<String> getExcludedPermissions();

    void addIncludedPermission(String permission);

    void removeIncludedPermission(String permission);

    void addExcludedPermission(String permission);

    void removeExcludedPermission(String permission);

    boolean canPermissionBeAnalysed(String permission);


    List<PermissionRequest> geRequests();

    void offerRequest(PermissionRequest request);



    void addListener(Consumer<PermissionRequest> request);

    void removeListener(Object request);

    boolean hasListener(Object request);


    boolean isEnabled();

    void enable();

    void disable();

}
