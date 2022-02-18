const { generateID } = require("../../helpers/genid");
const bcrypt = require("bcryptjs");
const Exception = require('../../exceptions/Exception');
const { createUser, findUser, getAll, getUserPassword } = require("../manageUser/tableFunctions/user-cred");

class Credential {
    constructor(data = { institution_id:'', email:'', password:''}){
        data.creationDate = new Date();
        this.data = data;
    }
    async create(){
        this.data.uid = generateID();
        const hashedPass = await hashPassword(this.data.password);
        this.data.password = hashedPass;

        return createUser(this.data); 
    }   
    find(data = {uid: '', email: '', institution_id: ''}){
        return findUser(data);
    }
    getAll(){
        return getAll();
    }
    comparePassword(data = {password: '', institution_id: ''}){
        const { institution_id, password } = data;
        return new Promise(async (resolve, reject) => {
            await findUser({institution_id})
                .then(result => {
                    if(result.length < 1)
                        reject(new Exception('Invalid password!', 400));
                    const { uid, email, password:hashed } = result[0];
                    
                    return ({
                        uid,
                        email,
                        passwordHashed: hashed
                    })
                })
                .then(async user => {
                    const { uid, email, passwordHashed } = user;
                    const result = await comparePassword(data.password, passwordHashed)
                                    .then(res => res);
                    const returnable = {
                        uid,
                        email,
                        institution_id,
                        authResult: result
                    };
                    resolve(returnable);
                })
                .catch(err => {
                    reject(err);
                });
        });
    }   
    data(){
        return this.data;
    }
}

function hashPassword(password){
    return new Promise((resolve, reject) => {
        bcrypt.hash(password, 10, (err, hash) => {
            if(err) return reject(err);
            resolve(hash);
        });
    });
}
function comparePassword(password, hashedPassword){
    return new Promise((resolve, reject) => {
        bcrypt.compare(password, hashedPassword, (err, result) => {
            if(err) return reject(err);
            resolve(result);
        });
    });
}

module.exports = Credential;