app.get('/', function(req, res){
    res.render('pages/home')
});


const port = process.env.PORT || 4000;


app.listen(port); 