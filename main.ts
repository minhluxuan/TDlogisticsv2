const FormData = require("form-data");
import axios, { AxiosResponse } from "axios";

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
        this.baseUrl = "https://api2.tdlogistics.net.vn/v2/auth/otp";
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
            const response = await axios.post(`${this.baseUrl}/verify`, payload, {
                withCredentials: true
            });
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
        this.baseUrl = "https://api2.tdlogistics.net.vn/v2/customers";
    }

    async getAuthenticatedCustomerInfo() {
        try {
            const response = await axios.get(`${this.baseUrl}/`, {
                withCredentials: true
            });
            return { error: response.data.error, message: response.data.message, data: response.data.data };
        } catch (error) {
            console.log("Error getting authenticated customer info: ", error?.response?.data);
            console.error("Request that caused the error: ", error?.request);
            return { error: error?.response?.data, request: error?.request, status: error.response ? error.response.status : null };
        }
    }

    async updateInfo(params: UpdatingCustomerParams, payload: UpdatingCustomerPayload) {
        try {
            const response = await axios.put(`${this.baseUrl}/update?customerId=${params.customerId}`, payload, {
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
            const formData = new FormData();
			formData.append('avatar', payload.avatar);

            const response = await axios.put(`${this.baseUrl}/avatar/update`, formData, {
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
            const response = await axios.get(`${this.baseUrl}/avatar/get?customerId=${params.customerId}`, {
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

export interface CheckingExistOrderCondition {
    orderId: string,
}

export interface GettingOrdersConditions {
    orderId: string,
    nameReceiver?: string,
    phoneReceiver?: string,
    provinceSource?: string,
    districtSource?: string,
    wardSource?: string,
    provinceDest?: string,
    districtDest?: string,
    wardDest?: string,
    serviceType?: number,
}

export interface CreatingOrderByUserInformation {
    nameSender: string,
    nameReceiver: string,
    phoneNumberReceiver: string,
    mass: number,
    height: number,
    width: number,
    length: number,
    provinceSource: string,
    districtSource: string,
    wardSource: string,
    detailSource: string,
    provinceDest: string,
    districtDest: string,
    wardDest: string,
    detailDest: string,
    longSource: number,
    latSource: number,
    longDestination: number,
    latDestination: number,
    cod: number,
    serviceType: string,
}


export interface CreatingOrderByAdminAndAgencyInformation {
    nameSender: string,
    phoneNumberSender: string,
    nameReceiver: string,
    phoneNumberReceiver: string,
    mass: number,
    height: number,
    width: number,
    length: number,
    provinceSource: string,
    districtSource: string,
    wardSource: string,
    detailSource: string,
    provinceDest: string,
    districtDest: string,
    wardDest: string,
    detailDest: string,
    longSource: number,
    latSource: number,
    longDestination: number,
    latDestination: number,
    cod: number,
    serviceType: string,
}


export interface UpdatingOrderCondition {
    orderId: string,
}

export interface UpdatingOrderInfo {
    mass?: number,
    height?: number,
    width?: number,
    length?: number,
    COD?: number,
    statusCode?: number, 
}

export interface CancelingOrderCondition {
    orderId: string,
}



export class OrdersOperation {
    private baseUrl: string;
    constructor() {
        this.baseUrl = "https://api2.tdlogistics.net.vn/v2/orders";
    }

    async create(payload: UpdatingOrderInfo) {
        try {
            const response = await axios.put(`${this.baseUrl}/create`, payload, {
                withCredentials: true,
            });

            return { error: response.data.error, data: response.data.data, message: response.data.message };
        } catch (error: any) {
            console.log("Error updating order: ", error?.response?.data);
            console.error("Request that caused the error: ", error?.request);
            return { error: error?.response?.data, request: error?.request, status: error.response ? error.response.status : null };
        }
    }

    async get(payload: GettingOrdersConditions) {
        try {
            const response = await axios.post(`${this.baseUrl}/search`, payload, {
                withCredentials: true,
            });

            const data = response.data;
            return { error: data.error, data: data.data, message: data.message };
        } catch (error: any) {
            console.log("Error getting orders: ", error?.response?.data);
            console.error("Request that caused the error: ", error?.request);
            return { error: error?.response?.data, request: error?.request, status: error.response ? error.response.status : null };
        }
    }

    async checkExist(params: CheckingExistOrderCondition) {
        try {
            const response = await axios.get(`${this.baseUrl}/check?orderId=${params.orderId}`, {
                withCredentials: true,
            });

            const data = response.data;
            return { error: data.error, exist: data.existed, message: data.message };
        } catch (error: any) {
            console.log("Error checking exist order: ", error?.response?.data);
            console.error("Request that caused the error: ", error?.request);
            return { error: error?.response?.data, request: error?.request, status: error.response ? error.response.status : null };
        }
    }

    async update(payload: UpdatingOrderInfo, params: UpdatingOrderCondition) {
        try {
            const response = await axios.put(`${this.baseUrl}/update?orderId=${params.orderId}`, payload, {
                withCredentials: true,
            });

            const data = response.data;
            return { error: data.error, data: data.data, message: data.message };
        } catch (error: any) {
            console.log("Error updating order: ", error?.response?.data);
            console.error("Request that caused the error: ", error?.request);
            return { error: error?.response?.data, request: error?.request, status: error.response ? error.response.status : null };
        }
    }

    async cancel(params: CancelingOrderCondition) {
        try {
            const response = await axios.delete(`${this.baseUrl}/cancel?orderId=${params.orderId}`, {
                withCredentials: true,
            });

            const data = response.data;
            return { error: data.error, message: data.message };
        } catch (error: any) {
            console.log("Error canceling order: ", error?.response?.data);
            console.error("Request that caused the error: ", error?.request);
            return { error: error?.response?.data, request: error?.request, status: error.response ? error.response.status : null };
        }
    }
}

export interface AdministrativePayload {
    province?: string,
    district?: string,
    ward?: string
}

export class AdministrativeOperation {
    private baseUrl: string;
    constructor() {
        this.baseUrl = "https://api2.tdlogistics.net.vn/v2/administrative";
    }

    async get(conditions: AdministrativePayload) {
        try {
            const response: AxiosResponse = await axios.post(`${this.baseUrl}/search`, conditions, {
                withCredentials: true
            });

            return { error: response.data.error, data: response.data.data, message: response.data.message }
        } catch (error: any) {
            console.error("Error getting administrative: ", error?.response?.data);
            console.error("Request that caused the error: ", error?.request);
            return { error: error?.response?.data, request: error?.request, status: error.response ? error.response.status : null };
        }
    }
}