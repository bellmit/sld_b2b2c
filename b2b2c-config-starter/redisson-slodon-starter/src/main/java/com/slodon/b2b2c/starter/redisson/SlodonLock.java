package com.slodon.b2b2c.starter.redisson;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * 自定义锁
 */
public class SlodonLock {
    private RedissonClient client;

    SlodonLock(RedissonClient client) {
        this.client = client;
    }

    /**
     * 加锁，默认锁有效期30秒
     * @param lockName 锁名称
     */
    public void lock(String lockName){
        this.lock(lockName,30);
    }

    /**
     * 加锁
     * @param lockName 锁名称
     * @param leaseTime 锁有效期，单位秒
     */
    public void lock(String lockName,long leaseTime){
        client.getLock(lockName).lock(leaseTime, TimeUnit.SECONDS);
    }

    /**
     * 解锁
     * @param lockName  锁名称
     */
    public void unlock(String lockName) {
        RLock lock = client.getLock(lockName);
        if (lock.isHeldByCurrentThread()){
            lock.unlock();
        }
    }

    /**
     * 判断该锁是否已经被线程持有
     * @param lockName  锁名称
     */
    public boolean isLock(String lockName) {
        return client.getLock(lockName).isLocked();
    }


    /**
     * 判断该线程是否持有当前锁
     * @param lockName  锁名称
     */
    public boolean isHeldByCurrentThread(String lockName) {
        return client.getLock(lockName).isHeldByCurrentThread();
    }
}
