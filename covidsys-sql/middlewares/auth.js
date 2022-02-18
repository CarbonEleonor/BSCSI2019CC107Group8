const Exception = require("../exceptions/Exception");
const { verifyAccessToken } = require("../store/token");


const auth = (req, res, next) => {
    try {
        console.log(req.headers);
        const { authorization } = req.headers;
        if (!authorization)
            return next(new Exception('No authorization header.', 403));

        const payload = verifyAccessToken(authorization);
        if (!payload)
            return next(new Exception('Invalid token.', 403));

        next();
    } catch (error) {
        next(error);
    }
}

module.exports = auth;