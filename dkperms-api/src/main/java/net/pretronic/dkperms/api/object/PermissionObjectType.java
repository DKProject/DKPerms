/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.object;

public interface PermissionObjectType {

    PermissionObjectType GROUP = Registry.INFO.getGroup();

    PermissionObjectType USER = Registry.INFO.getUser();

    PermissionObjectType SERVICE = Registry.INFO.getService();


    int getId();

    String getName();

    void setName(String name);

    PermissionObjectFactory getLocalFactory(PermissionObjectFactory factory);

    void setLocalFactory(PermissionObjectFactory factory);



    class Registry {

        public static DefaultObjectType INFO;

    }

    class DefaultObjectType {

        private final PermissionObjectType user;
        private final PermissionObjectType group;
        private final PermissionObjectType service;

        public DefaultObjectType(PermissionObjectType user, PermissionObjectType group, PermissionObjectType service) {
            this.user = user;
            this.group = group;
            this.service = service;
        }

        public PermissionObjectType getUser() {
            return user;
        }

        public PermissionObjectType getGroup() {
            return group;
        }

        public PermissionObjectType getService() {
            return service;
        }
    }

}
