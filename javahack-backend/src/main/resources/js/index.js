let resp;
let index = 1;
function elem(id) {
    return document.getElementById(id)
}
const flexbox = document.getElementsByClassName('flexbox')[0];

async function fetching() {
    await fetch(`/helper${index}`)
        .then(response=>{
            return response.json()
        })
        .then(function (defs) {
            resp=defs;
            elem("question").innerHTML = `<h1> <span style="color: red;">${index}</span>. ${resp.question}</h1>`;
            if (resp.childs) {
                for (let x = 0; x < resp.childs.length; x++) {
                    publishPost(resp.childs[x]);
                }
                index++;
            }
            else{
                publishInput(resp);
            }
        })
        .catch(() => {
            elem("question").innerHTML = `<h1>ERROR, can't establish connection to server! </h1>`});
}
fetching();
function callServer(par) {
    let param;
    index++;
    if (par === "input") {
        param = elem("input").value ;
    }
    else {
        param=par
    }
    const xhr = new XMLHttpRequest();
    xhr.open("GET", '/submit?' +param , true);;
    xhr.send();
}
    function publishPost(post){
    const node = document.createElement("div");
    node.className = 'item ' + post.id;
    node.alt = '';
    const textNode = document.createElement('p');
    textNode.innerHTML = `<span style="color:red;">${post.id}</span>. ${post.answer}`;
    node.appendChild(textNode);
    node.onclick = callServer(post.id);
    flexbox.appendChild(node);
};
function publishInput(post){
    const node = document.createElement("input");
    node.className = 'item ' + post.id;
    node.id = "input";
    node.alt = '';
    node.onclick = callServer("input");
    flexbox.appendChild(node);
};