package com.micro.app;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.context.ManagedExecutor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@Slf4j
public class InMemoryGridBean {

    private Queue<UUID> inputQueue;
    private IMap<UUID, UUID> outputMap;
    private Map<UUID, AsyncResponse> asyncResponseMap = new ConcurrentHashMap<>();

    @Resource
    private ManagedExecutor mes;

    @PostConstruct
    void init() {
        HazelcastInstance hazelcastInstance = HazelcastClient.newHazelcastClient();
        inputQueue = hazelcastInstance.getQueue("inbound-queue");
        outputMap = hazelcastInstance.getMap("inbound-map");
        outputMap.addEntryListener(new ItemListenerImpl(), false);
    }

    public void submitInboundRequest(UUID uuid, AsyncResponse asyncResponse) {
        asyncResponseMap.put(uuid, asyncResponse);
        inputQueue.add(uuid);
    }

    @PreDestroy
    void destroy() {
    }

    private class ItemListenerImpl implements EntryAddedListener<UUID, UUID> {

        @Override
        public void entryAdded(EntryEvent<UUID, UUID> event) {
            System.out.println("Item added:" + event.getKey());
            mes.execute(() -> {
                if (asyncResponseMap.containsKey(event.getKey())) {
                    UUID uuid = outputMap.get(event.getKey());
                    log.info("inside executor");
                    Response response = Response.ok(
                            Json.createObjectBuilder()
                                    .add("input", event.getKey().toString())
                                    .add("output", uuid.toString())
                                    .build()
                    ).build();
                    asyncResponseMap.get(event.getKey()).resume(response);
                    asyncResponseMap.remove(event.getKey());
                    outputMap.remove(event.getKey());
                    log.info("asyncResponseMap.size() = " + asyncResponseMap.size());
                    log.info("outputMap.size() = " + outputMap.size());
                }
            });
        }
    }
}
