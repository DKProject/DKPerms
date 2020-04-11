/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.object.meta;

import net.pretronic.libraries.document.Document;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.scope.PermissionScope;

import java.util.concurrent.CompletableFuture;

public interface ObjectMetaEntry {

    int getId();

    PermissionObject getOwner();

    String getKey();

    PermissionScope getScope();


    String getValue();

    byte getByteValue();

    int getIntValue();

    long getLongValue();

    double getDoubleValue();

    float getFloatValue();

    boolean getBooleanValue();

    Document getDocumentValue();

    boolean equalsValue(Object value);


    void setValue(Object value);

    CompletableFuture<Void> setValueAsync(Object value);


    void delete();

    CompletableFuture<Void> deleteAsync();
}
