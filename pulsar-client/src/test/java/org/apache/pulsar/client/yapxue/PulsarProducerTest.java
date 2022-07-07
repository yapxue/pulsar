package org.apache.pulsar.client.yapxue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.ConsumerInterceptor;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.interceptor.ProducerInterceptor;
import org.apache.pulsar.client.impl.ClientBuilderImpl;
import org.apache.pulsar.client.impl.schema.JSONSchema;
import org.junit.Test;

@Slf4j
public class PulsarProducerTest {

    @Test
    public void test1() throws PulsarClientException, InterruptedException {
        PulsarClient client = buildClient();
        Map<String, String> schemaProps = new HashMap();
        schemaProps.put("app", "bes2testapp");
        Producer<LogRecord> producer = client.newProducer(JSONSchema.of(LogRecord.class, schemaProps))
                .intercept(new MyInterceptor())
                .topic("public/default/logger")
                .create();
        Consumer<LogRecord> consumer = client.newConsumer(JSONSchema.of(LogRecord.class, schemaProps))
                .subscriptionName("test")
                .startMessageIdInclusive()
                .isAckReceiptEnabled(true)
                .intercept(new ConsumerInterceptor())
                .topic("public/default/logger")
                .subscribe();
        final int total = 10;
        final CountDownLatch latch = new CountDownLatch(total);
//        new Thread(() -> {
//            try {
//                for (int i=0;i<total;i++) {
//                    MessageId id = producer.send(LogRecord.buildDefault());
//                    log.info("sent {}", id);
//                }
//            } catch (PulsarClientException e) {
//                log.error("produce failed {}", e.getMessage(), e);
//            }
//        }).start();

        new Thread(() -> {
            while (latch.getCount() > 0) {
                try {
                    Message<LogRecord> message = consumer.receive();
                    if (message.getSequenceId() % 2 == 0) {
                        consumer.acknowledgeAsync(message).whenComplete((v, e) -> {
                            latch.countDown();
                        });
                    }
                } catch (PulsarClientException e) {
                    log.error("consume failed {}", e.getMessage(), e);
                }
            }
        }).start();

        latch.await(10, TimeUnit.MINUTES);
    }

    private PulsarClient buildClient() {
        try {
            PulsarClient client = new ClientBuilderImpl()
                    .serviceUrl("pulsar://localhost:6650")
                    .build();
            return client;
        }catch (PulsarClientException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    static class MyInterceptor implements ProducerInterceptor {

        public void close() {

        }

        public boolean eligible(Message message) {
            return true;
        }

        public Message beforeSend(Producer producer, Message message) {
            log.info("beforeSend {}", message.getValue().toString());
            return message;
        }

        public void onSendAcknowledgement(Producer producer, Message message, MessageId msgId, Throwable exception) {
            log.info("onSendAcknowledgement");
        }
    }

    static class ConsumerInterceptor implements org.apache.pulsar.client.api.ConsumerInterceptor<LogRecord> {

        @Override
        public void close() {

        }

        @Override
        public Message beforeConsume(Consumer consumer, Message message) {
            log.info("beforeConsume {}", message.getValue().toString());
            return message;
        }

        @Override
        public void onAcknowledge(Consumer consumer, MessageId messageId, Throwable exception) {
            log.info("onAck {}", messageId);
        }

        @Override
        public void onAcknowledgeCumulative(Consumer consumer, MessageId messageId, Throwable exception) {
            log.info("onAckCumulative {}", messageId);
        }

        @Override
        public void onAckTimeoutSend(Consumer consumer, Set set) {
            log.info("onAckTimeoutSend {}");
        }

        @Override
        public void onNegativeAcksSend(Consumer consumer, Set set) {
            log.info("onNegativeAcksSend {}");
        }
    }
}
