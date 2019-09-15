let resp;
let index = 1;
function elem(id) {
    return document.getElementById(id)
}
const flexbox = document.getElementsByClassName('flexbox')[0];

async function fetching() {
    await fetch("/test")
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
            elem("question").innerHTML = `<h1 style="  font-family: sans-serif;
  background: #222;
  color: darkred;
    text-shadow: 2px 0 0 #fff, -2px 0 0 #fff, 0 2px 0 #fff, 0 -2px 0 #fff, 1px 1px #fff, -1px -1px 0 #fff, 1px -1px 0 #fff, -1px 1px 0 #fff;
">ERROR, can't establish connection to server! </h1>`});
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