/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.scope;

import net.prematic.libraries.utility.map.Pair;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.PermissionScopeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DefaultPermissionScopeBuilder implements PermissionScopeBuilder {

    private final PermissionScope root;
    private final List<Pair<String,String>> conditions;

    public DefaultPermissionScopeBuilder(PermissionScope root) {
        this.root = root;
        this.conditions = new ArrayList<>();
    }

    @Override
    public PermissionScopeBuilder of(String type, String value) {
        this.conditions.add(new Pair<>(type,value));
        return this;
    }

    @Override
    public PermissionScope build() {
        PermissionScope current = root;
        for (Pair<String, String> entry : conditions) current = current.getChild(entry.getKey(),entry.getValue());
        return current;
    }

    @Override
    public CompletableFuture<PermissionScope> buildAsync() {
        return null;//@Todo async
    }
}
