const router = require('express').Router();
const { addHealth, updateHealth, getUserHealth } = require('../controllers/healtInformation.controller');
const auth = require('../middlewares/auth');

router.post('/:id', auth, addHealth);
router.put('/:id', auth, updateHealth);
router.get('/:id', getUserHealth);

module.exports = router;