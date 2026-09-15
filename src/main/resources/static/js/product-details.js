initHeader(null);
initFooter();

const addButton =
    document.querySelector(".jp_add-product-btn");

if (addButton) {

    addButton.addEventListener("click", () => {

        fetch("/auth-status", {
            cache: "no-store"
        })
            .then(response => response.json())
            .then(status => {

                if (!status.loggedIn) {
                    window.location.href = "/login";
                    return;
                }

                const productId =
                    addButton.dataset.productId;

                const formData =
                    new URLSearchParams();

                formData.append(
                    "productId",
                    productId
                );

                return fetch("/cart/add", {
                    method: "POST",
                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded"
                    },
                    body: formData
                });

            })
            .then(response => {

                if (!response) {
                    return;
                }

                if (response.redirected) {
                    window.location.href =
                        response.url;
                }

            })
            .catch(error => {

                console.error(
                    "Could not add product to cart:",
                    error
                );

            });

    });

}


/* Review rating */

const ratingOptions =
    document.querySelectorAll(".jp_rating-option");

const ratingInputs =
    document.querySelectorAll(
        ".jp_rating-option input[name='rating']"
    );

if (ratingOptions.length > 0 &&
    ratingInputs.length > 0) {

    function updateStars(selectedRating) {

        ratingOptions.forEach(option => {

            const input =
                option.querySelector("input");

            const rating =
                Number(input.value);

            const label =
                option.querySelector("label");

            if (rating <= selectedRating) {

                label.style.color =
                    "var(--clr-accent)";

            } else {

                label.style.color =
                    "#333";
            }

        });
    }


    ratingOptions.forEach(option => {

        const input =
            option.querySelector("input");

        const label =
            option.querySelector("label");

        label.addEventListener("mouseenter", () => {

            updateStars(
                Number(input.value)
            );

        });


        input.addEventListener("change", () => {

            updateStars(
                Number(input.value)
            );

        });

    });


    const ratingSelect =
        document.querySelector(".jp_rating-select");

    if (ratingSelect) {

        ratingSelect.addEventListener("mouseleave", () => {

            const selectedInput =
                document.querySelector(
                    ".jp_rating-option input[name='rating']:checked"
                );

            if (selectedInput) {

                updateStars(
                    Number(selectedInput.value)
                );

            } else {

                updateStars(0);
            }

        });

    }

}