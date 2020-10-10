package com.micro.app;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;
import com.hazelcast.topic.Message;
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
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@Slf4j
public class InMemoryGridBean {

    private Queue<String> inputQueue;
    private ITopic<String> inputTopic;
    private Map<String, AsyncResponse> asyncResponseMap = new ConcurrentHashMap<>();

    @Resource
    private ManagedExecutor mes;

    @PostConstruct
    void init() {
        HazelcastInstance hazelcastInstance = HazelcastClient.newHazelcastClient();
        inputQueue = hazelcastInstance.getQueue("inbound-queue");
        inputTopic = hazelcastInstance.getReliableTopic("inbound-topic");
        inputTopic.addMessageListener(this::onMessage);
    }

    public void submitInboundRequest(String uuid, AsyncResponse asyncResponse) {
        asyncResponseMap.put(uuid, asyncResponse);
        inputQueue.add(uuid);
    }

    @PreDestroy
    void destroy() {
    }

    public void onMessage(Message<String> message) {
        log.info("message.getMessageObject() " + message.getMessageObject());
        String[] split = message.getMessageObject().split(",");
        mes.execute(() -> {
            if (asyncResponseMap.containsKey(split[0])) {
                log.info("inside executor");
                Response response = Response.ok(
                        Json.createObjectBuilder()
                                .add("input", split[0])
                                .add("output", split[1])
                                .build()
                ).build();
                asyncResponseMap.get(split[0]).resume(response);
                asyncResponseMap.remove(split[0]);
                log.info("asyncResponseMap.size() = " + asyncResponseMap.size());
            }
        });
    }
}
