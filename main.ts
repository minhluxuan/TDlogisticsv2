const FormData = require("form-data");
import axios, { AxiosResponse } from "axios";

export enum Role {
    CUSTOMER,
    ADMIN,
    MANAGER,
    HUMAN_RESOURCE_MANAGER,
    TELLER,
    COMPLAINTS_SOLVER,
    AGENCY_MANAGER,
    AGENCY_HUMAN_RESOURCE_MANAGER,
    AGENCY_TELLER,
    AGENCY_COMPLAINTS_SOLVER,
    SHIPPER,
    DRIVER,
    TRANSPORT_PARTNER_REPRESENTOR
};

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
            console.log("Error searching customer information: ", error?.response?.data);
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

export interface CheckingExistOrderCriteria {
    orderId: string,
}

export interface GettingOrdersCriteria {
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


export interface UpdatingOrderCriteria {
    orderId: string,
}

export interface UpdatingOrderPayload {
    mass?: number,
    height?: number,
    width?: number,
    length?: number,
    COD?: number,
    statusCode?: number, 
}

export interface CancelingOrderCriteria {
    orderId: string,
}

export interface CalculateFeePayload {
    serviceType: string,
    provinceSource: string,
    provinceDest: string,
    mass: number
}

export class OrdersOperation {
    private baseUrl: string;
    constructor() {
        this.baseUrl = "https://api2.tdlogistics.net.vn/v2/orders";
    }

