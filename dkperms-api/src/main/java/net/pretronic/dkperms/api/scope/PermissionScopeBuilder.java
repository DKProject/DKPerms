/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.scope;

import java.util.concurrent.CompletableFuture;

public interface PermissionScopeBuilder {

    PermissionScopeBuilder of(String type, String value);

    PermissionScope build();

    CompletableFuture<PermissionScope> buildAsync();

}
