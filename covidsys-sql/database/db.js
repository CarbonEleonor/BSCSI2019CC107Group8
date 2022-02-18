const mysql = require('mysql2');
module.exports.init = (config) => {
    return mysql.createPool(config);
}