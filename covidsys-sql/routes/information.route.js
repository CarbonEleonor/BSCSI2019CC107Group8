const router = require('express').Router();
const { addInfo, updateInfo, getUserInfo} = require('../controllers/information.controller');
const auth = require('../middlewares/auth');
// add info
router.post('/:id', auth, addInfo);

// update info
router.put('/:id', auth, updateInfo);

router.get('/:id',getUserInfo);

module.exports = router;