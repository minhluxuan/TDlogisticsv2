class Response {
    #_status;
    #_error;
    #_message;
    #_data;

    constructor(status, error, message, data) {
        this.#_status = status;
        this.#_error = error;
        this.#_message = message;
        this.#_data = data;
    }

    toJSON() {
        return {
            status: this.#_status,
            error: this.#_error,
            message: this.#_message,
            data: this.#_data
        };
    }
}

module.exports = Response;