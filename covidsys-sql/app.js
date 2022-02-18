require('dotenv').config({path: './configs/.env'});
const express = require('express');
const cors = require('cors');
const errorHandler = require('./middlewares/errorHandler');
const Schedule = require('./database/scheduler');

const PORT = process.env.PORT || 5000;

// init express
const app = express();
app.use(express.json()); // to handle api calls responses in json format
app.use(cors()); 

// api routes
app.use('/auth', require('./routes/auth.route'));
app.use('/verify', require('./routes/verify.route'));

app.use('/user', require('./routes/profile.route'));
app.use('/user/information', require('./routes/information.route'));
app.use('/user/health', require('./routes/health.route'));
app.use('/user/vaccination', require('./routes/vaccination.route'));
app.use('/statistics', require('./routes/stats.route'));


app.use(errorHandler); // should always be the last middleware
// no more middleware after this one

// init scheduler to delete expired verification codes

Schedule.clearCodes.start();

// init server and listen
const serverHandler = () => {
    console.log('Server is running on port: ', PORT);
}
const server = app.listen(PORT, serverHandler);

/**
 * For handling unhandled rejections, 
 * for additional security and debugging efficiency
 */
const rejectionHandler = (err) => {
    console.warn('Server timed out.');
	console.log(`ERROR LOG: ${err}`);

	/**Close the server if an error is unhandled. */
	server.close(_=>process.exit(1));		
}
process.on('unhandledRejection', rejectionHandler);

