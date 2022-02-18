const router = require('express').Router();
const { verify, validateCode } = require('../controllers/verify.controller');

router.post('/', verify);
router.post('/validateCode', validateCode);
module.exports = router;