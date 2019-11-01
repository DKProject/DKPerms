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

import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.PermissionScopeBuilder;
import net.pretronic.dkperms.api.scope.PermissionScopeManager;
import net.pretronic.dkperms.api.storage.DKPermsStorage;

import java.util.List;

/*
@Todo - Configuration
        - Default scope load count (With level and tree width)
        - Current scope
 */
public class DefaultPermissionScopeManager implements PermissionScopeManager {

    private final DKPermsStorage storage;
    private PermissionScope root;
    private PermissionScope current;

    public DefaultPermissionScopeManager(DKPermsStorage storage) {
        this.storage = storage;
    }

    @Override
    public PermissionScope getCurrentInstanceScope() {
        return current;
    }

    @Override
    public PermissionScope getRoot() {
        return root;
    }

    @Override
    public PermissionScope getNamespace(String name) {
        return root.getChild(PermissionScope.NAMESPACE,name);
    }

    @Override
    public PermissionScope get(String scopeOrder) {
        String[] scopes = scopeOrder.split(";");
        PermissionScope last = root;
        for (String scope : scopes) {
            String[] data = scope.split("=");
            last = last.getChild(data[0],data[1]);
        }
        return last;
    }

    @Override
    public PermissionScopeBuilder newBuilder() {
        return new DefaultPermissionScopeBuilder(this.root);
    }

    public void initialise(){
        List<PermissionScope> result = this.storage.loadScopes(null);
        if(result.isEmpty()){
            this.root = new DefaultPermissionScope(this.storage,-1,"Root","Root",null);
            this.root.insert();
        }else{
            this.root = result.get(0);
            if(result.size() > 1);//@Todo warning to many roots
        }
    }
}
