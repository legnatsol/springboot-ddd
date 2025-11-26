package io.github.legnatsol.springboot.ddd.exception;

import io.github.legnatsol.response.models.ResponseError;

/**
 * Business Exception
 */
public class BizException extends RuntimeException {
    /**
     * Response error
     */
    private ResponseError error;

    /**
     * Constructor with response error
     *
     * @param error response error
     */
    public BizException(ResponseError error) {
        super(error.getMessage());
        this.error = error;
    }

    /**
     * Get response error
     *
     * @return response error
     */
    public ResponseError getError() {
        return error;
    }

    /**
     * Set response error
     *
     * @param error response error
     */
    public void setError(ResponseError error) {
        this.error = error;
    }
}
