const database = require('../../../database/db');
const sqlConfig = require('../../../configs/sql.config');

const db = database.init(sqlConfig);

const table = 'user_cred';

exports.createUser = (data) =>{
    const { uid, institution_id, email, password, creationDate} = data;
    
    return new Promise((resolve, reject) => {
        db.query(`INSERT INTO ${table} 
            (uid, institution_id, email, password, creationDate) 
            VALUES (?, ?, ?, ?, ?)`, [uid, institution_id, email, password, creationDate], 
            (err, result) => {
                if(err) return reject(err);

                const responseData = {
                    uid,
                    institution_id,
                    email,
                    creationDate
                }
                result.data = responseData;
                resolve(result);
            });
    });
}
exports.getUserPassword = (institution_id) => {
    return new Promise((resolve, reject) => {
        db.query(`SELECT password FROM ${table} WHERE institution_id = ?`,
            [institution_id],
            (err, result) => {
                if(err) return reject(err);
                resolve(result);
            });
    });
    
}
exports.findUser = (data = {uid: '', email: '', institution_id: ''}) => {
    const { uid, email, institution_id } = data;
    return new Promise((resolve, reject) => {
        db.query(`SELECT * FROM ${table} 
        WHERE uid = ? OR email = ? OR institution_id = ?`, 
        [uid, email, institution_id], 
                (err, result) => {
                    if(err) return reject(err);
                    resolve(result);    
                });
    });
}
exports.getAll = () => {
    return new Promise((resolve, reject) => {
        db.query(`SELECT * FROM ${table}`, 
            (err, result) => {
                if(err) return reject(err);
                resolve(result);    
            });
    });
}