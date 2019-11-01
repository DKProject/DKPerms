/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.backup;

import java.net.InetAddress;

public interface BackupMeta {

    String getName();

    long getTimestamp();

    InetAddress getAddress();


    String getDKPermsVersion();

    int getDKPermsBuild();

}
