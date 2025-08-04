package com.example.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ConcurrentHashMap that renews entries based on a cron schedule.
 * @param <K>
 * @param <V>
 */
public class RenewableConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {

    private final static Logger log = LoggerFactory.getLogger(RenewableConcurrentHashMap.class);

    private final RenewalListener<K, V> listener;

    public RenewableConcurrentHashMap(RenewalListener<K, V> listener) {
        this.listener = listener;
    }

    @Scheduled(cron = "${cache.renewal.cron:0 0/1 * * * ?}")
    public void renewEntries() {
        log.atDebug().log("Renew entries triggered at {}", LocalDateTime.now());
        for (Entry<K, V> entry : super.entrySet()) {
            listener.onRenew(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Listener interface for renewal events currently called by basic and history implementations
     * @param <K>
     * @param <V>
     */
    public interface RenewalListener<K, V> {
        void onRenew(K key, V value);
    }
}