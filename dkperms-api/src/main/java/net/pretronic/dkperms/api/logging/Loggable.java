/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 19.12.20, 17:19
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.logging;

import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;

public interface Loggable {

    int getId();

    Document serializeRecord();

    default String serializeRecordToString(){
        return DocumentFileType.JSON.getWriter().write(serializeRecord(),false);
    }

}
