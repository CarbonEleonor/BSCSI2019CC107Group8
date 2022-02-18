const Exception = require('../../exceptions/Exception');
const VaxStats = require('../manageUser/tableFunctions/vaccinated-statistics');
const HealthStats = require('../manageUser/tableFunctions/health-statistics');
class Statistics{
    getVax(){
        return VaxStats.get();
    }
    getHealth(){
        return HealthStats.get();
    }
}

module.exports = Statistics;