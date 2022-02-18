const database = require('./db');
const config = require('../configs/sql.config');
const nodeCron = require('node-cron');
const Exception = require('../exceptions/Exception');

const db = database.init(config);

// clear verification codes every 15s
const clearCodes = nodeCron.schedule('*/15 * * * * *', () => {
    try {
        db.query(`
            DELETE FROM verification_codes
            WHERE expiresAt < current_timestamp()`,
        (err, result) => {
            if(err) console.log(err);   
        });
    } catch (error) {
        throw new Exception("Error clearing verification codes", error);
    }
});

module.exports = {
    clearCodes
};
