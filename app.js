// Imports
const express = require('express')
const app = express()
const port = 5000

app.use(express.static(__dirname + '/pages'));

app.get('/', (req, res) => {
    res.sendFile(__dirname + '/pages' +'/views/home.html')
})

// Listen on Port 5000
app.listen(port, () => console.info(`App listening on port ${port}`))