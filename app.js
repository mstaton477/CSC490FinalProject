// Imports
const express = require('express');
const mysql = require('mysql');
const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.static(__dirname + '/pages'));

app.get('/', (req, res) => {
    res.sendFile(__dirname + '/pages' +'/views/home.html')
})

const db = mysql.createConnection({
    host: "us-cdbr-east-06.cleardb.net", 
    user: "bab87ea7d060c5", 
    password: "c593381b", 
    database: "heroku_209a0a2d6441663"
});

db.connect((err) => {
    if(err) {throw err;}
    console.log("DB connection OK")
});

db.query("SELECT * from `heroku_209a0a2d6441663.user`", (err, results) => {
    if (err) { throw err;}
    console.log(results); 
})

// Listen on Port 3000
app.listen(PORT, () => console.info(`App listening on port ${PORT}`))
