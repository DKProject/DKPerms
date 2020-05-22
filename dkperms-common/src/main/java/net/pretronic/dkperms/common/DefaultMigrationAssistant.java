/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 21.05.20, 15:07
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common;

import net.pretronic.dkperms.api.migration.MigrationAssistant;
import net.pretronic.dkperms.api.migration.PermissionMigration;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultMigrationAssistant implements MigrationAssistant {

    private final Collection<PermissionMigration> migrations;

    public DefaultMigrationAssistant() {
        this.migrations = new ArrayList<>();
    }

    @Override
    public Collection<PermissionMigration> getMigrations() {
        return migrations;
    }

    @Override
    public PermissionMigration getMigration(String name){
        return Iterators.findOne(this.migrations, migration -> migration.getName().equalsIgnoreCase(name));
    }

    @Override
    public void registerMigration(PermissionMigration migration){
        Validate.notNull(migration);
        this.migrations.add(migration);
    }

    @Override
    public void unregisterMigration(PermissionMigration migration){
        Validate.notNull(migration);
        this.migrations.remove(migration);
    }

}
