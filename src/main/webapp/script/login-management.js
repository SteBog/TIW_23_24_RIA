
function check_login(form) {
    var req = new XMLHttpRequest();
    req.onreadystatechange = () => {
        if (req.readyState == XMLHttpRequest.DONE) {
            var message = req.responseText;
            switch (req.status) {
                case 200:
                    sessionStorage.setItem('username', message);
                    window.location.replace("home.html");
                    break;
                default:
                    document.getElementById('error-message').textContent = message;
                    break;
            }
        }
    }
    req.open("POST", "CheckLogin");
    req.send(new FormData(form));
}

(function () {
    document.getElementById("login-button").addEventListener('click', (e) => {
        var form = e.target.closest('form');
        if (form.reportValidity()) {
            check_login(form);
        }
    })
})();