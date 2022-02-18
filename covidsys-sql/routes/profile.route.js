const router = require('express').Router();
const { getUserProfile } = require('../controllers/profile.controller');
const auth = require('../middlewares/auth');

router.get('/:id', auth, getUserProfile);

module.exports = router;