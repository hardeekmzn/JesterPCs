/* Header and footer */
initHeader(null);
initFooter();

/* Password visibility toggles */
document.querySelectorAll(".jp_toggle-pw").forEach(btn => {
    btn.addEventListener("click", () => {
        const input = btn.closest(".jp_input-wrap").querySelector("input");
        const icon  = btn.querySelector("i");
        if (input.type === "password") {
            input.type = "text";
            icon.classList.replace("fa-eye", "fa-eye-slash");
        } else {
            input.type = "password";
            icon.classList.replace("fa-eye-slash", "fa-eye");
        }
    });
});

/* Form validation */
document.getElementById("jp_signup-form").addEventListener("submit", (e) => {
    e.preventDefault();

    const pw  = document.getElementById("password").value;
    const cpw = document.getElementById("confirm").value;

    if (pw !== cpw) {
        showError("Passwords do not match.");
        return;
    }
    if (pw.length < 8) {
        showError("Password must be at least 8 characters.");
        return;
    }
    if (!document.getElementById("terms").checked) {
        showError("Please agree to the Terms of Service.");
        return;
    }

    e.target.submit();
});

function showError(msg) {
    const el = document.getElementById("jp_form-error");
    if (el) {
        el.textContent = msg;
        el.style.display = "block";
    }
}