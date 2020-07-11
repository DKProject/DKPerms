/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 07.04.20, 20:12
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.migration;

public interface PermissionMigration extends MigrationExecutor {

    String getDisplayName();

    String getName();

    boolean isAvailable();

}
