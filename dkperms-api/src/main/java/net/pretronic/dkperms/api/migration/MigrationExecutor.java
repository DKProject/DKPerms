/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 21.05.20, 14:55
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.migration;

public interface MigrationExecutor {

    boolean migrate() throws Exception;

}
