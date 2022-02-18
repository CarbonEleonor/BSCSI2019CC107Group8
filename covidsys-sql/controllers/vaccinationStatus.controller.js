const User = require('../models/User.model');
const Exception = require('../exceptions/Exception');

exports.addVaccination = async (req, res, next) => {
    try {
        const { id } = req.params;
        const {
            status
        } = req .body;

        const Vaccination = new User.vaccination({
            uid: id,
            status
        });
        await Vaccination.add()
        .then(result=>{
            if(result.affectedRows < 1)
                next(new Exception('Failed to create status.', 500));

            return res.status(201).json({
                success: true,
                message: 'Status saved.'
            });
        })

    } catch (error) {
        return next(error);
    }
};

exports.updateVaccination = async (req, res, next) => {
    try {
        const { id } = req.params;
        if(!id)
            return next(new Exception('User id is required.', 400));

        const {
            status
        } = req .body;

        const Vaccination = new User.vaccination({
            uid: id,
            status
        });
        await Vaccination.update()
        .then(result=>{
            if(result.affectedRows < 1)
                next(new Exception('Failed to update status.', 500));

            return res.status(200).json({
                success: true,
                message: 'Report saved.'
            });
        })

    } catch (error) {
        return next(error);
    }
};

exports.getOneVaccination = async (req, res, next) => {
    try {
        const { id } = req.params;
        if(!id)
        next(new Exception('User id is required.', 400));

        const Vaccination = new User.vaccination();
        await Vaccination.getOne({uid: id})
        .then(result=>{
            if(result.length < 1)
                next(new Exception('Failed to get user vaccination status.', 500));

            return res.status(200).json({
                success: true,
                result
            });
        });
    } catch (error) {
        return next(error)
    }
}
