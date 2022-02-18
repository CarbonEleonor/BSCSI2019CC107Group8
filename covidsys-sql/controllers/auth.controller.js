const Exception = require('../exceptions/Exception');
const User = require('../models/User.model');
const RefreshToken = require('../models/user/RefreshToken');
const { createAccessToken, createRefreshToken, verifyRefreshToken } = require('../store/token');

exports.signup = async (req, res, next) => {
    try {
        const { institution_id, email, password, 
            givenName, middleName,familyName, suffix } = req.body;
        
        const credential = new User.credential({
            institution_id,
            email,
            password
        });

        const result = await credential
                    .create()
                    .then(result => result)
                    .catch(err => {
                        if(err.errno = 1062)
                            return next("Email already exists.", 400);
                    });
        
        if(!result || result.affectedRows < 1)
            return next(new Exception('Failed to create user.', 400));
                    
        const { data } = result;
        await new User.information({
            uid: data.uid,
            institution_id,
            email,
            givenName,
            middleName,
            familyName,
            suffix
        })
        .add()
        .catch(err => {
            return next(err);
        });

        const payload = {   
            uid: data.uid,
            email: data.email,
            institution_id: data.institution_id
        };

        const accessToken = createAccessToken(payload);
        const refreshToken = createRefreshToken(payload);

        /**Add refresh token to database */
        const refreshResult = await new RefreshToken().add({refreshToken, uid: payload.uid});
        if(!refreshResult || refreshResult < 1)
            return next(new Exception('Failed to create session.', 500));

        return res.status(201).json({
            success: true,
            message: 'User created successfully.',
            accessToken,
            refreshToken,
            uid: data.uid
        });
    } catch (error) {
        if(error.errno === 1062)
            return next(new Exception("User already exists.", 400));
        next(error);
    }
}
exports.userLogin = async (req, res, next) => {
    try {
        const { institution_id, password } = req.body;
        const cred = new User.credential();


        /** validate password */
        const comparison = await cred.comparePassword({ institution_id, password });
        if(!comparison || !comparison.authResult)
            return next(new Exception('Invalid password.', 400));
        
        const payload = {
            uid: comparison.uid,
            email: comparison.email,
            institution_id: comparison.institution_id
        }

        const accessToken = createAccessToken(payload);
        const refreshToken = createRefreshToken(payload);

        /**Add refresh token to database */
        const result = await new RefreshToken().add({refreshToken, uid: payload.uid});
        
        if(!result || result.affectedRows < 1)
            return next(new Exception('Failed to create session.', 500));

        return res.status(201).json({
            success: true,
            message: 'User logged in successfully.',
            accessToken,
            refreshToken
        });
    } catch (error) {
        return next(error);
    }
}
exports.createSession = async (req, res, next) => {
    try {
        const { refreshToken } = req.body;
        
        const credential = refreshToken && verifyRefreshToken(refreshToken);
 
        if(!credential)
            return next(new Exception('Invalid token.', 400)); 
        // check if refresh token exists
        const doExists = await new RefreshToken()
            .get({refreshToken})
            .then(result => result && result.length > 0);

        if(!doExists)
            return next(new Exception('Invalid token.', 400));
         

        // if refresh token exists, create new access token
        const { uid, email, institution_id } = credential;
        
        const payload = {
            uid,
            email,
            institution_id
        }

        const accessToken = createAccessToken(payload);
        return res.json({
            success: true,
            accessToken
        });
    } catch (error) {
        return next(error);
    }
}

exports.sessionLogout = async (req, res, next) => {
    try {
        const { refreshToken } = req.body;

        const credential = refreshToken && verifyRefreshToken(refreshToken);
        if(!credential)
            return next(new Exception('Invalid token.', 400));
        // check if refresh token exists
        const doExists = await new RefreshToken()
            .get({refreshToken})
            .then(result => result && result.length > 0);

        if(!doExists)
            return next(new Exception('Invalid token.', 400));
        
        // if refresh token exists, delete refresh token
        const result = await new RefreshToken()
            .delete({refreshToken, uid: credential.uid});

        if(!result || result.affectedRows < 1)
            return next(new Exception('Failed to delete session.', 500));

        return res.json({
            success: true,
            message: 'User logged out successfully.'
        });
    }
    catch (error) {
        return next(error);
    }
}

exports.get = async (req, res, next) => {
    try {
        const user = new User.credential();
        await user.getAll()
                .then(result => {
                    res.json({
                        success: true,
                        data: result
                    })
                })
                .catch(err => {
                    return next(err);
                });
    } catch (error) {
        return next(error);
    }
} 