    async create(payload: UpdatingOrderPayload) {
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

    async get(payload: GettingOrdersCriteria) {
        try {
            const response = await axios.post(`${this.baseUrl}/search`, payload, {
                withCredentials: true,
            });

            return { error: response.data.error, data: response.data.data, message: response.data.message };
        } catch (error: any) {
            console.log("Error getting orders: ", error?.response?.data);
            console.error("Request that caused the error: ", error?.request);
            return { error: error?.response?.data, request: error?.request, status: error.response ? error.response.status : null };
        }
    }

    async checkExist(params: CheckingExistOrderCriteria) {
        try {
            const response = await axios.get(`${this.baseUrl}/check?orderId=${params.orderId}`, {
                withCredentials: true,
            });

            return { error: response.data.error, exist: response.data.existed, message: response.data.message };
        } catch (error: any) {
            console.log("Error checking exist order: ", error?.response?.data);
            console.error("Request that caused the error: ", error?.request);
            return { error: error?.response?.data, request: error?.request, status: error.response ? error.response.status : null };
        }
    }

    async update(payload: UpdatingOrderPayload, params: UpdatingOrderCriteria) {
        try {
            const response = await axios.put(`${this.baseUrl}/update?orderId=${params.orderId}`, payload, {
                withCredentials: true,
            });

            return { error: response.data.error, data: response.data.data, message: response.data.message };
        } catch (error: any) {
            console.log("Error updating order: ", error?.response?.data);
            console.error("Request that caused the error: ", error?.request);
            return { error: error?.response?.data, request: error?.request, status: error.response ? error.response.status : null };
        }
    }

    async cancel(params: CancelingOrderCriteria) {
        try {
            const response = await axios.delete(`${this.baseUrl}/cancel?orderId=${params.orderId}`, {
                withCredentials: true,
            });

            return { error: response.data.error, message: response.data.message };
        } catch (error: any) {
            console.log("Error canceling order: ", error?.response?.data);
            console.error("Request that caused the error: ", error?.request);
            return { error: error?.response?.data, request: error?.request, status: error.response ? error.response.status : null };
        }
    }

    async calculateFee(payload: CalculateFeePayload) {
        try {
            const response = await axios.post(`${this.baseUrl}/calculate_fee`, payload, {
                withCredentials: true,
            });

            return { error: response.data.error, message: response.data.message, data: response.data.data };
        } catch (error: any) {
            console.log("Error calculating fee: ", error?.response?.data);
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

export interface CreatingStaffByAgencyPayload {
	fullname: string,
    dateOfBirth: string,
    cccd: string,
    role: Role,
    position: string,
    salary: number,
    paid_salary: number,
    province: string,
    district: string,
    town: string,
    detailAddress: string,
    managedWards?: Array<String>
}

export interface CreatingStaffByAdminPayload {
    agencyId: string,
    fullname: string,
    dateOfBirth: string,
    cccd: string,
    role: Role,
    position: string,
    salary: number, 
    paidSalary: number,
    province: string,
    district: string,
    town: string,
    detailAddress: string,
    managedWards?: Array<String>
}
  
export interface FindingStaffByStaffCriteria {
    staffId: string,
}
  
export interface FindingStaffByAdminCriteria {
    staffId?: string,
    fullname?: string,
    dateOfBirth?: string, 
    cccd?: string, 
    role?: Role,
    province?: string,
    district?: string,
    town?: string,
}
  
export interface UpdatingStaffPayload {
    fullname?: string,
    username?: string,
    dateOfBirth?: string,
    role?: Role,
    salary?: number, 
    paidSalary?: string, 
    province?: string,
    district?: string,
    town?: string,
    detailAddress?: string,
    managedWards?: Array<String>
}
  
export interface UpdatingStaffCriteria {
    staffId: string,
}
  
export interface DeletingStaffCriteria {
    staffId: string,
};
  
export interface UpdatingAvatarStaffPayload {
    avatar: File,
};
  
export interface FindingAvatarCriteria {
    staffId: string,
}
  
export class StaffOperation {
	private baseUrl: string;

	constructor() {
		this.baseUrl = "https://api2.tdlogistics.net.vn/v2/staffs";
	}

	// ROLE: any
	async getAuthenticatedStaffInfo() {
        try {
            const response: AxiosResponse = await axios.get(`${this.baseUrl}/`, {
                withCredentials: true,
            });
            
            return { error: response.data.error, data: response.data.info, message: response.data.message };
        } catch (error: any) {
            console.log("Error get authenticated staff information: ", error?.response?.data);
            console.error("Request that caused the error: ", error?.request);
            return { error: error?.response?.data, request: error?.request, status: error.response ? error.response.status : null };
        }
    }

	// ROLE: ADMIN, TELLER, HUMAN_RESOURCE_MANAGER, COMPLAINTS_SOLVER, AGENCY_MANAGER, AGENCY_HUMAN_RESOURCE_MANAGER
	async findByAdmin(conditions: FindingStaffByAdminCriteria) {
		try {
			const response: AxiosResponse = await axios.post(`${this.baseUrl}/search`, conditions, {
				withCredentials: true,
			});
			
			return { error: response.data.error, data: response.data.data, message: response.data.message };
		}     
		catch (error: any) {
			console.log("Error getting staffs: ", error?.response?.data);
            console.error("Request that caused the error: ", error?.request);
            return { error: error?.response?.data, request: error?.request, status: error.response ? error.response.status : null };
		}
	}

	// ROLE: ADMIN, MANAGER, HUMAN_RESOURCE_MANAGER
	async createByAdmin(info: CreatingStaffByAdminPayload) {
		try {
			const response: AxiosResponse = await axios.post(`${this.baseUrl}/create`, info, {
				withCredentials: true,
			});
			
			return { error: response.data.error, message: response.data.message, data: response.data.data };
		} 
		catch (error: any) {
			console.log("Error create new staff: ", error?.response?.data);
            console.error("Request that caused the error: ", error?.request);
            return { error: error?.response?.data, request: error?.request, status: error.response ? error.response.status : null };
		}
	}

	// ROLE: AGENCY_MANAGER, AGENCY_HUMAN_RESOURCE_MANAGER
	async createByAgency(info: CreatingStaffByAgencyPayload) {
		try {
			const response: AxiosResponse = await axios.post(`${this.baseUrl}/create`, info, {
				withCredentials: true,
			});
			
			const data = response.data;
			return { error: data.error, message: data.message, data: response.data.data };
		} 
		catch (error: any) {
			console.log("Error create new staff: ", error?.response?.data);
            console.error("Request that caused the error: ", error?.request);
            return { error: error?.response?.data, request: error?.request, status: error.response ? error.response.status : null };
		}
	}

	// ROLE: ADMIN, MANAGER, HUMAN_RESOURCE_MANAGER, AGENCY_MANAGER, AGENCY_HUMAN_RESOURCE_MANAGER
	async update(info: UpdatingStaffPayload, condition: UpdatingStaffCriteria) {
		try {
			const response: AxiosResponse = await axios.put(`${this.baseUrl}/update?staffId=${condition.staffId}`, info, {
				withCredentials: true,
			});
			
			const data = response.data;
			return { error: data.error, message: data.message, data: response.data.data };
		} 
		catch (error: any) {
			console.log("Error create new staff: ", error?.response?.data);
            console.error("Request that caused the error: ", error?.request);
            return { error: error?.response?.data, request: error?.request, status: error.response ? error.response.status : null };
		}
	}

	// ROLE: ADMIN, MANAGER, HUMAN_RESOURCE_MANAGER, AGENCY_MANAGER, AGENCY_HUMAN_RESOURCE_MANAGER
	async deleteStaff(condition: DeletingStaffCriteria) {
		try {
			const response = await axios.delete(`${this.baseUrl}/delete?staffId=${condition.staffId}`, {
				withCredentials: true,
			});

			return { error: response.data.error, message: response.data.message, data: response.data.data };
		} 
		catch (error: any) {
			console.log("Error deleting staff: ", error?.response?.data);
            console.error("Request that caused the error: ", error?.request);
            return { error: error?.response?.data, request: error?.request, status: error.response ? error.response.status : null };
		}
	}

	// ROLE: ADMIN, MANAGER, HUMAN_RESOURCE_MANAGER, AGENCY_MANAGER, AGENCY_HUMAN_RESOURCE_MANAGER
	async updateAvatar(info: UpdatingAvatarStaffPayload, condition: UpdatingStaffCriteria) {
		try {       
			// Tạo FormData object và thêm hình ảnh vào đó
			const formData = new FormData();
			formData.append('avatar', info.avatar);
	
			// Gửi yêu cầu POST để tải lên hình ảnh
			const response: AxiosResponse = await axios.patch(`${this.baseUrl}/avatar/update?staffId=${condition.staffId}`, formData , {
				withCredentials: true,
			});

            return { error: response.data.error, message: response.data.message, data: response.data.data };
		} catch (error: any) {
			console.error('Error uploading image:', error?.response?.data);
            console.error("Request that caused the error: ", error?.request);
            return { error: error?.response?.data, request: error?.request, status: error.response ? error.response.status : null }; // Ném lỗi để xử lý bên ngoài
		}   
	}

	// ROLE: any
	async getAvatar (condition: FindingAvatarCriteria) {
		try {
            const response = await axios.get(`${this.baseUrl}/avatar/get?staffId=${condition.staffId}`, {
                withCredentials: true,
                responseType: 'arraybuffer',
            });
    
            return response.data;
        } catch (error: any) {
            console.error("Error getting avatar: ", error);
            return error.response.data;
        }
	}
}