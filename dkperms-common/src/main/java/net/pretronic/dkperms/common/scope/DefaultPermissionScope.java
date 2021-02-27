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

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.message.bml.variable.describer.VariableObjectToString;
import net.pretronic.libraries.synchronisation.SynchronisationCaller;
import net.pretronic.libraries.synchronisation.UnconnectedSynchronisationCaller;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.annonations.Internal;
import net.pretronic.libraries.utility.annonations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DefaultPermissionScope implements PermissionScope, VariableObjectToString {

    public static SynchronisationCaller<Integer> SYNCHRONISATION_CALLER = new UnconnectedSynchronisationCaller<>(true);

    private final int UNSAVED_ID = -1;

    private int id;
    private int level;
    private String key;
    private String name;

    private PermissionScope parent;
    private List<PermissionScope> children;

    public DefaultPermissionScope(int id, String key, String name,@Nullable PermissionScope parent) {
        Validate.notNull(key,name);
        this.id = id;
        this.key = key;
        this.name = name;
        this.level = parent!=null?parent.getLevel()+1:0;
        this.parent = parent;
    }

    public DefaultPermissionScope(int id, String key, String name, int level, PermissionScope parent, List<PermissionScope> children) {
        Validate.notNull(parent,key,name,children);
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
    public String getPath() {
        if(parent == null) return "\\";
        else return parent.getPath()+"\\"+key+"="+name;
    }

    @Override
    public PermissionScope getParent() {
        return parent;
    }

    @Override
    public PermissionScope getChild(String key, String name) {
        Objects.requireNonNull(key,"Key can't be null");
        Objects.requireNonNull(name,"Name can't be null");
        PermissionScope result = Iterators.findOne(getChildren(), scope -> scope.getKey().equalsIgnoreCase(key) && scope.getName().equalsIgnoreCase(name));
        if(result == null){
            result = new DefaultPermissionScope(UNSAVED_ID,key,name,this);
            this.children.add(result);
        }
        return result;
    }

    @Override
    public CompletableFuture<PermissionScope> getChildAsync(String key, String name) {
        return DKPerms.getInstance().getExecutor().execute(() -> getChild(key, name));
    }

    @Override
    public List<PermissionScope> getChildren() {
        if(children == null) loadChildren();
        return children;
    }

    @Override
    public CompletableFuture<List<PermissionScope>> getChildrenAsync() {
        return DKPerms.getInstance().getExecutor().execute(this::getChildren);
    }

    @Override
    public boolean contains(PermissionScope innerScope) {
        PermissionScope previous = innerScope;
        while (previous != null){
            if(previous == this) return true;
            previous = previous.getParent();
        }
        return false;
    }

    @Override
    public boolean areChildrenLoaded() {
        return children != null;
    }

    @Override
    public boolean isSaved() {
        boolean saved = id != UNSAVED_ID;
        if(!saved && !SYNCHRONISATION_CALLER.isConnected()){
            if(getParent() == null) return id != UNSAVED_ID;
            else if(!getParent().isSaved()) return false;
            else {
                this.id = DKPerms.getInstance().getStorage().getScopeStorage().getScopeId(getParent().getId(),key,name);
                if(this.id == 0) this.id = UNSAVED_ID;
                return this.id != UNSAVED_ID;
            }
        }
        return saved;
    }

    @Override
    public void insert() {
        if(isSaved()) throw new IllegalArgumentException("The Scope is already inserted.");
        if(parent != null && !parent.isSaved()) parent.insert();
        this.id = DKPerms.getInstance().getStorage().getScopeStorage().insertScope(parent!=null?parent.getId():UNSAVED_ID,this.key,this.name);
        if(getParent() != null){
            SYNCHRONISATION_CALLER.create(this.id, Document.newDocument()
                    .add("parent",getParent().getId())
                    .add("key",this.key)
                    .add("name",this.name));
        }
    }

    @Override
    public void rename(String key, String name) {
        Objects.requireNonNull(key,"Key can't be null");
        Objects.requireNonNull(name,"Name can't be null");
        DKPerms.getInstance().getStorage().getScopeStorage().updateScopeName(this.id,this.key,this.name);
        this.key = key;
        this.name = name;
    }

    @Override
    public CompletableFuture<Void> renameAsync(String key, String name) {
        return DKPerms.getInstance().getExecutor().executeVoid(() -> rename(key, name));
    }

    /*
    @Override
    public void delete() {
        this.children.forEach(PermissionScope::delete);
        DKPerms.getInstance().getStorage().getScopeStorage().deleteScope(this.id);
        SYNCHRONISATION_CALLER.delete(this.id, Document.newDocument());
        this.id = UNSAVED_ID;
    }

    @Override
    public CompletableFuture<Void> deleteAsync() {
        return DKPerms.getInstance().getExecutor().executeVoid(this::delete);
    }
     */

    @Override
    public void move(PermissionScope newParent) {
        Objects.requireNonNull(newParent,"Parent can't be null");
        DKPerms.getInstance().getStorage().getScopeStorage().updateScopeParent(this.id,newParent.getId());
        this.parent = newParent;
        this.level = newParent.getLevel()+1;
    }

    @Override
    public CompletableFuture<Void> moveAsync(PermissionScope parent) {
        return DKPerms.getInstance().getExecutor().executeVoid(() -> move(parent));
    }

    private void loadChildren() {
        if (isSaved()) this.children = DKPerms.getInstance().getStorage().getScopeStorage().loadScopes(this);
        else this.children = new ArrayList<>();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        else if(obj == null) return false;
        else if(obj instanceof DefaultPermissionScope) return this.id == ((DefaultPermissionScope) obj).getId();
        return false;
    }

    @Internal
    public void provideReversedLoadedScope(PermissionScope scope){
        loadChildren();
        this.children.remove(scope);
        this.children.add(scope);
    }

    @Internal
    public void scopeDeleted(){
        this.id = UNSAVED_ID;
    }

    @Internal
    public void scopeInserted(int id){
        this.id = id;
    }

    @Override
    public String toStringVariable() {
        return getPath();
    }
}
