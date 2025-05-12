package com.matching.ezgg.matching.infra.redis;

public enum RedisKey {
    STREAM_KEY("matching-stream"),
    STREAM_ID_HASH_KEY("stream-id-hash"),
    STREAM_GROUP("matching-group"),
    CONSUMER_NAME ("matching-consumer"),
    RETRY_ZSET_KEY("matching-retry-zset");

    private final String value;

    RedisKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
