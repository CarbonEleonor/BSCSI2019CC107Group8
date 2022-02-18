const database = require('../../../database/db');
const sqlConfig = require('../../../configs/sql.config');

const db = database.init(sqlConfig);

const table = 'health_statistics';

exports.get = () => {
    return new Promise((resolve, reject) => {
        db.query(`SELECT * FROM ${table}`, (err, result) => {
            if(err) return reject(err);
            resolve(result);
        });
    });
}