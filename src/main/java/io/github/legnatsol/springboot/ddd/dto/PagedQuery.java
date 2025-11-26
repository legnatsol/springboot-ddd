package io.github.legnatsol.springboot.ddd.dto;

/**
 * Paging Query
 */
public class PagedQuery extends Query {

    /**
     * constructor
     */
    public PagedQuery() {}

    /**
     * current page number
     */
    private int current;

    /**
     * page size
     */
    private int pageSize;

    /**
     * Get begin index
     *
     * @return begin index
     */
    public int getBegin() {
        return (current - 1) * pageSize;
    }

    /**
     * Get current page number
     *
     * @return current page number
     */
    public int getCurrent() {
        return current;
    }

    /**
     * Set current page number
     *
     * @param current current page number
     */
    public void setCurrent(int current) {
        this.current = current;
    }

    /**
     * Get page size
     *
     * @return page size
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Set page size
     *
     * @param pageSize page size
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
