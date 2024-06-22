const axios = require("axios");
const zalo = require("../databases/Zalo");

const refreshToken = async () => {
    const key = await zalo.gettingKey();
    const data = {
        refresh_token: key.refresh_token,
        app_id: "2757135236453764158",
        grant_type: "refresh_token",
    };
    const response = await axios.post(" https://oauth.zaloapp.com/v4/oa/access_token", data, {
        headers: {
            "Content-Type": "application/x-www-form-urlencoded",
            secret_key: "6SdZXHd4ZG2SYsGINzdG",
        },
    });

    console.log(response.data);
    await zalo.updateKey(response.data.access_token, response.data.refresh_token);
    return response;
};

const confirmPaymet = async (phone, fee, customer_name, order_id) => {
    const formattedPhoneNumber = "+84" + phone.substring(1);
    let data = {
        phone: formattedPhoneNumber,
        template_id: "330875",
        template_data: {
            fee: fee,
            customer_name: customer_name,
            order_id: order_id,
        },
    };

    try {
        const key = await zalo.gettingKey();
        const accessToken = key.access_token;

        const response = await axios.post("https://business.openapi.zalo.me/message/template", data, {
            headers: {
                "Content-Type": "application/json",
                access_token: accessToken,
            },
        });
        console.log(response);
    } catch (error) {
        console.log(error);
        throw new Error(`Error message: ${err.message}`);
    }
};

const confirmCreateOrder = async (
    phone,
    shipper,
    service_type,
    name_receiver,
    address_source,
    address_destination,
    cod,
    name_sender,
    order_id
) => {
    const formattedPhoneNumber = "+84" + phone.substring(1);
    let data = {
        phone: formattedPhoneNumber,
        template_id: "330874",
        template_data: {
            shipper: shipper,
            service_type: service_type,
            name_receiver: name_receiver,
            address_source: address_source,
            address_destination: address_destination,
            COD: cod,
            name_sender: name_sender,
            order_id: order_id,
        },
    };

    try {
        const key = await zalo.gettingKey();
        const accessToken = key.access_token;

        const response = await axios.post("https://business.openapi.zalo.me/message/template", data, {
            headers: {
                "Content-Type": "application/json",
                access_token: accessToken,
            },
        });
        console.log(response);
    } catch (error) {
        console.log(error);
        throw new Error(`Error message: ${err.message}`);
    }
};

const confirmOrder = async (phone, order_code, date, price, name, status) => {
    const formattedPhoneNumber = "+84" + phone.substring(1);

    const data = {
        phone: formattedPhoneNumber,
        template_id: "330287",
        template_data: {
            order_code: order_code,
            date: date,
            price: price,
            name: name,
            phone_number: formattedPhoneNumber,
            status: status,
        },
    };
    try {
        const key = await zalo.gettingKey();
        const accessToken = key.access_token;

        const response = await axios.post("https://business.openapi.zalo.me/message/template", data, {
            headers: {
                "Content-Type": "application/json",
                access_token: accessToken,
            },
        });
        console.log(response);
    } catch {
        console.log(error);
        throw new Error(`Error message: ${err.message}`);
    }
};

// refreshToken();

// confirmCreateOrder(
//     "0976481171",
//     "Ly Tu Trong",
//     "fast delivery",
//     "Ho Thanh Nhan",
//     "114 District 9",
//     "215 LTK District 10",
//     "118k",
//     "Nguyen Van Troi",
//     "2212234"
// );

// confirmPaymet("0976481171", "118k", "Ho Thanh Nhan", "22123323");

// confirmOrder("0976481171", "123213", "21/6/2024", "118k", "Ho Thanh Nhan", "success");

module.exports = {
    confirmPaymet,
    confirmCreateOrder,
    confirmOrder,
    refreshToken,
};
