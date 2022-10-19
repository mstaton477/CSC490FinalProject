// Imports
const express = require('express')
const app = express()
const port = 5000

// Listen on Port 5000
app.listen(port, () => console.info(`App listening on port ${port}`))


app.use(express.static('pages'));

app.set('home', ',/home');
