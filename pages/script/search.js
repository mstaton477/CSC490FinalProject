$("input[type='radio']").change(function() {
    if($(this).checked()){
        $("#Answer").show(); 
    }else{
        $("#Answer").hide();
    }
})