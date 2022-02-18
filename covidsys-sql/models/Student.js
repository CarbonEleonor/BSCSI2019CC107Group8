const config = require('../configs/sql.config');
const db = require('../database/db');
const database = db.init(config);

class Student {
    findOne({
        student_id = '',
        givenName = '',
        middleName = '',
        familyName = '',
        suffix = ''
    }){
        return new Promise((resolve, reject) => {
            database.query(`
                SELECT * FROM students
                WHERE student_id = '${student_id}'
                AND givenName = '${givenName}'
                AND middleName = '${middleName}'
                AND familyName = '${familyName}'
                AND suffix = '${suffix}'
                `, (err, result) => {
                if (err) reject(err);
                else resolve(result[0]);
            });
        });   
    }
}

module.exports = Student;