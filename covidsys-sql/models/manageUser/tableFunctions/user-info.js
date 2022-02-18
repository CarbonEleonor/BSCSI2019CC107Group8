const database = require('../../../database/db');
const sqlConfig = require('../../../configs/sql.config');

const db = database.init(sqlConfig);

const table = 'user_info';

exports.add = (data) => {
    const {
        uid, institution_id, email, givenName, middleName, 
        familyName, suffix, gender, birthdate, phoneNumber, 
        addressLine1, addressLine2, city
    } = data;
    return new Promise((resolve, reject) => {
        db.query(`INSERT INTO ${table} 
            (uid, institution_id, email, givenName, middleName, 
                familyName, suffix, gender, birthdate, phoneNumber, 
                addressLine1, addressLine2, city) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`, 
            [uid, institution_id, email, givenName, middleName,
                familyName, suffix, gender, birthdate, phoneNumber,
            addressLine1, addressLine2, city], 
            (err, result) => {
                if(err) return reject(err);
                resolve(result);
            });
    });
}

exports.update = (data) => {
    const {
        uid, institution_id, email, givenName, middleName,
        familyName, suffix, gender, birthdate, phoneNumber,
        addressLine1, addressLine2, city, lastUpdated
    } = data;

    return new Promise((resolve, reject) => {
        db.query(`UPDATE ${table} 
        SET 
        givenName = ?, 
        middleName = ?, 
        familyName = ?, 
        suffix = ?, 
        gender = ?, 
        birthdate = ?, 
        phoneNumber = ?, 
        addressLine1 = ?, 
        addressLine2 = ?, 
        city = ?,
        infoLastUpdated = ?
        WHERE uid='${uid}' AND 
        institution_id='${institution_id}' 
        AND email='${email}'`, 
        [givenName, middleName,familyName, suffix, 
            gender, birthdate, phoneNumber,
        addressLine1, addressLine2, city, lastUpdated], 
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