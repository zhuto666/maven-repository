package com.mail.core.properties;

/**
 * @author Kevin
 * @Date: 2019/4/19 17:09
 */
public interface DeadLetterKeyProperties {

    /**
     * 死信队列 交换机标识符（固定）
     */
    String DEAD_LETTER_QUEUE_KEY = "x-dead-letter-exchange";

    /**
     * 死信队列交换机标识符（固定）
     */
    String DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";
}
