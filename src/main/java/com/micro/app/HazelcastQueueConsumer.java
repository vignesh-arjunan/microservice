package com.micro.app;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.collection.IQueue;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;
import lombok.Builder;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.synchronizedMap;

public class HazelcastQueueConsumer {
//    public static void main(String[] args) throws InterruptedException {
//        ClientConfig clientConfig = new ClientConfig();
//        clientConfig.getNetworkConfig().addAddress("127.0.0.1");
//        HazelcastInstance hazelcastInstance = HazelcastClient.newHazelcastClient();
//        IQueue<String> inputQueue = hazelcastInstance.getQueue("inbound-queue");
//        ITopic<String> inputTopic = hazelcastInstance.getReliableTopic("inbound-topic");
//        while (true) {
//            String consumed = inputQueue.take();
//            String produced = UUID.randomUUID().toString();
//            System.out.println("Consumed: " + consumed + ", Produced: " + produced);
//            inputTopic.publish(consumed + "," + produced);
//        }
//    }

    public static void main(String[] args) throws InterruptedException {
//        Thread thread = new Thread(() -> {
//            System.out.println("testTryCatchFinally() = " + testTryCatchFinally("input"));
//            System.out.println("after call");
//        });
//        thread.start();
//        System.out.println("starting");
//        Thread.sleep(1000);
//        System.out.println("before interrupting");
//        thread.interrupt();
//        System.out.println("after interrupting");
        Map<String, Object> safe = Collections.synchronizedMap(new LinkedHashMap<String, Object>());
        Map<String, Serializable> thMap = new HashMap<>();
        thMap.put("testString", "RandomString");
        thMap.put("testMap", new ArrayList<>());

        System.out.println("thMap.get(\"testMap\") = " + thMap.get("testMap").getClass());
        Test test = Test.builder().build();
        System.out.println("test.val = " + test.val);
    }

    private static String testTryCatchFinally(String input) {
        try {
            Thread.sleep(10000);
        } catch (Exception ex) {
            System.out.println("in catch");
            //ex.printStackTrace();
            throw new IllegalStateException(ex.getMessage(), ex);
        }
        finally {
            return "finally";
        }
        //return "dummy";
    }

    @Builder
    static class Test {
        boolean val = true;
    }
}
