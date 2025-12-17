document.addEventListener("DOMContentLoaded", function () {
    document.querySelector("form").addEventListener("submit", function (event) {
        // Get input values
        const username = document.getElementById("username").value;
        const password = document.getElementById("password").value;

        // Validation
        if (username !== "zawyenaung" || password !== "naungnaung") {
            alert("Invalid username or password");
            event.preventDefault(); // Prevent form submission
        }
    });
});