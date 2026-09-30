package com.fintechautomation.ftatoken.common;

/**
 * Standard response envelope returned by every API endpoint.
 *
 * <pre>
 * {
 *     "code": 600,
 *     "errorCode": null,
 *     "errorMessage": "The request failed",
 *     "error": null,
 *     "data": null
 * }
 * </pre>
 */
public class ApiResult {

    public static final int SUCCESS_CODE = 200;
    public static final int FAILURE_CODE = 600;
    public static final String DEFAULT_FAILURE_MESSAGE = "The request failed";

    private Integer code;
    private Integer errorCode;
    private String errorMessage;
    private Object error;
    private Object data;

    public ApiResult() {
    }

    private ApiResult(Integer code, String errorMessage, Object error, Object data) {
        this.code = code;
        this.errorMessage = errorMessage;
        this.error = error;
        this.data = data;
    }

    public static ApiResult success() {
        return success(null);
    }

    public static ApiResult success(Object data) {
        return new ApiResult(SUCCESS_CODE, null, null, data);
    }

    public static ApiResult failure() {
        return failure(DEFAULT_FAILURE_MESSAGE);
    }

    public static ApiResult failure(String errorMessage) {
        return failure(errorMessage, null);
    }

    public static ApiResult failure(String errorMessage, Object error) {
        return new ApiResult(FAILURE_CODE, errorMessage, error, null);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    /** Reserved for application error codes; always {@code null} until those are defined. */
    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
