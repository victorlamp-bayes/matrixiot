package com.victorlamp.matrixiot.common.util.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.time.Duration;
import java.util.concurrent.Executors;

public class CaffeineCacheUtils {
    public static <K, V> LoadingCache<K, V> buildLoadingCache(Duration duration, CacheLoader<K, V> loader) {
        return Caffeine.newBuilder()
                .refreshAfterWrite(duration)
                .executor(Executors.newCachedThreadPool())
                .build(loader);
    }
}
