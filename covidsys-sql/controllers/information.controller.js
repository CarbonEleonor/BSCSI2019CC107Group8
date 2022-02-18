const User = require('../models/User.model');
const Exception = require('../exceptions/Exception');

exports.addInfo = async (req, res, next) => {
    try {
        const { id } = req.params;
        if(!id)
            next(new Exception('User id is required.', 400));
    
        const {
            institution_id,
            email,
            givenName,
            middleName,
            familyName,
            suffix,
            gender,
            birthdate,
            phoneNumber,
            addressLine1,
            addressLine2,
            city
        } = req.body;

        const Information = new User.information({
            uid: id,
            institution_id,
            email,
            givenName,
            middleName,
            familyName,
            suffix,
            gender,
            birthdate,
            phoneNumber,
            addressLine1,
            addressLine2,
            city
        });
        await Information.add()
        .then(result=>{
            if(result.affectedRows < 1)
                next(new Exception('Failed to create user information.', 500));

            return res.status(201).json({
                success: true,
                message: 'User information created successfully.'
            });
        })

    } catch (error) {
        next(error);
    }
};

exports.updateInfo = async (req, res, next) => {
    try {
        const { id } = req.params;
        if(!id)
            return next(new Exception('User id is required.', 400));

        const {
            institution_id,
            email,
            givenName,
            middleName,
            familyName,
            suffix,
            gender,
            birthdate,
            phoneNumber,
            addressLine1,
            addressLine2,
            city
        } = req.body;

        const Information = new User.information({
            uid: id,
            institution_id,
            email,
            givenName,
            middleName,
            familyName,
            suffix,
            gender,
            birthdate,
            phoneNumber,
            addressLine1,
            addressLine2,
            city
        });
        await Information.update()
        .then(result=>{
            if(result.affectedRows < 1)
                next(new Exception('Failed to create user information.', 500));

            return res.status(201).json({
                success: true,
                message: 'User information updated successfully.'
            });
        })

    } catch (error) {
        return next(error);
    }
};

exports.getUserInfo = async (req, res, next) => {
    try {
        const { id } = req.params;
        if(!id)
        next(new Exception('User id is required.', 400));

        const Information = new User.information();
        await Information.getOne({uid: id})
        .then(result=>{
            if(result.length < 1)
                next(new Exception('Failed to create user information.', 500));

            return res.status(200).json({
                success: true,
                result
            });
        });
    } catch (error) {
        return next(error)
    }
}