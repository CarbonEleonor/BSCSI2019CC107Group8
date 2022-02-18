const User = require('../models/User.model');
const Exception = require('../exceptions/Exception');

exports.getUserProfile = async (req, res, next) => {
    try {
        const { id } = req.params;
        if(!id)
            return next(new Exception('User id is required.', 400));

        const Profile = new User.profile();
        await Profile.getOne({uid: id})
        .then(result=>{
            if(result.length < 1)
                return next(new Exception('Failed to fetch user information.', 500));

            return res.status(200).json({
                success: true,
                result
            });
        });
    } catch (error) {
        next(error)
    }
}