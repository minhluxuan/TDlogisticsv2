package project.tdlogistics.fee.entities;

import java.util.Objects;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

// Do not change
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response<T> {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer status;
    
    private boolean error;
    private String message;
    private T data;

    public Response() {
    }

    public Response(boolean error, String message, T data) {
        this.error = error;
        this.message = message;
        this.data = data;
    }

    public Response(Integer status, boolean error, String message, T data) {
        this.error = error;
        this.message = message;
        this.data = data;
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean getError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response<?> that = (Response<?>) o;
        return error == that.error && Objects.equals(message, that.message) && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(error, message, data);
    }

    @Override
    public String toString() {
        return "Response [error=" + error + ", message=" + message + ", data=" + data + "]";
    }
}
