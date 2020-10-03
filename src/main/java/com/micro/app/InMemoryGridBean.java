package com.micro.app;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.collection.ItemEvent;
import com.hazelcast.collection.ItemListener;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.MapListener;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.context.ManagedExecutor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.json.Json;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Startup
@Singleton
@Slf4j
public class InMemoryGridBean {

    private Queue<UUID> inputQueue;
    private IMap<UUID, UUID> outputMap;
    private Map<UUID, AsyncResponse> asyncResponseMap = new ConcurrentHashMap<>();

    @Resource
    private ManagedExecutor mes;

    @PostConstruct
    void init() {
        inputQueue = HazelcastClient.newHazelcastClient().getQueue("input-queue");
        outputMap = HazelcastClient.newHazelcastClient().getMap("output-map");
        outputMap.addEntryListener(new ItemListenerImpl<UUID, UUID>(), false);
    }

    public Queue<UUID> getInputQueue() {
        return inputQueue;
    }

    public void submitRequest(UUID uuid, AsyncResponse asyncResponse) {
        inputQueue.add(uuid);
        asyncResponseMap.put(uuid, asyncResponse);
    }

    public Map<UUID, UUID> getOutputMap() {
        return outputMap;
    }

    @PreDestroy
    void destroy() {
    }

    private class ItemListenerImpl<K, V> implements EntryAddedListener<K, V> {

        @Override
        public void entryAdded(EntryEvent<K, V> event) {
            System.out.println("Item added:" + event.getName());
            mes.execute(() -> {
                UUID uuid = outputMap.get(event.getKey());
                log.info("inside executor");
                Response response = Response.ok(
                        Json.createObjectBuilder()
                                .add("requestId", uuid.toString())
                                .build()
                ).build();
                asyncResponseMap.get(event.getKey()).resume(response);
            });
        }
    }
}
