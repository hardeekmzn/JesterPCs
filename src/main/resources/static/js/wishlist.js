initHeader("jp_nav-wishlist");
initFooter();

let wishlistStarted = false;

document.addEventListener(
    "DOMContentLoaded",
    initWishlistPage
);

window.addEventListener(
    "jp_header_loaded",
    initWishlistPage
);

function initWishlistPage() {

    if (wishlistStarted) return;

    const grid =
        document.querySelector(".jp_wishlist-grid");

    if (!grid) return;

    wishlistStarted = true;


    /* Remove from wishlist */

    grid.addEventListener(
        "click",
        e => {

            const button =
                e.target.closest(
                    ".jp_wish-remove"
                );

            if (!button) return;

            const productId =
                button.dataset.productId;

            const body =
                new URLSearchParams();

            body.append(
                "productId",
                productId
            );


            fetch(
                "/wishlist/remove-ajax",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded"
                    },

                    body
                }
            )
                .then(response =>
                    response.json()
                )
                .then(result => {

                    if (!result.success) {

                        if (
                            result.loggedIn === false
                        ) {
                            window.location.href =
                                "/login";
                        }

                        return;
                    }


                    const card =
                        button.closest(
                            ".jp_wish-card"
                        );

                    if (card) {
                        card.remove();
                    }


                    updateWishlistCount();

                    showToast(
                        "Removed from wishlist"
                    );


                    if (
                        !grid.querySelector(
                            ".jp_wish-card"
                        )
                    ) {

                        showEmptyWishlist();

                    }

                })
                .catch(error =>
                    console.error(
                        "Could not remove wishlist item:",
                        error
                    )
                );
        }
    );


    /* Add to cart */

    grid.addEventListener(
        "click",
        e => {

            const button =
                e.target.closest(
                    ".jp_wish-cart-btn"
                );

            if (
                !button ||
                button.disabled
            ) return;


            fetch(
                "/auth-status",
                {
                    cache: "no-store"
                }
            )
                .then(response =>
                    response.json()
                )
                .then(status => {

                    if (!status.loggedIn) {

                        window.location.href =
                            "/login";

                        return null;
                    }


                    const body =
                        new URLSearchParams();

                    body.append(
                        "productId",
                        button.dataset.productId
                    );


                    return fetch(
                        "/cart/add-ajax",
                        {
                            method: "POST",

                            headers: {
                                "Content-Type":
                                    "application/x-www-form-urlencoded"
                            },

                            body
                        }
                    );
                })
                .then(response =>
                    response
                        ? response.json()
                        : null
                )
                .then(result => {

                    if (
                        !result ||
                        !result.success
                    ) return;


                    const cartCount =
                        document.getElementById(
                            "jp_cart-count"
                        );


                    if (cartCount) {
                        cartCount.textContent =
                            result.count;
                    }


                    showToast(
                        button.dataset.name +
                        " added to cart"
                    );

                })
                .catch(error =>
                    console.error(
                        "Could not add to cart:",
                        error
                    )
                );
        }
    );


    function updateWishlistCount() {

        const cards =
            grid.querySelectorAll(
                ".jp_wish-card"
            );

        const count =
            cards.length;


        const countElement =
            document.querySelector(
                ".jp_wishlist-count"
            );


        if (countElement) {

            countElement.textContent =
                count +
                (count === 1
                    ? " item"
                    : " items");
        }
    }


    function showEmptyWishlist() {

        const emptyState =
            document.createElement("div");

        emptyState.className =
            "jp_wishlist-empty";

        emptyState.innerHTML = `
            <i class="fa-regular fa-heart"></i>

            <h2>
                Your wishlist is empty
            </h2>

            <p>
                Save products you like and come back to them later.
            </p>

            <a href="/marketplace"
               class="jp_wishlist-shop-btn">
                Browse Marketplace
            </a>
        `;


        const page =
            document.querySelector(
                ".jp_wishlist-page"
            );

        page.appendChild(emptyState);

        grid.remove();
    }


    function showToast(message) {

        const toast =
            document.getElementById(
                "jp_toast"
            );

        const toastMsg =
            document.getElementById(
                "jp_toast-msg"
            );


        if (!toast || !toastMsg) return;


        toastMsg.textContent =
            message;


        toast.classList.add(
            "jp_toast-show"
        );


        setTimeout(
            () =>
                toast.classList.remove(
                    "jp_toast-show"
                ),
            2500
        );
    }
}