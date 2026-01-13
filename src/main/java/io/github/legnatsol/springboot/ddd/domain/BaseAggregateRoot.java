package io.github.legnatsol.springboot.ddd.domain;

import io.github.legnatsol.springboot.ddd.dto.EventObject;
import io.github.legnatsol.springboot.ddd.event.DomainEvent;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Base Aggregate Root
 * @param <ID>  identifier
 */
public abstract class BaseAggregateRoot<ID extends Identifier<?>> implements AggregateRoot<ID>, Serializable {
    /**
     * save mode
     */
    private SaveMode saveMode;

    /**
     * create time
     */
    protected Instant createTime;

    /**
     * update time
     */
    protected Instant updateTime;

    /**
     * domain events
     */
    private List<DomainEvent<?>> events;

    private List<DomainEvent<?>> allEvents() {
        if (events == null) {
            events = new ArrayList<>();
        }

        return events;
    }

    /**
     * emit domain event
     *
     * @param event domain event
     * @param <T>   event data type
     */
    protected <T extends EventObject> void emitEvent(DomainEvent<T> event) {
        allEvents().add(event);
    }

    /**
     * get domain events
     *
     * @return domain events
     */
    public List<DomainEvent<?>> getEvents() {
        return events;
    }

    /**
     * clear domain events
     */
    public void clearEvents() {
        events = null;
    }

    /**
     * constructor
     */
    protected BaseAggregateRoot() {
        // default save mode
        this.append();
    }

    /**
     * append
     */
    protected void append() {
        Instant now = Instant.now();
        saveMode = SaveMode.INSERT;
        createTime = now;
        updateTime = now;
    }

    /**
     * update
     */
    protected void update() {
        saveMode = SaveMode.UPDATE;
        updateTime = Instant.now();
    }

    /**
     * delete
     */
    protected void delete() {
        saveMode = SaveMode.DELETE;
        updateTime = Instant.now();
    }

    /**
     * get create time
     *
     * @return create time
     */
    public Instant getCreateTime() {
        return createTime;
    }

    /**
     * get update time
     *
     * @return update time
     */
    public Instant getUpdateTime() {
        return updateTime;
    }

    /**
     * set create time
     *
     * @param createTime create time
     */
    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    /**
     * set update time
     *
     * @param updateTime update time
     */
    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public SaveMode getSaveMode() {
        return saveMode;
    }
}
