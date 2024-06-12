package project.tdlogistics.tasks.entities;

import java.util.List;

public class ListResponse {
    private int acceptedNumber;
    private List<String> acceptedArray;
    private int notAcceptedNumber;
    private List<String> notAcceptedArray;

    // Constructor, getters, and setters
    public ListResponse() {
        this.acceptedNumber = 0;
        this.acceptedArray = null;
        this.notAcceptedNumber = 0;
        this.notAcceptedArray = null;
    }

    public ListResponse(int acceptedNumber, List<String> acceptedArray, int notAcceptedNumber, List<String> notAcceptedArray) {
        this.acceptedArray = acceptedArray;
        this.acceptedNumber = acceptedNumber;
        this.notAcceptedArray = notAcceptedArray;
        this.notAcceptedNumber = notAcceptedNumber;
    }

    public void setAcceptedNumber(int acceptedNumber) {
        this.acceptedNumber = acceptedNumber;
    }

    public void setNotAcceptedNumber(int notAcceptedNumber) {
        this.notAcceptedNumber = notAcceptedNumber;
    }

    public void setAcceptedArray(List<String> acceptedArray) {
        this.acceptedArray = acceptedArray;
    }

    public void setNotAcceptedArray(List<String> notAcceptedArray) {
        this.notAcceptedArray = notAcceptedArray;
    }

    public int getAcceptedNumber() {
        return this.acceptedNumber;
    }

    public int getNotAcceptedNumber() {
        return this.notAcceptedNumber;
    }

    public List<String> getAcceptedArray() {
        return this.acceptedArray;
    }

    public List<String> getNotAcceptedArray() {
        return this.notAcceptedArray;
    }

}
