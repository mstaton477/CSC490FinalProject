// Imports
const express = require('express');
const session = require('express-session');
const passport = require('passport'); 
const LocalStrategy = require('passport-local').Strategy; 
const mysql = require('mysql');
const bcrypt = require('bcrypt');
const https = require('https'); 
const concat = require('concat-stream'); 

/**
* @jest-environment jsdom
*/

var bodyParser = require('body-parser');
const { writerState } = require('xmlbuilder');
const app = express();


app.use(session({
    secret: 'secret',
    saveUninitialized:true,
    resave: true 
}));

app.use(express.json());



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

//getting homepage
app.use(express.static(__dirname + '/pages'));

app.use(session({
    key: "cats",
    secret: "cats", 
    resave: false, 
    saveUninitialized: false
}));

app.use(passport.initialize());
app.use(passport.session()); 
app.use(express.json());
app.use(express.urlencoded({extended:true}));

app.get('/', (req, res) => {
    res.sendFile(__dirname + '/pages' +'/views/home.html')
})


app.post('/views/signin', async(req, res) => {
    inputData = {
        Username: req.body.Username,
        Name: req.body.Name, 
        Email: req.body.Email,
        Password: await bcrypt.hash(req.body.Password, 10)
    }

    var sql = 'SELECT * FROM user WHERE Username =?';
    await db.query(sql, [inputData.Username], async(err, data) =>{ 
        if(err) throw err;
        if(data.length != 0){
            console.log(inputData.Username + " already exists"); 
            res.send(`${Username} already exists`);
        }else {
            var sql = 'INSERT INTO user SET ?';
            await db.query(sql, inputData, (err, data) => {
                if (err) throw (err)
                console.log("You have sucessfully Registered your account"); 
                console.log(data.insertId); 
                res.redirect('./login.html');
            })

        }
    })
});


app.post('/login', async(req, res) => {
    const Username = req.body.username; 
    const Password = req.body.password;  

    const sqlSearch = "SELECT * FROM user where Username = ?;"; 
    const search_query = mysql.format(sqlSearch, [Username]);


    await db.query(search_query, async(err, result) => {
        if (err) throw err; 
        if(result.length == 0){
            console.log("User doesn't exit");
            res.sendStatus(404); 
        }else{
            const hashedPassword = result[0].Password;
            req.session.loggedinUser= true;
            req.session.Username= Username; 
            if(await bcrypt.compare(Password, hashedPassword)){
                console.log('Login Successful');
                // res.send(`${Username} is logged in `);   
            }else{
                console.log("Password Incorrect");
                res.send("Password incorrect"); 
            }
            res.redirect('./dashboard');
    
        }
    })
})

app.get('/dashboard', function(req, res, next) {
    if(req.session.loggedinUser){
        res.send({Username:req.session.Username}); 
    }else{
        res.redirect('./login');
    }
});

app.get('/logout', function(req, res){
    req.session.destroy(); 
    res.redirect('/login');
});

apiBaseUrl = 'http://openlibrary.org/search.json?'; 

app.post('/search', function(req, res){
    searchtxt = req.body.Answer; 
    console.log(req.body.Answer); 
    if(req.body.authorsearch){
        url = apiBaseUrl + "author=" + searchtxt;
        console.log(url);  
        https.get(url, (resp) => {
            let data = ''; 
            resp.on("data", (chunk) => {
                data += chunk; 
            }); 
            resp.on("end", () => {
                data = Buffer.concat(data).toString(); 
                console.log(data);
            }); 
        })

        
    }else if (req.body.titlesearch){
        url = apiBaseUrl + "title=" + searchtxt;
        console.log(url); 
        https.get(url, (resp) => {
            let data =''; 
            resp.on("data", (chunk) => {
                data =+ chunk; 
            }); 
            resp.on("end", () => {
                data = Buffer.concat(data).toString(); 
                console.log(data); 
            });
        })
        
    }else if(req.body.isbnsearch){
        isbnUrl = 'https://openlibrary.org/isbn/';
        url = isbnUrl + searchtxt + '.json';
        console.log(url);  
        https.get(url, (resp) => {
            let data = ''; 
            resp.on("data", (chunk) => {
                data =+ chunk; 
            }); 
            resp.on("end", () => {
                data = Buffer.concat(data).toString(); 
                console.log(data);
            });
        })
        
    }else if (req.body.subjectsearch){
        subjectUrl = "http://openlibrary.org/subjects/"; 
        url = subjectUrl+ searchtxt + '.json'; 
        console.log(url); 
        https.get(url, (resp) => {
            let data = ''; 
            resp.on("data", (chunk) => {
                data =+ chunk; 
            }); 
            resp.on("end", () => {
                data = Buffer.concat(data).toString(); 
                console.log(data);
            });
        })
        
    }
})




app.listen(process.env.PORT || 8080, function () {
    console.log('Node app is running on port 8080');
});

