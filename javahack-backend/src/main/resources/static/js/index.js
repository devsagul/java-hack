let resp;
let index = 1;
function elem(id) {
    return document.getElementById(id)
}
const flexbox = document.getElementsByClassName('flexbox')[0];

async function fetching() {
    await fetch("http://localhost:80/")
        .then(response=>{
            return response.json()
        })
        .then(function (defs) {
            resp=defs;
            console.log(defs);
        })
        .catch(
            // Отправить на сервер для метрики
        );
    /*
        resp =
            {
            question:'Ты пишешь на Kotlin?',
            id:0,
            "childs":
                [
                    {"id":1,"answer":"да"},
                    {"id":2,"answer":"нет"}
                ]
        };
        resp =
        {
            question:'Ты пишешь на Kotlin?',
            id:0,
            input:true
        };

 */
        elem("question").innerHTML = `<h1> <span style="color: red;">${index}</span>. ${resp.question}</h1>`;
        if (resp.childs) {
            for (let x = 0; x < 10; x++) {
                publishPost(resp.childs[0]);
            }
            index++;
        }
        else{
            publishInput(resp);
        }
}
fetching();
function publishPost(post){
    const node = document.createElement("div");
    node.className = 'item ' + post.id;
    node.alt = '';
    const textNode = document.createElement('p');
    textNode.innerHTML = `<span style="color:red;">${post.id}</span>. ${post.answer}`;
    node.appendChild(textNode);
    flexbox.appendChild(node);
};
function publishInput(post){
    const node = document.createElement("input");
    node.className = 'item ' + post.id;
    node.alt = '';
    flexbox.appendChild(node);
};