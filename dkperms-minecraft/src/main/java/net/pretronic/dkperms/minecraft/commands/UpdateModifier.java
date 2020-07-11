/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 03.05.20, 13:09
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands;

import java.time.Duration;

public enum UpdateModifier {

    REPLACE("r"){
        @Override
        public Duration take(Duration existing, Duration input) {
            return input;
        }
    },
    FAIL("f"){
        @Override
        public Duration take(Duration existing, Duration input) {
            return null;
        }
    },
    ACCUMULATE("a"){
        @Override
        public Duration take(Duration existing, Duration input) {
            if(input.getSeconds() != -1){
                if(existing.getSeconds() == -1) return existing;
                else return input.plus(existing);
            }
            return input;
        }
    },
    LONGEST("l"){
        @Override
        public Duration take(Duration existing, Duration input) {
            if(input.getSeconds() != -1) {
                if (existing.getSeconds() == -1) return existing;
                else{
                    if(existing.getSeconds() > input.getSeconds()){
                        return existing;
                    }
                }
            }
            return input;
        }
    };

    private final String short0;

    UpdateModifier(String short0) {
        this.short0 = short0;
    }

    public static UpdateModifier parse(String input){
        for (UpdateModifier value : values()) {
            if(value.name().equalsIgnoreCase(input) || value.short0.equalsIgnoreCase(input)){
                return value;
            }
        }
        return null;
    }

    public abstract Duration take(Duration existing,Duration input);
}
