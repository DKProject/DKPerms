/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 01.11.19, 21:09
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.context;

import net.prematic.libraries.utility.Iterators;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.context.PermissionContext;
import net.pretronic.dkperms.api.context.PermissionContextManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class DefaultPermissionContextManager implements PermissionContextManager {

    private final List<PermissionContext> cachedContexts;

    public DefaultPermissionContextManager() {
        this.cachedContexts = new ArrayList<>();
    }

    @Override
    public PermissionContext getContext(int id) {
        PermissionContext result = Iterators.findOne(cachedContexts, context -> context.getId() == id);
        if(result == null){
            result = DKPerms.getInstance().getStorage().getContextStorage().getContext(id);
            if(result == null) result = new NullPermissionContext(id,"Unknown");
            this.cachedContexts.add(result);
        }
        return result instanceof NullPermissionContext ?null:result;
    }

    @Override
    public PermissionContext getContext(String name) {
        Objects.requireNonNull(name,"Name can't be null");
        PermissionContext result = Iterators.findOne(cachedContexts, context -> context.getName().equalsIgnoreCase(name));
        if(result == null){
            result = DKPerms.getInstance().getStorage().getContextStorage().getContext(name);
            if(result == null) result = new NullPermissionContext(-1,name);
            this.cachedContexts.add(result);
        }
        return result instanceof NullPermissionContext ?null:result;
    }

    @Override
    public PermissionContext getOrCreateContext(String name) {
        Objects.requireNonNull(name,"Name can't be null");
        PermissionContext result = getContext(name);
        if(result == null) result = createContext(name);
        return result;
    }

    @Override
    public CompletableFuture<PermissionContext> getContextAsync(int id) {
        return DKPerms.getInstance().getExecutor().execute(() -> getContext(id));
    }

    @Override
    public CompletableFuture<PermissionContext> getContextAsync(String name) {
        return DKPerms.getInstance().getExecutor().execute(() -> getContext(name));
    }

    @Override
    public CompletableFuture<PermissionContext> getOrCreateContextAsync(String name) {
        return DKPerms.getInstance().getExecutor().execute(() -> getOrCreateContext(name));
    }


    @Override
    public boolean hasContext(String name) {
        return getContext(name) != null;
    }

    @Override
    public CompletableFuture<Boolean> hasContextAsync(String name) {
        return DKPerms.getInstance().getExecutor().execute(() -> hasContext(name));
    }


    @Override
    public PermissionContext createContext(String name) {
        Objects.requireNonNull(name,"Name can't be null");
        if(getContext(name) != null) throw new IllegalArgumentException("A context with the name "+name+" exists already");
        int id = DKPerms.getInstance().getStorage().getContextStorage().createContext(name);
        PermissionContext context = new DefaultPermissionContext(id,name);
        Iterators.remove(this.cachedContexts, context1
                -> context1 instanceof NullPermissionContext
                && (context1.getName().equalsIgnoreCase(name) || context1.getId() == id));
        this.cachedContexts.add(context);
        return context;
    }

    @Override
    public CompletableFuture<PermissionContext> createContextAsync(String name) {
        return DKPerms.getInstance().getExecutor().execute(() -> createContext(name));
    }


    @Override
    public void deleteContext(int id) {
        DKPerms.getInstance().getStorage().getContextStorage().deleteContext(id);
        Iterators.removeOne(this.cachedContexts, context -> context.getId() == id);
    }

    @Override
    public void deleteContext(String name) {
        Objects.requireNonNull(name,"Name can't be null");
        DKPerms.getInstance().getStorage().getContextStorage().deleteContext(name);
        Iterators.removeOne(this.cachedContexts, context -> context.getName().equalsIgnoreCase(name));
    }

    @Override
    public void deleteContext(PermissionContext context) {
        Objects.requireNonNull(context,"Context can't be null");
        context.delete();
    }

    @Override
    public CompletableFuture<Void> deleteContextAsync(int id) {
        return DKPerms.getInstance().getExecutor().executeVoid(() -> deleteContext(id));
    }

    @Override
    public CompletableFuture<Void> deleteContextAsync(String name) {
        return DKPerms.getInstance().getExecutor().executeVoid(() -> deleteContext(name));
    }

    @Override
    public CompletableFuture<Void> deleteContextAsync(PermissionContext context) {
        return context.deleteAsync();
    }
}
