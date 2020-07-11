/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 04.05.20, 18:02
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api;

import net.pretronic.dkperms.api.object.PermissionObject;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public interface TimeoutAble {

    Duration PERMANENTLY = Duration.of(-1, ChronoUnit.SECONDS);

    long getTimeout();

    void setTimeout(PermissionObject executor, long timeout);


    default void setDuration(PermissionObject executor,long duration, TimeUnit unit){
        setTimeout(executor,System.currentTimeMillis()+unit.toMillis(duration));
    }

    default Duration getRemainingDuration(){
        if(getTimeout() == -1) return PERMANENTLY;
        if(getTimeout() > 0) return Duration.of(getTimeout()-System.currentTimeMillis(), ChronoUnit.MILLIS);
        return Duration.of(getTimeout(),ChronoUnit.SECONDS);
    }

    default long getRemainingMillis(){
        if(getTimeout() > 0) return getTimeout()-System.currentTimeMillis();
        return getTimeout();
    }

    default boolean hasTimeout(){
        return getTimeout() > 0 && getTimeout() < System.currentTimeMillis();
    }

    default String getTimeoutFormatted(){
        return DKPerms.getInstance().getFormatter().formatDateTime(getTimeout());
    }

    default String getRemainingDurationFormatted(){
        return DKPerms.getInstance().getFormatter().formatRemainingDuration(getRemainingMillis());
    }
}
