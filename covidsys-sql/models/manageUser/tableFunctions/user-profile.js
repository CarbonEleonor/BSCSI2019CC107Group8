const database = require('../../../database/db');
const sqlConfig = require('../../../configs/sql.config');

const db = database.init(sqlConfig);

const table = 'user_profile';

exports.get = () => {
    return new Promise((resolve, reject) => {
        db.query(`SELECT * FROM ${table}`, (err, result) => {
            if(err) return reject(err);
            resolve(result);
        });
    });
}

exports.getOne = (data) => {
    const { uid, institution_id } = data;
    return new Promise((resolve, reject) => {
        db.query(`SELECT * FROM ${table} 
        WHERE uid='${uid}' OR institution_id='${institution_id}'`, 
        (err, result) => {
            if(err) return reject(err);
            resolve(result);
        });
    });
}