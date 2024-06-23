package project.tdlogistics.transport_partners.entities;

import java.util.HashMap;
import java.util.Map;

public class Request<T> {
    private String operation;
    private HashMap<String, Object> params;
    private T payload;
    
    public Request() {
    }

    public Request(String operation, HashMap<String, Object> params, T payload) {
        this.operation = operation;
        this.params = params;
        this.payload = payload;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(HashMap<String, Object> params) {
        this.params = params;
    }
    
    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}
