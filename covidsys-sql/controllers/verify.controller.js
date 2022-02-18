const { verifyVerificationToken, verifyVerificationCodeRequest } = require('../store/token');
const Exception = require('../exceptions/Exception');
const VerificationCode = require('../models/VerificationCode');
const Student = require('../models/Student');

const mailer = require('../smtp/email');
const sendCode = require('../smtp/email');

exports.verify = async (req, res, next) => {
   try {
        // get encrypted email
    const { token } = req.body;

    // if token exists, decrypt
    if (!token) 
        return next(new Exception('No token provided', 403));
    
    // decrypt token
    const credential = verifyVerificationToken(token);

    // if credential is not valid, return error
    if (!credential)
        return next(new Exception('Invalid token', 403));

    // if credential is valid check in database if user is a student
    const { givenName, middleName, familyName, email, suffix, institution_id:student_id } = credential;
    
    const student = await new Student().findOne({
        givenName,
        middleName,
        familyName,
        suffix,
        student_id
    });

    // if student is not found, return error
    if (!student)
        return next(new Exception('You are not allowed to create an account!', 403));

    // if student is found, generate verification code and send to email
    await new VerificationCode()
        .generate()
        .save({
            email
        })
        .then(result => {
            // if result.affectedRows is not 1, return error
            if (result.affectedRows < 1)
                return next(new Exception('Unable to create verification code. Try again later!', 500));
            return result.code;
        }) 
        .then(async code=>{
            // send verification code to email
            await sendCode({
                email,
                code
            })
            .then(_ => {
                res.status(200).json({
                    message: 'Verification code sent to email!'
                });
            })
        })
        .catch(err => {
            if(err.errno == 1062)
                return next(new Exception('Verification code already sent!', 400));
            return next(err);
        });
   } catch (error) {
        return next(error);
   }

}

exports.validateCode = async (req, res, next) => {
    try {
        // get token
        const { token } = req.body;
        if(!token)
            return next(new Exception('No token provided', 403));

        // decrypt token
        const credential = verifyVerificationCodeRequest(token);
        
        // if credential is not valid, return error
        if(!credential)
            return next(new Exception('Invalid token', 403));
        
        // check if code is valid
        const { email, code } = credential; 
        const verificationCode = await new VerificationCode().findOne({
            email,
            code
        });

        // if code is not valid, return error
        if (!verificationCode)
            return next(new Exception('Invalid verification code.', 400));


        // if code is valid, delete code from database
        await new VerificationCode().remove({
            email,
            code
        }).then(()=> {
            res.json({
                message: 'Success!'
            });
        })    
    } catch (error) {
        return next(error);
    }
}