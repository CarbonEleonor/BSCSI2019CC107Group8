const sqlConfig = require('../../configs/sql.config');
const database = require('../../database/db');
const db = database.init(sqlConfig);


const Exception = require("../../exceptions/Exception");


class RefreshToken {
    add(data = { uid, refreshToken }){
        const { uid, refreshToken } = data;
        return new Promise((resolve, reject) => {
            db.query(`INSERT INTO refresh_tokens (uid, refresh_token) VALUES (?, ?)`, 
            [uid, refreshToken], (err, result) => {
                if(err) return reject(err);
                resolve(result);
            });
        });
    }
    get(data = { uid: '', refreshToken: '' }){
        const { uid, refreshToken } = data;
        return new Promise((resolve, reject) => {
            db.query(`SELECT * FROM refresh_tokens WHERE uid = ? OR refresh_token = ?`, [uid, refreshToken], (err, result) => {
                if(err) return reject(err);
                resolve(result);
            });
        });
    }
    delete(data = { uid: '', refreshToken: '' }){
        const { uid, refreshToken } = data;
        return new Promise((resolve, reject) => {
            db.query(`DELETE FROM refresh_tokens WHERE uid = ? AND refresh_token = ?`, 
            [uid, refreshToken], (err, result) => {
                if(err) return reject(err);
                resolve(result);
            });
        });
    }
}

module.exports = RefreshToken;