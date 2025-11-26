package io.github.legnatsol.springboot.ddd.domain;

import io.github.legnatsol.springboot.ddd.event.DomainEvent;
import io.github.legnatsol.springboot.ddd.event.DomainEventData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
    protected Date createTime;

    /**
     * update time
     */
    protected Date updateTime;

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
    protected <T extends DomainEventData> void emitEvent(DomainEvent<T> event) {
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
        Date now = new Date();
        saveMode = SaveMode.INSERT;
        createTime = now;
        updateTime = now;
    }

    /**
     * update
     */
    protected void update() {
        saveMode = SaveMode.UPDATE;
        updateTime = new Date();
    }

    /**
     * delete
     */
    protected void delete() {
        saveMode = SaveMode.DELETE;
        updateTime = new Date();
    }

    /**
     * get create time
     *
     * @return create time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * get update time
     *
     * @return update time
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * set create time
     *
     * @param createTime create time
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * set update time
     *
     * @param updateTime update time
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public SaveMode getSaveMode() {
        return saveMode;
    }
}
