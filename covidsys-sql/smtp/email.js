const nodemailer = require('nodemailer');
const { google } = require('googleapis');
const OAuth2 = google.auth.OAuth2;

const OAuth2Client = new OAuth2(
    process.env.SMTP_CLIENT_ID,
    process.env.SMTP_CLIENT_SECRET
);
OAuth2Client.setCredentials({
        refresh_token: process.env.SMTP_REFRESH_TOKEN
    });
const accessToken = OAuth2Client.getAccessToken();

const sendCode = ({email, code})=>{
    
    return new Promise((resolve, reject) => {

        let transporter = nodemailer.createTransport({
            service: 'gmail',
            auth: {
                type: 'OAuth2',
                user: process.env.SMTP_EMAIL,
                clientId: process.env.SMTP_CLIENT_ID,
                clientSecret: process.env.SMTP_CLIENT_SECRET,
                refreshToken: process.env.SMTP_REFRESH_TOKEN,
                accessToken
            }      
        });
        const mailOptions = {
            from: `Avax Team <${process.env.SMTP_EMAIL}>`,
            to: email,
            subject: `Avax - Verification Code - ${code}`,
            text: `Your verification code is ${code}`
        };

        transporter.sendMail(mailOptions, (error, info) => {
            if(error) reject(error);
            resolve(info);
        });
    });
}

module.exports = sendCode;