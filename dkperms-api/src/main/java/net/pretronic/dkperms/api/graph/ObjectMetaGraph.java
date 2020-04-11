/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 18.01.20, 22:38
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.graph;

import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;

public interface ObjectMetaGraph extends Graph<ObjectMetaEntry> {

    ObjectMetaEntry calculate(String key);

    boolean isSet(String key);

    boolean isSet(String key,Object value);

}
