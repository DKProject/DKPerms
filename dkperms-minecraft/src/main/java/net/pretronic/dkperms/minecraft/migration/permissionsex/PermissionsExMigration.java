/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 23.05.20, 12:54
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.migration.permissionsex;

import net.pretronic.dkperms.api.migration.PermissionMigration;
import org.bukkit.Bukkit;

public class PermissionsExMigration implements PermissionMigration {

    @Override
    public String getDisplayName() {
        return "PermissionsEx";
    }

    @Override
    public String getName() {
        return "PermissionsEx";
    }

    @Override
    public boolean isAvailable() {
        return Bukkit.getPluginManager().getPlugin("PermissionsEx") != null;
    }

    @Override
    public boolean migrate() throws Exception {
        return new PermissionsExMigrationExecutor().migrate();
    }
}
