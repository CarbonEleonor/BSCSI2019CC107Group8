const { getOneVaccination, addVaccination, updateVaccination } = require('../controllers/vaccinationStatus.controller');
const auth = require('../middlewares/auth');
const router = require('express').Router();

router.post('/:id', auth, addVaccination);
router.put('/:id', auth, updateVaccination);
router.get('/:id', getOneVaccination);

module.exports = router