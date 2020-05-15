/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 14.03.20, 22:32
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.object.search;

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.search.ObjectSearchQuery;
import net.pretronic.dkperms.api.object.search.ObjectSearchResult;
import net.pretronic.libraries.utility.Iterators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class DirectObjectSearchResult implements ObjectSearchResult {

    private final ObjectSearchQuery query;
    private final List<PermissionObject> objects;

    public DirectObjectSearchResult(ObjectSearchQuery query, List<PermissionObject> objects) {
        this.query = query;
        this.objects = objects;
    }

    @Override
    public ObjectSearchQuery getQuery() {
        return query;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public PermissionObject getFirst() {
        return objects.get(0);
    }

    @Override
    public PermissionObject getFirstOrNull() {
        if(objects.isEmpty()) return null;
        return getFirst();
    }

    @Override
    public PermissionObject getLastOrNull() {
        if(objects.isEmpty()) return null;
        return getLast();
    }

    @Override
    public PermissionObject getLast() {
        return objects.get(objects.size()-1);
    }

    @Override
    public PermissionObject get(int index) {
        return objects.get(index);
    }

    @Override
    public Collection<PermissionObject> get(int fromIndex, int toIndex) {
        Collection<PermissionObject> result = new ArrayList<>();
        int index = fromIndex;
        for (PermissionObject object : objects) {
            if(index >= toIndex) break;
            result.add(object);
            index++;
        }
        return result;
    }

    @Override
    public Collection<PermissionObject> getAll() {
        return objects;
    }

    @Override
    public Collection<Object> getAllHolders() {
        return Iterators.map(this.objects, PermissionObject::getHolder);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Collection<T> getAllHolders(Class<T> holderClass) {
        return (Collection<T>) getAllHolders();
    }

    @Override
    public Iterator<PermissionObject> iterator() {
        return objects.iterator();
    }
}
