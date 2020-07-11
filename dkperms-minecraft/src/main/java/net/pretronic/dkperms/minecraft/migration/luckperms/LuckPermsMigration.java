/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 30.05.20, 15:07
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.migration.luckperms;

import net.pretronic.dkperms.api.migration.PermissionMigration;
import org.bukkit.Bukkit;

public class LuckPermsMigration implements PermissionMigration {

    @Override
    public String getDisplayName() {
        return "LuckPerms";
    }

    @Override
    public String getName() {
        return "LuckPerms";
    }

    @Override
    public boolean isAvailable() {
        return Bukkit.getPluginManager().getPlugin("LuckPerms") != null;
    }

    @Override
    public boolean migrate() throws Exception {
        return new LuckPermsMigrationExecutor().migrate();
    }
}
