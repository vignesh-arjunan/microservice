package com.micro.app;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.collection.IQueue;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;

import java.util.UUID;

public class HazelcastQueueConsumer {
    public static void main(String[] args) throws InterruptedException {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getNetworkConfig().addAddress("127.0.0.1");
        HazelcastInstance hazelcastInstance = HazelcastClient.newHazelcastClient();
        IQueue<String> inputQueue = hazelcastInstance.getQueue("inbound-queue");
        ITopic<String> inputTopic = hazelcastInstance.getReliableTopic("inbound-topic");
        while (true) {
            String consumed = inputQueue.take();
            String produced = UUID.randomUUID().toString();
            System.out.println("Consumed: " + consumed + ", Produced: " + produced);
            inputTopic.publish(consumed + "," + produced);
        }
    }
}
