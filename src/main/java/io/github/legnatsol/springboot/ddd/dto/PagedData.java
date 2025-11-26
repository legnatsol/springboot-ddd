package io.github.legnatsol.springboot.ddd.dto;

import io.github.legnatsol.response.models.PagedResponse;

import java.io.Serial;
import java.util.List;

/**
 * Paging Data
 * @param <T>   data type
 */
public class PagedData<T> extends DTO {

    @Serial
    private static final long serialVersionUID = -1L;

    /**
     * constructor
     */
    public PagedData() {
    }

    /**
     * current page
     */
    private int current;

    /**
     * total records
     */
    private int total;

    /**
     * page size
     */
    private int pageSize;

    /**
     * data list
     */
    private List<T> data;

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
     * Get total records count
     *
     * @return total records count
     */
    public int getTotal() {
        return total;
    }

    /**
     * Set total records count
     *
     * @param total total records count
     */
    public void setTotal(int total) {
        this.total = total;
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

    /**
     * Get data list
     *
     * @return data list
     */
    public List<T> getData() {
        return data;
    }

    /**
     * Set data list
     *
     * @param data data list
     */
    public void setData(List<T> data) {
        this.data = data;
    }

    /**
     * Create paged data
     *
     * @param qry       query
     * @param total     total records count
     * @param list      data list
     * @param <T>       data type
     * @return paged data
     */
    public static <T> PagedData<T> of(PagedQuery qry, long total, List<T> list) {
        return of(qry, (int) total, list);
    }

    /**
     * Create paged data
     *
     * @param qry       query
     * @param total     total records count
     * @param list      data list
     * @param <T>       data type
     * @return paged data
     */
    public static <T> PagedData<T> of(PagedQuery qry, int total, List<T> list) {
        PagedData<T> pagedData = new PagedData<>();
        pagedData.setCurrent(qry.getCurrent());
        pagedData.setPageSize(qry.getPageSize());
        pagedData.setTotal(total);
        pagedData.setData(list);
        return pagedData;
    }

    /**
     * Convert to paged response
     *
     * @param pagedData paged data
     * @param <T>       data type
     * @return paged response
     */
    public static <T> PagedResponse<T>  toPagedResponse(PagedData<T> pagedData) {
        PagedResponse<T> pagedResponse = new PagedResponse<>();
        pagedResponse.setCurrent(pagedData.getCurrent());
        pagedResponse.setPageSize(pagedData.getPageSize());
        pagedResponse.setTotal(pagedData.getTotal());
        pagedResponse.setData(pagedData.getData());
        return pagedResponse;
    }
}
