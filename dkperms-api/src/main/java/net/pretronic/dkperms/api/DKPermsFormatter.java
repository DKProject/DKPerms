/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 18.04.20, 17:00
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api;

public interface DKPermsFormatter {

    String formatDateTime(long time);

    String formatRemainingDuration(long timeout);

}
