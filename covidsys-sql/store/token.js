const { sign, verify } = require("jsonwebtoken");

const accessTokenKey = process.env.ACCESS_TOKEN_KEY;
const refreshTokenKey = process.env.REFRESH_TOKEN_KEY;
const verificationTokenKey = process.env.VERIFICATION_TOKEN_KEY;

exports.createAccessToken = (load) => {
    const { uid, email, institution_id } = load;
    const payload = { uid, email, institution_id };
    return sign(
        payload, 
        accessTokenKey, 
        {expiresIn: '15m'}
    );  
};

exports.createRefreshToken = (load) => {
    const { uid, email, institution_id } = load;
    const payload = { uid, email, institution_id };
    return sign(
        payload, 
        refreshTokenKey, 
        {expiresIn: '7d'}
    );
}

exports.verifyAccessToken = (token) => {
    const credential = verify(token, accessTokenKey);
    return {
        uid: credential.uid,
        email: credential.email,
        institution_id: credential.institution_id
    }
}

exports.verifyRefreshToken = (token) => {
    const credential = verify(token, refreshTokenKey);
    return {
        uid: credential.uid,
        email: credential.email,
        institution_id: credential.institution_id
    }
}

exports.verifyVerificationToken = (token) => {
    const credential = verify(token, verificationTokenKey);
    return {
        givenName: credential.givenName,
        middleName: credential.middleName,
        familyName: credential.familyName,
        suffix: credential.suffix,
        email: credential.email,
        institution_id: credential.institution_id
    }
}

exports.verifyVerificationCodeRequest = (token) => {
    const credential = verify(token, verificationTokenKey);
    return {
        email: credential.email,
        code: credential.code,
    }
}