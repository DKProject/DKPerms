/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 23.04.20, 19:03
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.object;

public enum SyncAction {

    OBJECT_NAME_UPDATE,
    OBJECT_TYPE_UPDATE,
    OBJECT_DISABLED_UPDATE,
    OBJECT_PRIORITY_UPDATE,
    OBJECT_SCOPE_UPDATE,
    OBJECT_GROUP_UPDATE,
    OBJECT_META_UPDATE,
    OBJECT_PERMISSION_UPDATE;

    public static SyncAction of(int action){
        for (SyncAction value : values()) {
            if(value.ordinal() == action) return value;
        }
        return null;
    }
}
