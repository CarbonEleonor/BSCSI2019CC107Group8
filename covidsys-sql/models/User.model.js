const credential = require('./user/Credential.model');
const information = require('./user/Information.model');
const health = require('./user/HealthStatus.model');
const vaccination = require('./user/VaccinationStatus.model');
const profile = require('./user/Profile.model');
module.exports = {
    credential,
    information,
    health,
    vaccination,
    profile
};