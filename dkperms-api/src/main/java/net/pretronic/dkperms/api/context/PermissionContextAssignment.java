/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.context;

public interface PermissionContextAssignment {

    int getId();

    PermissionContext getContext();

    String getValue();

    int getIntValue();

    long getLongValue();

    boolean getByteValue();

    float getFloatValue();

    boolean getBooleanValue();

    void setValue(Object value);

    boolean isSaved();

    boolean save();
}
