/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 04.05.20, 18:00
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.object.meta;

import net.pretronic.dkperms.api.TimeoutAble;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.libraries.document.Document;

import java.util.concurrent.CompletableFuture;

public interface ObjectMetaEntry extends TimeoutAble {

    int getId();

    PermissionObject getOwner();

    String getKey();

    PermissionScope getScope();

    int getPriority();


    String getValue();

    byte getByteValue();

    int getIntValue();

    long getLongValue();

    double getDoubleValue();

    float getFloatValue();

    boolean getBooleanValue();

    Document getDocumentValue();

    boolean equalsValue(Object value);


    void setScope(PermissionObject executor,PermissionScope scope);


    void setValue(PermissionObject executor,Object value);

    CompletableFuture<Void> setValueAsync(PermissionObject executor,Object value);


    void setPriority(PermissionObject executor,int priority);

    CompletableFuture<Void> setPriorityAsync(PermissionObject executor,int priority);


    void update(PermissionObject executor,Object value, int priority, PermissionScope scope, long timeout);
}
