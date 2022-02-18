const Exception = require('../../exceptions/Exception');
const UserInfo = require('../manageUser/tableFunctions/user-info');
class Information{
    constructor(data = {
        uid: '', 
        institution_id: '',
        email:'', 
        givenName:'',
        middleName: '',
        familyName: '',
        suffix: '',
        gender:'', 
        birthdate:'', 
        phoneNumber:'', 
        addressLine1:'',
        addressLine2: '', 
        city: ''
    }){
        this.data = data;
    }
    add(){
        return UserInfo.add(this.data);
    }
    update(){      
        this.data.lastUpdated = new Date();
        return UserInfo.update(this.data);
    }
    get(){
        return UserInfo.get();
    }
    getOne(data = { uid: '', institution_id: ''}){
        return UserInfo.getOne(data);
    }

    data(){
        return this.data;
    }
}

module.exports = Information;