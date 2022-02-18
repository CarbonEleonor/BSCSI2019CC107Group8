const { add, get, getOne, update } = require('../manageUser/tableFunctions/user-vaccination');
class VaccinationStatus {
    constructor(data ={uid: '', status: ''}){
        this.data = data;
    }
    add(){
        return add(this.data);
    }
    update(){
        this.data.lastUpdated = new Date();
        return update(this.data);
    }
    get(){
        return get();
    }
    getOne(data = { uid: '' }){
        return getOne(data);
    }
}

module.exports = VaccinationStatus;