document.getElementById("form-sign-in").onsubmit = function(e) {
    e.preventDefault();
    let query = window.location.search
    let queryParam = new URLSearchParams(query)
    let audiences = queryParam.get("audience")
    let scopes = queryParam.get("scopes")
    let formdata = new FormData(this);
    let jsonObject = {}
    formdata.forEach((value, key) => jsonObject[key] = value)
    fetch(`/api/auth/sign-in?audiences=${audiences != null ? audiences : ""}&scopes=${scopes != null ? scopes: ""}`,{
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(jsonObject)
    }).then(response => {
        if (!response.ok)
            throw Error(response.statusText)
        return response.json()
    }).then(data => {
        let returnUrl = queryParam.get("returnUrl");
        console.log(data)
        if (returnUrl != null)
            location.replace(`${returnUrl}?grant=${data.content}`)
    }).catch(err => {
        console.error(`Cached error: ${err}`)
    })
};

(function() {

    const faEyeIcon = "/assets/vectors/fa-eye.svg"
    const faEyeSlashIcon = `/assets/vectors/fa-eye-slash.svg`

    let passwordButton = document.getElementById("password-visible")
    let passwordInputs = passwordButton.parentElement.querySelectorAll("input[type=password]")

    passwordButton.setAttribute("data-pwd-visible", "false");

    passwordButton.onclick = function (e) {
        let icon = passwordButton.getElementsByTagName("img")[0];
        if (passwordButton.getAttribute("data-pwd-visible") === "false"){
            passwordInputs.forEach(el => el.type = "text");
            icon.src = faEyeIcon;
            passwordButton.setAttribute("data-pwd-visible", "true");
        }
        else {
            passwordInputs.forEach(el => el.type = "password")
            icon.src = faEyeSlashIcon;
            passwordButton.setAttribute("data-pwd-visible", "false");
        }
    }
})();

/*
document.getElementById("form-sign-up").onsubmit = function(e) {
    e.preventDefault();
    let formdata = new FormData(this);
    let jsonObject = {}
    formdata.forEach((value, key) => jsonObject[key] = value)
    fetch("/api/auth/sign-up",{
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(jsonObject)
    }).then(response => {
        if (!response.ok)
            throw Error(response.statusText)
        return response.json()
    }).then(data => {
        console.log(data)
    }).catch(err => {
        console.error(`Cached error: ${err}`)
    })
}
*/