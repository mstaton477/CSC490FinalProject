// Imports
const express = require('express');
const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.static(__dirname + '/pages'));

app.get('/', (req, res) => {
    res.sendFile(__dirname + '/pages' +'/views/home.html')
})

// Listen on Port 3000
app.listen(PORT, () => console.info(`App listening on port ${PORT}`))
