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

import net.prematic.libraries.utility.Iterators;
import net.prematic.libraries.utility.annonations.Nullable;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.storage.DKPermsStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

//@Todo implement async
public class DefaultPermissionScope implements PermissionScope {

    private final int UNSAVED_ID = -1;

    private final DKPermsStorage storage;

    private int id;
    private int level;
    private String key;
    private String name;

    private PermissionScope parent;
    private List<PermissionScope> children;

    public DefaultPermissionScope(DKPermsStorage storage, int id, String key, String name,@Nullable PermissionScope parent) {
        this.storage = storage;
        this.id = id;
        this.key = key;
        this.name = name;
        this.level = parent!=null?parent.getLevel()+1:0;
        this.parent = parent;
    }

    public DefaultPermissionScope(DKPermsStorage storage, int id, String key, String name, int level, PermissionScope parent, List<PermissionScope> children) {
        this.storage = storage;
        this.id = id;
        this.key = key;
        this.name = name;
        this.level = level;
        this.parent = parent;
        this.children = children;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PermissionScope getParent() {
        return parent;
    }

    @Override
    public PermissionScope getChild(String key, String name) {
        Objects.requireNonNull(key,"Key can't be null");
        Objects.requireNonNull(name,"Name can't be null");
        PermissionScope result =  Iterators.findOne(getChildren(), scope -> scope.getKey().equalsIgnoreCase(key) && scope.getName().equalsIgnoreCase(name));
        if(result == null){
            result = new DefaultPermissionScope(storage,UNSAVED_ID,key,name,this);
            this.children.add(result);
        }
        return result;
    }

    @Override
    public CompletableFuture<PermissionScope> getChildAsync(String key, String name) {
        return null;
    }

    @Override
    public List<PermissionScope> getChildren() {
        if(children == null) loadChildren();
        return children;
    }

    @Override
    public CompletableFuture<List<PermissionScope>> getChildrenAsync() {
        return null;
    }

    @Override
    public boolean isSaved() {
        return id != UNSAVED_ID;
    }

    @Override
    public void insert() {
        if(isSaved()) throw new IllegalArgumentException("The Scope is already inserted.");
        if(!parent.isSaved()) parent.insert();
        this.id = this.storage.insertScope(this);
    }

    @Override
    public void rename(String key, String name) {
        Objects.requireNonNull(key,"Key can't be null");
        Objects.requireNonNull(name,"Name can't be null");
        this.key = key;
        this.name = name;
        if(isSaved()){
            try{
                this.storage.updateScopeName(this);
            }catch (RuntimeException exception){
                throw exception;
            }
        }
    }

    @Override
    public void move(PermissionScope newParent) {
        Objects.requireNonNull(newParent,"Parent can't be null");
        this.storage.updateScopeParent(this,newParent);
        this.parent = newParent;
        this.level = newParent.getLevel()+1;
    }

    @Override
    public void delete() {
        this.children.forEach(PermissionScope::delete);
        this.storage.deleteScope(this);
        this.id = UNSAVED_ID;
    }

    private void loadChildren(){
        if(isSaved()) this.children = this.storage.loadScopes(this);
        else this.children = new ArrayList<>();
    }
}
