package com.micro.app;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionalTask;
import com.hazelcast.transaction.TransactionalTaskContext;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.context.ManagedExecutor;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class LockSupport {

    private IMap<UUID, ZonedDateTime> lockMap;
    private final UUID lock = UUID.randomUUID();
    @Inject
    InMemoryGridBean gridBean;
    @Resource
    private ManagedExecutor mes;
    private HazelcastInstance hazelcastInstance;

    @PostConstruct
    public void init() {
        hazelcastInstance = gridBean.getHazelcastInstance();
        lockMap = hazelcastInstance.getMap("lock-map");
        log.info("hazelcastInstance.getName() " + hazelcastInstance.getName());
    }

    void tryAndAcquireLockAtStartup() {
        hazelcastInstance.executeTransaction(context -> {
            if (lockMap.isEmpty()) {
                return lockMap.put(lock, ZonedDateTime.now());
            }
            return lockMap.get(lock);
        });
    }

    boolean hasLock(ZonedDateTime now) {
        //log.info("lockAcquired {}", lockMap.containsKey(lock));
        if (lockMap.containsKey(lock)) {
            lockMap.put(lock, now);
            return true;
        }

        return false;
    }

    void tryAndAcquire(ZonedDateTime now) {
        UUID key = lockMap.keySet().stream().findAny().get();
        Duration d = Duration.between(lockMap.get(key), now);
        System.out.println("d.getSeconds() = " + d.getSeconds());
        if (d.getSeconds() > 10) {
            hazelcastInstance.executeTransaction(context -> {
                lockMap.clear();
                if (lockMap.isEmpty()) {
                    return lockMap.put(lock, ZonedDateTime.now());
                }
                return lockMap.get(lock);
            });
        }
    }
}
