/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 01.03.20, 12:58
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.scope.data;

import net.pretronic.libraries.utility.Iterators;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedData;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

public class ArrayScopeBasedDataList<T> extends ArrayList<ScopeBasedData<T>> implements ScopeBasedDataList<T> {

    private final Function<ScopeBasedData<T>, PermissionScope> SCOPE_MAPPING_FUNCTION = ScopeBasedData::getScope;

    public ArrayScopeBasedDataList(int initialCapacity) {
        super(initialCapacity);
    }

    public ArrayScopeBasedDataList() {}

    public ArrayScopeBasedDataList(Collection<? extends ScopeBasedData<T>> c) {
        super(c);
    }

    @Override
    public ScopeBasedData<T> get(PermissionScope scope) {
        return Iterators.findOne(this, data -> data.getScope().equals(scope));
    }

    @Override
    public Collection<T> getData(PermissionScope scope) {
        ScopeBasedData<T> data = get(scope);
        return data != null ? data.getData() : Collections.emptyList();
    }

    @Override
    public Collection<T> getAll() {
        Collection<T> result = new ArrayList<>();
        for (ScopeBasedData<T> data : this) {
            result.addAll(data.getData());
        }
        return result;
    }

    @Override
    public Collection<PermissionScope> getScopes() {
        return Iterators.map(this,SCOPE_MAPPING_FUNCTION);
    }

    @Override
    public void put(PermissionScope scope, Collection<T> data) {
        add(new DefaultScopeBasedData<>(scope, data));
    }
}
