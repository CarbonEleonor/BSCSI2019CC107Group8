const User = require('../models/User.model');
const Exception = require('../exceptions/Exception');
const HealthProcessor = require('../helpers/HealthProcessor');

exports.addHealth = async (req, res, next) => {
    try {
        const { id } = req.params;
        if(!id)
            next(new Exception('User id is required.', 400));
        const {
            report
        } = req .body;

        const healthProcessor = new HealthProcessor(report).init();
        const status = healthProcessor.getStatus();

        const Health = new User.health({
            uid: id,
            status,
            report: JSON.stringify(report)
        });
        await Health.add()
        .then(result=>{
            if(result.affectedRows < 1)
                next(new Exception('Failed to create report.', 500));

            return res.status(201).json({
                success: true,
                message: 'Report saved.'
            });
        })

    } catch (error) {
        return next(error);
    }
};

exports.updateHealth = async (req, res, next) => {
    try {
        const { id } = req.params;
        if(!id) 
            return next(new Exception('User id is required.', 400));
        const {
            report
        } = req .body;

        const healthProcessor = new HealthProcessor(report).init();
        const status = healthProcessor.getStatus();

        const Health = new User.health({
            uid: id,
            status,
            report: JSON.stringify(report)
        });
        await Health.update()
        .then(result=>{
            if(result.affectedRows < 1)
                next(new Exception('Failed to create report.', 500));

            return res.status(200).json({
                success: true,
                message: 'Report saved.'
            });
        })

    } catch (error) {
        return next(error);
    }
};

exports.getUserHealth = async (req, res, next) => {
    try {
        const { id } = req.params;
        if(!id)
            return next(new Exception('User id is required.', 400));
        const healthProcessor = new HealthProcessor(report).init();
        const status = healthProcessor.getStatus();

        const Health = new User.health();
        await Health.getOne({uid: id})
        .then(result=>{
            if(result.length < 1)
                next(new Exception('Failed to get user health.', 500));

            return res.status(200).json({
                success: true,
                result
            });
        });
    } catch (error) {
        return next(error)
    }
}
