initHeader(null, true);
initFooter();


const profileView =
    document.getElementById("jp_profile-view");

const profileEdit =
    document.getElementById("jp_profile-edit");

const profileEditBtn =
    document.getElementById("jp_profile-edit-btn");


profileEditBtn.addEventListener("click", () => {

    profileView.style.display = "none";
    profileEdit.style.display = "block";
    profileEditBtn.style.display = "none";

});


document
    .getElementById("jp_cancel-profile-btn")
    .addEventListener("click", () => {

        profileView.style.display = "block";
        profileEdit.style.display = "none";
        profileEditBtn.style.display = "flex";

    });


document
    .getElementById("jp_save-profile-btn")
    .addEventListener("click", async () => {

        const nameInput =
            document.getElementById("jp_edit-name");

        const usernameInput =
            document.getElementById("jp_edit-username");

        const emailInput =
            document.getElementById("jp_edit-email");

        const phoneInput =
            document.getElementById("jp_edit-phone");

        const addressInput =
            document.getElementById("jp_edit-address");


        const fullName =
            nameInput.value.trim();

        const username =
            usernameInput.value.trim();

        const email =
            emailInput.value.trim();

        const phone =
            phoneInput.value.trim();

        const address =
            addressInput.value.trim();


        const namePattern =
            /^[A-Za-zÀ-ÖØ-öø-ÿ .'-]+$/;

        const usernamePattern =
            /^[A-Za-z0-9_.]+$/;

        const emailPattern =
            /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        const phonePattern =
            /^(?:\+977[- ]?)?(?:98|97|96)\d{8}$/;


        if (!fullName) {

            showToast("Full name is required.");
            nameInput.focus();

            return;
        }


        if (
            fullName.length < 2 ||
            fullName.length > 50 ||
            !namePattern.test(fullName)
        ) {

            showToast("Please enter a valid full name.");
            nameInput.focus();

            return;
        }


        if (!username) {

            showToast("Username is required.");
            usernameInput.focus();

            return;
        }


        if (
            username.length < 3 ||
            username.length > 30 ||
            !usernamePattern.test(username)
        ) {

            showToast(
                "Username must be 3-30 characters and use only letters, numbers, dots, or underscores."
            );

            usernameInput.focus();

            return;
        }


        if (!email) {

            showToast("Email address is required.");
            emailInput.focus();

            return;
        }


        if (
            email.length > 254 ||
            !emailPattern.test(email)
        ) {

            showToast(
                "Please enter a valid email address."
            );

            emailInput.focus();

            return;
        }


        if (
            phone &&
            !phonePattern.test(phone)
        ) {

            showToast(
                "Please enter a valid Nepal mobile number."
            );

            phoneInput.focus();

            return;
        }


        if (address.length > 200) {

            showToast(
                "Address must be 200 characters or less."
            );

            addressInput.focus();

            return;
        }


        try {

            const response =
                await fetch(
                    "/account/profile",
                    {
                        method: "POST",
                        headers: {
                            "Content-Type":
                                "application/json"
                        },
                        body: JSON.stringify({
                            fullName,
                            username,
                            email,
                            phone,
                            address
                        })
                    }
                );


            const result =
                await response.json();


            if (!result.success) {

                showToast(result.message);

                return;
            }


            document
                .getElementById("jp_view-name")
                .textContent = fullName;

            document
                .getElementById("jp_view-username")
                .textContent = username;

            document
                .getElementById("jp_view-email")
                .textContent = email;

            document
                .getElementById("jp_view-phone")
                .textContent =
                phone || "Not added";

            document
                .getElementById("jp_view-address")
                .textContent =
                address || "Not added";


            document
                .querySelector(".jp_acct-name")
                .textContent = fullName;

            document
                .querySelector(".jp_acct-avatar")
                .textContent =
                fullName
                    .charAt(0)
                    .toUpperCase();

            document
                .querySelector(".jp_acct-meta span span")
                .textContent = email;


            profileView.style.display = "block";
            profileEdit.style.display = "none";
            profileEditBtn.style.display = "flex";


            showToast(result.message);

        } catch (error) {

            showToast(
                "Unable to update profile."
            );

        }

    });


document
    .getElementById("jp_save-pw-btn")
    .addEventListener("click", async () => {

        const currentInput =
            document.getElementById("jp_current-pw");

        const newInput =
            document.getElementById("jp_new-pw");

        const confirmInput =
            document.getElementById("jp_confirm-pw");

        const msgEl =
            document.getElementById("jp_pw-msg");


        const currentPassword =
            currentInput.value;

        const newPassword =
            newInput.value;

        const confirmPassword =
            confirmInput.value;


        msgEl.textContent = "";
        msgEl.className =
            "jp_settings-msg";


        if (
            !currentPassword ||
            !newPassword ||
            !confirmPassword
        ) {

            msgEl.textContent =
                "Please fill in all password fields.";

            msgEl.className =
                "jp_settings-msg jp_msg-error";

            return;
        }


        if (newPassword.length < 8) {

            msgEl.textContent =
                "Password must be at least 8 characters.";

            msgEl.className =
                "jp_settings-msg jp_msg-error";

            return;
        }


        if (newPassword.length > 72) {

            msgEl.textContent =
                "Password must be 72 characters or less.";

            msgEl.className =
                "jp_settings-msg jp_msg-error";

            return;
        }


        if (newPassword !== confirmPassword) {

            msgEl.textContent =
                "New passwords do not match.";

            msgEl.className =
                "jp_settings-msg jp_msg-error";

            return;
        }


        try {

            const response =
                await fetch(
                    "/account/password",
                    {
                        method: "POST",
                        headers: {
                            "Content-Type":
                                "application/json"
                        },
                        body: JSON.stringify({
                            currentPassword,
                            newPassword
                        })
                    }
                );


            const result =
                await response.json();


            if (!result.success) {

                msgEl.textContent =
                    result.message;

                msgEl.className =
                    "jp_settings-msg jp_msg-error";

                return;
            }


            currentInput.value = "";
            newInput.value = "";
            confirmInput.value = "";

            msgEl.textContent = "";

            showToast(result.message);

        } catch (error) {

            msgEl.textContent =
                "Unable to update password.";

            msgEl.className =
                "jp_settings-msg jp_msg-error";

        }

    });


document
    .querySelectorAll(".jp_toggle-pw")
    .forEach(btn => {

        btn.addEventListener("click", () => {

            const input =
                btn
                    .closest(".jp_input-wrap")
                    .querySelector("input");

            const icon =
                btn.querySelector("i");


            const showing =
                input.type === "text";


            input.type =
                showing
                    ? "password"
                    : "text";


            icon.classList.toggle(
                "fa-eye",
                showing
            );

            icon.classList.toggle(
                "fa-eye-slash",
                !showing
            );

        });

    });


document
    .querySelectorAll(".jp_order-detail-btn")
    .forEach(btn => {

        btn.addEventListener("click", () => {

            const orderId =
                btn.dataset.order;

            if (!orderId) {
                return;
            }


            window.location.href =
                "/order-success?orderId=" +
                encodeURIComponent(orderId);

        });

    });


document
    .getElementById("jp_delete-btn")
    .addEventListener("click", () => {

        showConfirmModal(
            "Delete Account?",
            "Are you sure you want to delete your account? This cannot be undone.",
            "Delete",
            async () => {

                try {

                    const response =
                        await fetch(
                            "/account/delete",
                            {
                                method: "POST"
                            }
                        );


                    const result =
                        await response.json();


                    if (!result.success) {

                        showToast(
                            result.message
                        );

                        return;
                    }


                    window.location.href =
                        "/";

                } catch (error) {

                    showToast(
                        "Unable to delete account."
                    );

                }

            }
        );

    });


let toastTimer = null;


function showToast(message) {

    const toast =
        document.getElementById("jp_toast");

    const toastMsg =
        document.getElementById("jp_toast-msg");


    toastMsg.textContent =
        message;

    toast.classList.add(
        "jp_toast-show"
    );


    clearTimeout(toastTimer);


    toastTimer =
        setTimeout(() => {

            toast.classList.remove(
                "jp_toast-show"
            );

        }, 2500);
}