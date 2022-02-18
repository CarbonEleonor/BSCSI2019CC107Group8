const Exception = require('../../exceptions/Exception');
const UserInfo = require('../manageUser/tableFunctions/user-profile');
class Profile{
    get(){
        return UserInfo.get();
    }
    getOne(data = { uid: '', institution_id: ''}){
        return UserInfo.getOne(data);
    }
}

module.exports = Profile;