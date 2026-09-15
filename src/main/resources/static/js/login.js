/* Header and footer */
initHeader(null);
initFooter();

/* Password visibility toggle */
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