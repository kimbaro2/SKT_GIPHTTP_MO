package com.infra.mo.skt_giphttp_mo.utils

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import lombok.RequiredArgsConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

@Component
@Scope(scopeName = "prototype")
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
class CacheUtil(
    @Qualifier("ApplicationCacheQueue") private val cacheQueue: ConcurrentLinkedQueue<Any>,
    @Qualifier("ApplicationCacheMap") private val cacheMap: ConcurrentHashMap<String, Any>
) {

    private val log: Logger = LoggerFactory.getLogger(CacheUtil::class.java)

    fun queueSize(): Int {
        return cacheQueue.size
    }

    fun enqueue(value: Any, type: Class<*>) {
        cacheQueue.offer(type!!.cast(value))
//        log.info("CacheUtil.enqueue() -> ${cacheQueue.size} 객체를 큐에 추가했습니다.")
    }

    fun dequeue(type: Class<*>): Any? {
//        log.info("CacheUtil.dequeue() -> ${cacheQueue.size} 객체를 큐에서 제거했습니다.")
        return if (cacheQueue.isEmpty()) {
            return null;
//            throw IllegalArgumentException("[ERROR:0000] 큐가 비어있습니다.")
        } else {
            try {
                return type!!.cast(cacheQueue.poll())
            } catch (e: NullPointerException) {
                return null;
            }
        }
    }

    fun get(key: String?, type: Class<*>?): Any? {
        return if (cacheMap.containsKey(key)) {
            return type?.cast(cacheMap.get(key))
        } else {
            log.warn("[ERROR:0000] 일치하는 키가 존재하지 않습니다.: $key")
//            throw IllegalArgumentException("[ERROR:0000] 일치하는 키가 존재하지 않습니다.: $key")
        }
    }

    fun put(key: String, value: Any) {
        if (cacheMap.containsKey(key)) {
            log.warn("[ERROR:0000] 이미 존재하는 키입니다.: $key")
            throw IllegalArgumentException("[ERROR:0000] 이미 존재하는 키입니다.: $key")
        } else {
            cacheMap[key!!] = value!!
        }
    }
}