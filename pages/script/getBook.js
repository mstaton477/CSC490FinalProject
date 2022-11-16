
// LEAVE UNCOMMENTED WHEN DONE
const fetch = require("node-fetch");

// LEAVE COMMENTED WHEN DONE
// var base_path = './'

if(typeof base_path === 'undefined') base_path = './pages/script/';
const getRequest = require(base_path + "getRequest.js");
const getAuthor = require(base_path + "getAuthor.js");

// DO NOT CALL THIS FUNCTION: call getBook() instead
async function bookHelper(_type, _value, _limit) {
    const api_url_base = 'http://www.openlibrary.org';
    var request_url;
    let escaped_value = encodeURIComponent(_value).replaceAll('%20','+');
    limit_exists = _limit !== null  &&  _limit > 1;
    if (limit_exists)   escaped_limit = encodeURIComponent(_limit).replaceAll('%20','+')

    switch(_type){
        case 'key': request_url = api_url_base + escaped_value.replaceAll('%2F','/') + '.json'; break;
        case 'title': request_url = api_url_base + '/search.json?title=' + escaped_value + (limit_exists ? '&limit=' + escaped_limit : ''); break;
        case 'isbn': request_url = api_url_base + '/isbn/' + escaped_value + '.json'; break;
        default: return []
    }
   
    // Make API call
    const response = await fetch(request_url);
    console.log(request_url)
 
    // to JSON 
    const data = await response.json();

    // redirect if necessary
    if(typeof data.type !== 'undefined' && data.type.key === '/type/redirect'){
        return getBook('key', data.results.location)
    }
 
 // TODO finish this part
 switch(_type){

    case 'isbn':
    temp = [{
        'key': data.key,
        'title': data.title,
        'authors': data.authors
    }];
    console.log(temp);
    console.log(data.authors);
    return temp;


    case 'title':

    var temp;
    var temp_list = [];
    const docs = data.docs;

    for(let i = 0; i < docs.length; i++){
        let key = docs[i].key;
        if(typeof key === 'undefined') continue;

        var title = null;
        var author_keys = null;
        var author_names = null;

        try{
            title = docs[i].title
            author_keys = docs[i].author_key
            author_names = docs[i].author_name
        }catch(error){
          console.error(error);
        } finally{

            temp_list.push({
                'key': key,
                'title': title,
                'author_keys': author_keys,
                'author_names': author_names
            })
        }
    }
    return temp_list;

    default:
        console.log('invalid _type: ' + _type)
        return []
 }
}

/*
_type should be 'key', 'title', or 'isbn'
_value holds that specific value
_limit is optional
An example call: getBook('title', 'the lord of the rings', 10)
*/
async function getBook(__type, __value, __limit, __timeout){
    if (__timeout === null) __timeout = 10000;

    data = await getRequest(bookHelper,  __timeout, __type, __value, __limit);
    return { "books": data};
}

/* <- add/remove a slash before the block comment to toggle code
// example code of it in use; the call to then is crucial
//
let type = 'title', searchtxt = 'the hunger games', limit = 5;
function dummy_function(x) { console.log(x) }
//
getBook(type, searchtxt, limit)
    .then(results => dummy_function(results))
//
//                              toggle (up above) to comment out when done testing
//
//*/      
//          do not touch the line above; it ends block comment (ignored if not in a block comment) 

module.exports = getBook; 
