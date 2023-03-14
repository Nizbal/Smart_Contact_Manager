console.log("this is script file")

const search=()=>{
    // console.log("searching...");
    let query=$("#search-input").val();
    console.log(query);

    if(query=='')
    {
        $(".search-result").hide();
    }
    else
    {
        // sending request to server

        let url=`http://localhost:8080/search/${query}`;
        fetch(url)
        .then((response) => {
            return response.json();
        })
        .then((data) => {
                console.log(data);
                let text=`<div class='list-group'>`;
                data.forEach((contact) => {
                    text += `<a href='/user/contact/${contact.cId}' class='list-group-item list-group-item-action'> ${contact.name} </a>`;
                });
                text += `</div>`;

                $(".search-result").html(text);
                $(".search-result").show();
            });

        $(".search-result").show();
    }
}