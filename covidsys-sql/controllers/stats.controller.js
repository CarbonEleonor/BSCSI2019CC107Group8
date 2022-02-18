const Statistics = require('../models/user/Statistics.model');
const Exception = require('../exceptions/Exception');

exports.getHealth = async (req, res, next) => {
    try {
        await new Statistics()
        .getHealth()
        .then(result=>{
            if(result.length < 1)
                return next(new Exception('Failed to fetch user information.', 500));

            return res.status(200).json({
                success: true,
                result
            });
        });
    } catch (error) {
        return next(error)
    }
}
exports.getVax = async (req, res, next) => {
    try {
        await new Statistics()
        .getVax()
        .then(result=>{
            if(result.length < 1)
                return next(new Exception('Failed to fetch user information.', 500));

            return res.status(200).json({
                success: true,
                result
            });
        });
    } catch (error) {
       return next(error)
    }
}