
//import apiRouter from './api/index';
//import callsRouter from './api/calls';
const apiRouter = require('./api/index');
const callsRouter = require('./api/calls');


// [START gae_node_request_example]
const express = require('express');
const cors = require("cors");
const app = express();
app.use(cors());

app.get('/', (req, res) => {
  res.status(200).send('Hello, world!').end();
});
app.get('/myhome', (req, res) => {
  res.send('Hello Home Express');
});
app.use('/api', apiRouter);
app.use(express.static('public'));
app.use('/nytimes', callsRouter);
app.use(express.static('public'));
/*const googleTrends = require('google-trends-api');
 
//googleTrends.apiMethod(optionsObject, [callback])
googleTrends.interestOverTime({keyword: 'Women\'s march'})
.then(function(results){
  console.log('These results are awesome', results);
})
.catch(function(err){
  console.error('Oh no there was an error', err);
});*/
// Start the server
const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`App listening on port ${PORT}`);
  console.log('Press Ctrl+C to quit.');
});
// [END gae_node_request_example]

module.exports = app;
