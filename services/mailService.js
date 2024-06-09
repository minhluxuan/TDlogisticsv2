const nodemailer = require("nodemailer");
const randomstring = require("randomstring");
const Response = require('../models/Response');
const moment = require("moment-timezone");

const transporter = nodemailer.createTransport({
    service: "gmail",
    auth: {
        user: "dungnt@tiendungcorp.com",
        pass: "kvdk hifp hxoq mbom",
    }
});

const createOTP = async (email) => {
    const otp = randomstring.generate({
        length: 4,
        charset: "numeric",
        min: 1000,
        max: 9999,
    });

    const mailOptions = {
        from: "Dịch vụ chuyển phát nhanh TDLogistics",
        to: email,
        subject: "Xác thực OTP cho ứng dụng TDLogistics",
        html: `<p>OTP xác thực:<br><br>
        <strong style="font-size: 20px; color: red;">${otp}</strong>
        <br><br>
        Quý khách vui lòng không tiết lộ OTP cho bất kỳ ai. OTP sẽ hết hạn sau 5 phút nữa.
        <br><br>
        Xin cảm ơn quý khách,<br>
        Đội ngũ kỹ thuật TDLogistics.
        </p>`,
    };

    try {
        await transporter.sendMail(mailOptions);
        const expires = moment().add(5, 'minutes').tz('Asia/Ho_Chi_Minh').format(); // Adjust to the current time zone
        return new Response(200, false, "Gửi otp qua email thành công", { otp, expires });
    } catch (error) {
        console.error("Error sending email:", error);
        return new Response(500, true, "Gửi otp qua email thất bại", null);
    }
}

module.exports = {
    createOTP
}
