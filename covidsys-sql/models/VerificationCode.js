const { generateCode } = require('../helpers/genid');
const config = require('../configs/sql.config');
const db = require('../database/db');
const database = db.init(config);

class VerificationCode{
    #code = '';
    generate(){
        let code = generateCode();
        this.#code = code;
        return this;
    }
    save({email = ''}){
        return new Promise((resolve, reject) => {
            database.query(`
                INSERT INTO verification_codes (email, v_code, expiresAt)
                VALUES ('${email}', '${this.#code}', current_timestamp() + 500)
                `, (err, result) => {   
                if (err) return reject(err);
                console.log(err);
                result.code = this.#code;
                resolve(result);
            });
        });
    }
    findOne({email = '', code = ''}){
        return new Promise((resolve, reject) => {
            database.query(`
                SELECT * FROM verification_codes
                WHERE email = '${email}'
                AND v_code = '${code}'
                `, (err, result) => {                
                if (err) reject(err);
                else resolve(result[0]);
            });
        });
    }
    remove({email,code}){
        return new Promise((resolve, reject) => {
            database.query(`
                DELETE FROM verification_codes
                WHERE email = '${email}'
                AND v_code = '${code}'
                `, (err, result) => {                
                if (err) reject(err);
                else resolve(result);
            });
        });
    }
}

module.exports = VerificationCode;