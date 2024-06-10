const fileService = require("../services/fileService");
const Response = require("../models/Response");

const handle = async (channel, msg) => {
    try {
        const messageContent = msg.content.toString();
        console.log("Received message");
        const request = JSON.parse(messageContent);
        let response;
        switch (request.operation) {
            case "storeFile":
                response = await fileService.storeFile(request.params.path, request.params.option, request.params.filename, request.payload);
                break;
            case "getFile":
                response = await fileService.getFile(request.params.path);
                channel.sendToQueue(
                    msg.properties.replyTo,
                    Buffer.from(response),
                    { correlationId: msg.properties.correlationId }
                );
        
                channel.ack(msg);   
                return;
            case "deleteFile":
                response = await fileService.deleteFile(request.params.path);
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