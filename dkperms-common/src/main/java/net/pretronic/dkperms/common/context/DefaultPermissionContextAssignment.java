/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 01.11.19, 21:18
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.context;

import net.pretronic.dkperms.api.context.PermissionContext;
import net.pretronic.dkperms.api.context.PermissionContextAssignment;

public class DefaultPermissionContextAssignment implements PermissionContextAssignment {

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public PermissionContext getContext() {
        return null;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public int getIntValue() {
        return 0;
    }

    @Override
    public long getLongValue() {
        return 0;
    }

    @Override
    public boolean getByteValue() {
        return false;
    }

    @Override
    public float getFloatValue() {
        return 0;
    }

    @Override
    public boolean getBooleanValue() {
        return false;
    }

    @Override
    public void setValue(Object value) {

    }
}
