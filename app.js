// Imports
const express = require('express');
const session = require('express-session'); 
const mysql = require('mysql');
const bcrypt = require('bcrypt');
const path = require('path'); 
const LocalStrategy = require('passport-local'); 
const passport = require('passport'); 


//javascript files import 

const getBook = require('./pages/script/getBook');  
const db = require('./database'); 
const { result } = require('lodash');


const store = new session.MemoryStore(); 
const app = express();

// app.set("view engine", "ejs"); 


app.engine("html", require("ejs").renderFile);
app.set("view engine", "html");
app.set('views', path.join(__dirname, "/views")); 

app.use(session({
    secret: 'secret',
    saveUninitialized:true,
    resave: true 
}));

app.use(express.json());

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

//getting signup page and inserting new users into the database, then redirect to login 
//bycrypt is what we are using the hash the password to make it more secure
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
            return res.send(`${Username} already exists`);
        }else {
            var sql = 'INSERT INTO user SET ?';
            await db.query(sql, inputData, (err, data) => {
                if (err) throw (err)
                console.log("You have sucessfully Registered your account"); 
                console.log(data.insertId); 
                return res.redirect('./login.html');
            })

        }
    })
});

//grabbing user details from the database to log people in 
// then will redirect to the user specific dashboard 
app.post('/login', async(req, res) => {
    const Username = req.body.username; 
    const Password = req.body.password; 
    
    

    const sqlSearch = "SELECT * FROM user where Username = ?;"; 
    const search_query = mysql.format(sqlSearch, [Username]);


    await db.query(search_query, async(err, result) => {
        if (err) throw err; 
        if(result.length == 0){
            console.log("User doesn't exsit");
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
                res.send("Password Incorrect "); 
            }
            res.redirect('./dashboard');
    
        }
    })
})
//user specific dashboard 
//will hold the users book lists, clubs, link to book search
app.get('/dashboard', async function(req, res) {
    if(req.session.loggedinUser){
        const Username = req.session.Username; 
        const booklistSQL = 'SELECT * FROM `book list` WHERE Username = ? '; 
        const bookList_query = mysql.format(booklistSQL, [Username]);

        await db.query(bookList_query, async function(err, results) {
            if (err) throw err;
            if (results.length == 0){
                console.log("No Booklists associated with this User"); 
            }
            console.log(results); 
            return results; 

        }, 
        res.render("../pages/views/dashboard.ejs",{Username:Username
            // , 
            // results:results
        }) 
        )

        
        



        // db.query('SELECT * FROM `book list` WHERE Username = ? ', [req.session.Username], function(error, results, fields){
        //     if (error) throw error; 
        //     res.render("../pages/views/dashboard.ejs", {data : results}); 
        // })
    }else{
        res.redirect('./login');
    }
    
    
});

// log out function 
app.get('/logout', function(req, res){
    req.session.destroy(); 
    res.redirect('/login');
});

 
//search page 
app.post('/search',  async function(req, res){
    searchtxt = req.body.Answer; 
    console.log(req.body.Answer); 
    if(req.body.titlesearch){

        await getBook('title', searchtxt, 10).then((results) => 
                   
                    res.render("../pages/views/search-results.ejs", 
                    // console.log(results.books), 
            {
                data: results.books
            }) )
      } 

   }
)

app.listen(process.env.PORT || 8080, function () {
    console.log('Node app is running on port 8080');
});

