/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 30.05.20, 21:08
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.permission;

import net.pretronic.dkperms.api.permission.analyse.PermissionAnalyser;
import net.pretronic.dkperms.api.permission.analyse.PermissionRequest;
import net.pretronic.dkperms.common.calculator.PermissionCalculator;
import net.pretronic.libraries.utility.Validate;

import java.util.*;
import java.util.function.Consumer;

public class DefaultPermissionAnalyser implements PermissionAnalyser {

    private static int MAXIMUM_SIZE = 10000;

    private boolean enabled;
    private final Collection<Consumer<PermissionRequest>> listeners;
    private final List<PermissionRequest> requests;

    private final Map<String,String[]> includedPermissionsNodes;
    private final Map<String,String[]> excludedPermissionsNodes;

    public DefaultPermissionAnalyser() {
        enabled = false;
        listeners = new ArrayList<>();
        requests = new ArrayList<>();
        includedPermissionsNodes = new HashMap<>();
        excludedPermissionsNodes = new HashMap<>();
        includedPermissionsNodes.put("*",new String[]{"*"});
    }

    @Override
    public Collection<String> getIncludedPermissions() {
        return includedPermissionsNodes.keySet();
    }

    @Override
    public Collection<String> getExcludedPermissions() {
        return excludedPermissionsNodes.keySet();
    }

    @Override
    public void addIncludedPermission(String permission) {
        Validate.notNull(permission);
        includedPermissionsNodes.put(permission,permission.split("\\."));
    }

    @Override
    public void removeIncludedPermission(String permission) {
        Validate.notNull(permission);
        includedPermissionsNodes.remove(permission);
    }

    @Override
    public void addExcludedPermission(String permission) {
        Validate.notNull(permission);
        excludedPermissionsNodes.put(permission,permission.split("\\."));
    }

    @Override
    public void removeExcludedPermission(String permission) {
        Validate.notNull(permission);
        excludedPermissionsNodes.remove(permission);
    }

    @Override
    public boolean canPermissionBeAnalysed(String permission) {
        Validate.notNull(permission);
        String[] nodes = permission.split("\\.");

        boolean included = false;
        for (String[] value : includedPermissionsNodes.values()) {
            if(PermissionCalculator.compare(value,nodes)){
                included = true;
                break;
            }
        }

        for (String[] value : excludedPermissionsNodes.values()) {
            if(PermissionCalculator.compare(value,nodes)){
                return false;
            }
        }

        return included;
    }

    @Override
    public List<PermissionRequest> geRequests() {
        return requests;
    }

    @Override
    public void offerRequest(PermissionRequest request) {
        Validate.notNull(request);
    }

    @Override
    public void addListener(Consumer<PermissionRequest> request) {
        Validate.notNull(request);
        listeners.add(request);
    }

    @Override
    public void removeListener(Object request) {
        Validate.notNull(request);
        listeners.remove(request);
    }

    @Override
    public boolean hasListener(Object request) {
        Validate.notNull(request);
        return listeners.contains(request);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void enable() {
        enabled = true;
    }

    @Override
    public void disable() {
        enabled = false;
    }
}
