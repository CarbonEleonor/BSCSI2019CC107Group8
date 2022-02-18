const router = require('express').Router();
const { getVax, getHealth } = require('../controllers/stats.controller');
const auth = require('../middlewares/auth');

router.get('/vaccine', getVax);
router.get('/health', getHealth);

module.exports = router;