const mailService = require("../services/mailService");
const Response = require("../models/Response");

const handle = async (channel, msg) => {
    try {
        const messageContent = msg.content.toString();
        console.log(" [x] Received '%s'", messageContent);
        const request = JSON.parse(messageContent);
        let response;
        switch (request.operation) {
            case "sendOtp":
                response = await mailService.createOTP(request.payload.email);
                break;
            default:
                response = new Response(500, true, "Đã xảy ra lỗi", null)
        }
        
        channel.sendToQueue(
            msg.properties.replyTo,
            Buffer.from(JSON.stringify(response.toJSON())),
            { correlationId: msg.properties.correlationId }
        );

        channel.ack(msg);   
    } catch (error) {
        console.log(error);
        channel.sendToQueue(
            msg.properties.replyTo,
            Buffer.from(JSON.stringify(new Response(500, true, "Đã xảy ra lỗi", null).toJSON())),
            { correlationId: msg.properties.correlationId }
        );

        channel.ack(msg);  
    }
}

module.exports = {
    handle
}