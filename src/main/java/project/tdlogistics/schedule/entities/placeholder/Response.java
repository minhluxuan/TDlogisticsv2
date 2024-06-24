package project.tdlogistics.schedule.entities.placeholder;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response<T> {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private HttpStatus status;

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

    public Response(HttpStatus status, boolean error, String message, T data) {
        this.error = error;
        this.message = message;
        this.data = data;
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
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
