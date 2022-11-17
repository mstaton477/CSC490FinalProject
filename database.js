const mysql = require('mysql');

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

module.exports = db; 