package io.github.legnatsol.springboot.ddd.event;

import io.github.legnatsol.springboot.ddd.dto.EventObject;

import java.io.Serializable;

/**
 * Event
 * @param <T> Event Data
 */
public class Event<T extends EventObject> implements Serializable {
    /**
     * Event ID
     */
    private String id;

    /**
     * Event Type
     */
    private String type;

    /**
     * Event Publish Timestamp
     */
    private long timestamp;

    /**
     * Event Data
     */
    private T data;

    /**
     * Default constructor
     */
    public Event() {
    }

    /**
     * Constructor with all fields
     *
     * @param id        event id
     * @param type      event type
     * @param data      event data
     * @param timestamp event timestamp
     */
    public Event(String id, String type, T data, long timestamp) {
        this.id = id;
        this.type = type;
        this.data = data;
        this.timestamp = timestamp;
    }

    /**
     * Get event id
     *
     * @return event id
     */
    public String getId() {
        return id;
    }

    /**
     * Set event id
     *
     * @param id event id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get event type
     *
     * @return event type
     */
    public String getType() {
        return type;
    }

    /**
     * Set event type
     *
     * @param type event type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get event timestamp
     *
     * @return event timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Set event timestamp
     *
     * @param timestamp event timestamp
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Get event data
     *
     * @return event data
     */
    public T getData() {
        return data;
    }

    /**
     * Set event data
     *
     * @param data event data
     */
    public void setData(T data) {
        this.data = data;
    }
}
