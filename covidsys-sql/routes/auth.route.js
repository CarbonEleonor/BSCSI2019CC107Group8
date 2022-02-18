const router = require('express').Router();
const {signup, createSession, get, userLogin, sessionLogout} = require('../controllers/auth.controller'); 
router.post('/signup', signup);
router.post('/userLogin', userLogin);
router.post('/createSession', createSession);
router.post('/sessionLogout', sessionLogout);
router.get('/', get);
module.exports = router;