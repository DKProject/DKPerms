/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 29.02.20, 20:44
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.tree;

import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedData;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;
import net.pretronic.libraries.utility.Validate;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TreeListBuilder<T> {

    private final PermissionScope root;
    private final ScopeBasedDataList<T> dataList;

    public TreeListBuilder(PermissionScope root, ScopeBasedDataList<T> dataList) {
        Validate.notNull(root,dataList);
        this.root = root;
        this.dataList = dataList;
    }

    private Consumer<PermissionScope> headerPrinter;
    private BiConsumer<PermissionScope,T> dataPrinter;

    public void setHeaderPrinter(Consumer<PermissionScope> headerPrinter) {
        this.headerPrinter = headerPrinter;
    }

    public void setDataPrinter(BiConsumer<PermissionScope,T> dataPrinter) {
        this.dataPrinter = dataPrinter;
    }

    public void process(){
        Validate.notNull(headerPrinter,dataPrinter);
        process(root, root,false);
    }

    private boolean process(PermissionScope last, PermissionScope scope, boolean header){
        ScopeBasedData<T> data = dataList.get(scope);

        if(data != null && !data.getData().isEmpty()){
            if(!header) printHeader(last,scope);
            for (T obj : data.getData()) {
                dataPrinter.accept(scope,obj);
            }
        }
        if(scope.areChildrenLoaded()){
            for (PermissionScope child : scope.getChildren()) {
                boolean result = process(last,child,header);
                if(result) last = scope;
            }
        }
        return false;
    }

    private void printHeader(PermissionScope last,PermissionScope current){
        if(current.getParent() != null && !current.getParent().equals(last)){
            printHeader(last,current.getParent());
        }
        headerPrinter.accept(current);
    }
}
