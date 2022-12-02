
// LEAVE UNCOMMENTED WHEN DONE
const fetch = require("node-fetch");

const getRequest = require("./getRequest.js");
const getAuthor = require("./getAuthor.js");

// DO NOT CALL THIS FUNCTION: call getBook() instead
async function bookHelper(_type, _value, _limit) {
    const api_url_base = 'https://www.openlibrary.org';
    var request_url;
    let escaped_value = encodeURIComponent(_value).replaceAll('%20','+');   // decodes the %20 to "+", as preferred by the api

    limit_exists = _limit !== null  &&  _limit > 1; // check if not-null and nonempty
    if (limit_exists)   escaped_limit = encodeURIComponent(_limit).replaceAll('%20','+')    // encode and decode

    switch(_type){  // build request_url
        case 'key': request_url = api_url_base + escaped_value.replaceAll('%2F','/') + '.json';                                             break;
        case 'title': request_url = api_url_base + '/search.json?title=' + escaped_value + (limit_exists ? '&limit=' + escaped_limit : ''); break;
        case 'isbn': request_url = api_url_base + '/isbn/' + escaped_value + '.json';                                                       break;
        default: return []  // if it doesn't match required form, we can't make the request
    }
   
    const response = await fetch(request_url);  // Make API call
    const data = await response.json(); // to JSON 

    // redirect if necessary
    if(typeof data.type !== 'undefined' && data.type.key === '/type/redirect')      return getBook('key', data.results.location);

    const author_map = new Map();

    switch(_type){  // filter and process results
        case 'key':
        case 'isbn':
            var temp1 = [];
            var temp3;
            promise_list = [];
            if(typeof data.authors === 'undefined') data.authors = [];

            for(let i = 0; i < data.authors.length; i++){   // process author keys (find name associated with each key)
                temp3 = typeof data.authors[i].key !== 'undefined' ? data.authors[i].key : data.authors[i].author.key;
                promise_list.push(  // amass promises from each author request
                    await getAuthor('key', temp3, -1)
                    .then(x => temp1.push(x))
                );
            }

            await Promise.allSettled(promise_list).then( () => {
                if(false){ // for debugging purposes; displays keys and names but breaks later code if not set to false
                    for(let i = 0; i < temp1.length; i++){
                        temp1[i] = {'key': temp1[i].authors.key, 'name': temp1[i].authors.name}
                    }
                    temp1.forEach(toPrint => console.log(toPrint))
                }

                temp2 = []
                temp1.forEach( (a) => { // process results and push onto temp2 to be returned
                    temp2.push({
                        'key': data.key,
                        'title': data.title,
                        'authors': a.authors
                    });
                })
            });
        return temp2;

        case 'title':

            var temp;
            var temp_list = [];
            const docs = data.docs;

            for(let i = 0; i < docs.length; i++){
                let key = docs[i].key;
                if(typeof key === 'undefined') continue;

                let temptitle = null;
                let author_keys = null;
                let author_names = null;

                try{
                    temptitle = docs[i].title
                    author_keys = docs[i].author_key
                    author_names = docs[i].author_name
                }catch(_error){
                console.error(_error);
                } finally{

                    temp_list.push({
                        'key': key,
                        'title': temptitle,
                        'author_keys': author_keys,
                        'author_names': author_names
                    })
                }
            }

            temp_list.forEach((e) => {
                if(typeof e.author_keys !== 'undefined' && typeof e.author_names !== 'undefined'){
                    for(let j = 0; j < e.author_keys.length && j < e.author_names.length; j++){
                        if(!author_map.has(e.author_keys[j])){
                            author_map.set(e.author_keys[j], e.author_names[j]);
                        }
                    }
                }
            })

            for(let k = 0; k < temp_list.length; k++){

                let temp_authors = []
                temp_list[k].author_keys.forEach((this_key) => {
                    if(author_map.has(this_key)){
                    temp_authors.push({'key':this_key, 'name':author_map.get(this_key)})
                }})

                temp_list[k] = {
                    'key': temp_list[k].key,
                    'title': temp_list[k].title,
                    'authors': temp_authors
                };
            }
        return temp_list;

      default: console.log('invalid _type: ' + _type); return [];
    }
}

/*
__type should be 'key', 'title', or 'isbn' corresponding to the type of __value; 'key' corresponds to the OpenLibrary key for the book
__value holds the value
__limit (optional) passed to the API; limits how many authors are returned; pass '' to specify no __limit if you wish to specify the __timeout
__timeout (optional) in milliseconds
An example call: getBook('title', 'the lord of the rings', 10)
*/
async function getBook(__type, __value, __limit, __timeout){
    if (__timeout === null) __timeout = 10000;

    data = await getRequest(bookHelper,  __timeout, __type, __value, __limit);
    return { "books": data};
}

module.exports = getBook; 
