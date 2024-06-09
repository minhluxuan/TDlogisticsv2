import axios from "axios";

export interface SendingOtp {
    phoneNumber: String,
    email: String,
}

export interface VerifyingOtp {
    phoneNumber: String,
    email: String,
    otp: String,
}

export class AuthOperation {
    private baseUrl: String;
    
    constructor() {
        this.baseUrl = "http://localhost:8081/v2/auth/otp";
    }

    async sendOtp(payload: SendingOtp) {
        try {
            const response = await axios.post(`${this.baseUrl}/send`, payload);
            return { error: response.data.error, message: response.data.message, data: response.data.data };
        } catch (error) {
            console.log("Error sending otp: ", error?.response?.data);
            console.error("Request that caused the error: ", error?.request);
            return { error: error?.response?.data, request: error?.request, status: error.response ? error.response.status : null };
        }
    }

    async verifyOtp(payload: VerifyingOtp) {
        try {
            const response = await axios.post(`${this.baseUrl}/verify`, payload);
            return { error: response.data.error, message: response.data.message, data: response.data.data };
        } catch (error) {
            console.log("Error verifying otp: ", error?.response?.data);
            console.error("Request that caused the error: ", error?.request);
            return { error: error?.response?.data, request: error?.request, status: error.response ? error.response.status : null };
        }
    }
}

interface UpdatingCustomerParams {
    customerId: String,
}

interface UpdatingCustomerPayload {
    fullname?: String,
    province?: String,
    district?: String,
    ward?: String,
    detailAddress?: String,
}

interface SearchingCustomerPayload {
    fullname?: String,
    province?: String,
    district?: String,
    ward?: String,
    detailAddress?: String,
}

interface UpdatingAvatarPayload {
    avatar: File
}

interface GettingAvatarParams {
    customerId: String
}

export class CustomerOperation {
    private baseUrl: String;

    constructor() {
        this.baseUrl = "http://localhost:8081/v2/customers";
    }

    async getAuthenticatedCustomerInfo() {
        try {
            const response = await axios.get(`${this.baseUrl}/`);
            return { error: response.data.error, message: response.data.message, data: response.data.data };
        } catch (error) {
            console.log("Error getting authenticated customer info: ", error?.response?.data);
            console.error("Request that caused the error: ", error?.request);
            return { error: error?.response?.data, request: error?.request, status: error.response ? error.response.status : null };
        }
    }

    async updateInfo(params: UpdatingCustomerParams, payload: UpdatingCustomerPayload) {
        try {
            const response = await axios.post(`${this.baseUrl}/update?customerId=${params.customerId}`, payload, {
                withCredentials: true
            });

            return { error: response.data.error, message: response.data.message, data: response.data.data };
        } catch (error) {
            console.log("Error updating customer information: ", error?.response?.data);
            console.error("Request that caused the error: ", error?.request);
            return { error: error?.response?.data, request: error?.request, status: error.response ? error.response.status : null };
        }
    }

    async search(payload: SearchingCustomerPayload) {
        try {
            const response = await axios.post(`${this.baseUrl}/search`, payload, {
                withCredentials: true
            });

            return { error: response.data.error, message: response.data.message, data: response.data.data };
        } catch (error) {
            console.log("Error updating customer information: ", error?.response?.data);
            console.error("Request that caused the error: ", error?.request);
            return { error: error?.response?.data, request: error?.request, status: error.response ? error.response.status : null };
        }
    }

    async updateAvatar(payload: UpdatingAvatarPayload) {
        try {
            const response = await axios.post(`${this.baseUrl}/avatar/update`, payload, {
                withCredentials: true
            });

            return { error: response.data.error, message: response.data.message, data: response.data.data };
        } catch (error) {
            console.log("Error updating avatar: ", error?.response?.data);
            console.error("Request that caused the error: ", error?.request);
            return { error: error?.response?.data, request: error?.request, status: error.response ? error.response.status : null };
        }
    }

    async getAvatar(params: GettingAvatarParams) {
        try {
            const response = await axios.get(`${this.baseUrl}/avatar/update?customerId=${params.customerId}`, {
                withCredentials: true
            });

            return response.data;
        } catch (error) {
            console.log("Error getting avatar: ", error?.response?.data);
            console.error("Request that caused the error: ", error?.request);
            return { error: error?.response?.data, request: error?.request, status: error.response ? error.response.status : null };
        }
    }
}
