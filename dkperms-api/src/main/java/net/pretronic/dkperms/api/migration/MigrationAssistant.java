/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 07.04.20, 20:13
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.migration;

import java.util.Collection;

public interface MigrationAssistant {

    Collection<PermissionMigration> getMigrations();

    PermissionMigration getMigration(String name);

    void registerMigration(PermissionMigration migration);

    void unregisterMigration(PermissionMigration migration);

}
