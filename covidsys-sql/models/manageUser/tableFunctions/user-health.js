const database = require('../../../database/db');
const sqlConfig = require('../../../configs/sql.config');

const db = database.init(sqlConfig);

const table = 'user_health';

exports.add = (data) => {
    const {
        report,
        status,
        uid
    } = data;   
    return new Promise((resolve, reject) => {
        db.query(`INSERT INTO ${table} 
            (uid, healthStatus, report) 
            VALUES (?, ?, ?)`, 
            [uid, status, report], 
            (err, result) => {
                if(err) return reject(err);
                resolve(result);
        });
    });
}

exports.update = (data) => {
    const {
        report,
        status,
        uid, 
        lastUpdated
    } = data;
    return new Promise((resolve, reject) => {
        db.query(`UPDATE ${table} 
            SET
            healthStatus = ?, 
            report = ?, 
            healthLastUpdated = ?
            WHERE uid = '${uid}'`, 
            [status, report, lastUpdated], 
            (err, result) => {
                if(err) return reject(err);
                resolve(result);
            });
    });
}

exports.get = () => {
    return new Promise((resolve, reject) => {
        db.query(`SELECT * FROM ${table}`, (err, result) => {
            if(err) return reject(err);
            resolve(result);
        });
    });
}

exports.getOne = (data) => {
    const { uid } = data;
    return new Promise((resolve, reject) => {
        db.query(`SELECT * FROM ${table} 
        WHERE uid='${uid}'`, 
        (err, result) => {
            if(err) return reject(err);
            resolve(result);
        });
    });
}