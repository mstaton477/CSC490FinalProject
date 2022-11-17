
const getRequest = require("./getRequest.js")

async function authorHelper(_type_, _value_, _limit_){
    const api_url_base = 'https://www.openlibrary.org';
    var request_url;
    let escaped_value = encodeURIComponent(_value_).replaceAll('%20','+');
    limit_exists = _limit_ !== null  &&  _limit_ > 1;
    if (limit_exists)   escaped_limit = encodeURIComponent(_limit_).replaceAll('%20','+')

    switch(_type_){
        case 'key': request_url = api_url_base + escaped_value.replaceAll('%2F','/') + '.json'; break;
        case 'name': request_url = api_url_base + '/search.json?author=' + escaped_value + (limit_exists ? '&limit=' + escaped_limit : ''); break;
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

    author_regex = new RegExp("A$");
    temp1 = [];

    switch(_type_){
        case 'key':
            return [_value_, data.name];
        break;

        case 'name':
            data.docs.forEach( (e) => { 
                if ( typeof e.seed !== 'undefined' ) { 
                    e.seed.forEach( (potential_key) => { 
                        if( author_regex.test(potential_key) ) temp1.push(potential_key) 
                    } ) 
                } 
            } )
                temp1 = [...new Set(temp1)];
                temp2 = [];

                for(let i = 0; i < temp1.length; i++){
                    await authorHelper('key', temp1[i]).then(key_name_pair => temp2.push(key_name_pair));
                }
            return temp2;
        break;

        default:
            console.log('invalid _type: ' + _type)
            return []
    }

    return [...new Set(temp)];
}

async function getAuthor(type_, value_, limit_, timeout_){
    if (limit_ === null) limit_ = '';
    if (timeout_ === null) timeout_ = 10000;
    data = await getRequest(authorHelper,  timeout_, type_, value_, limit_);
    if (typeof data.authors === 'undefined') return { "authors": data};
    else return data;
}

getAuthor('name', 'J.R.R. Tolkien').then((results) => { console.log(results) })