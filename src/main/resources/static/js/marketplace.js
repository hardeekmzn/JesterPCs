initHeader("jp_nav-marketplace");
initFooter();

let marketplaceStarted = false;

document.addEventListener("DOMContentLoaded", initMarketplacePage);
window.addEventListener("jp_header_loaded", initMarketplacePage);

function initMarketplacePage() {

    if (marketplaceStarted) return;

    const grid = document.getElementById("jp_grid");
    const pagination =
        document.getElementById("jp_pagination");

    if (!grid || !pagination) return;

    marketplaceStarted = true;

    const cards =
        Array.from(grid.querySelectorAll(".jp_card"));

    const searchInput =
        document.getElementById("jp_search");

    const sortSelect =
        document.getElementById("jp_sort");

    const priceRange =
        document.getElementById("jp_price-range");

    const priceVal =
        document.getElementById("jp_price-val");

    const inStockOnly =
        document.getElementById("jp_in-stock");

    const countEl =
        document.getElementById("jp_count");

    const emptyEl =
        document.getElementById("jp_empty");

    const categoryList =
        document.getElementById("jp_cat-list");

    const resetButton =
        document.getElementById("jp_reset-btn");

    const brandChecks =
        Array.from(
            document.querySelectorAll(".jp_brand-check")
        );

    let activeCategory = "all";
    let currentPage = 1;

    const productsPerPage = 12;


    const normalize = value =>
        String(value || "").trim().toLowerCase();


    function getActiveBrands() {

        const checkedBrands =
            brandChecks
                .filter(cb => cb.checked)
                .map(cb => normalize(cb.value));

        const allBrandsChecked =
            checkedBrands.length === brandChecks.length;

        return allBrandsChecked
            ? null
            : checkedBrands;
    }


    function getFilteredProducts() {

        const query =
            normalize(searchInput.value);

        const maxPrice =
            Number(priceRange.value);

        const activeBrands =
            getActiveBrands();


        return cards
            .filter(card => {

                const category =
                    normalize(card.dataset.category);

                const brand =
                    normalize(card.dataset.brand);

                const price =
                    Number(card.dataset.price) || 0;

                const stock =
                    card.dataset.stock === "true";

                const name =
                    normalize(
                        card.querySelector(
                            ".jp_card-name"
                        ).textContent
                    );


                return (
                    (activeCategory === "all" ||
                        category === activeCategory) &&

                    (!activeBrands ||
                        activeBrands.includes(brand)) &&

                    price <= maxPrice &&

                    (!inStockOnly.checked ||
                        stock) &&

                    (!query ||
                        name.includes(query))
                );
            })
            .sort((a, b) => {

                const priceA =
                    Number(a.dataset.price) || 0;

                const priceB =
                    Number(b.dataset.price) || 0;

                const dateA =
                    Number(a.dataset.date) || 0;

                const dateB =
                    Number(b.dataset.date) || 0;


                return {
                    "price-asc":
                        priceA - priceB,

                    "price-desc":
                        priceB - priceA,

                    "newest":
                        dateB - dateA,

                    "featured":
                        dateA - dateB

                }[sortSelect.value];
            });
    }


    function applyFilters() {

        const visible =
            getFilteredProducts();

        const totalPages =
            Math.ceil(
                visible.length /
                productsPerPage
            );


        currentPage =
            totalPages === 0
                ? 1
                : Math.min(
                    currentPage,
                    totalPages
                );


        cards.forEach(card => {

            card.style.display = "none";
            card.classList.remove(
                "jp_card-visible"
            );

        });


        const start =
            (currentPage - 1) *
            productsPerPage;


        visible
            .slice(
                start,
                start + productsPerPage
            )
            .forEach((card, index) => {

                card.style.display = "flex";

                setTimeout(() => {

                    card.classList.add(
                        "jp_card-visible"
                    );

                }, index * 40);

            });


        countEl.textContent =
            visible.length;


        emptyEl.style.display =
            visible.length
                ? "none"
                : "flex";


        renderPagination(totalPages);
    }


    function renderPagination(totalPages) {

        pagination.innerHTML = "";

        pagination.style.display =
            totalPages > 1
                ? "flex"
                : "none";


        if (totalPages <= 1) return;


        const previous =
            document.createElement("button");

        previous.type = "button";
        previous.className = "jp_page-btn";
        previous.textContent = "Previous";
        previous.disabled = currentPage === 1;


        previous.addEventListener("click", () => {

            currentPage =
                Math.max(1, currentPage - 1);

            applyFilters();
            scrollToProducts();

        });


        pagination.appendChild(previous);


        Array.from(
            { length: totalPages },
            (_, index) => index + 1
        ).forEach(page => {

            const button =
                document.createElement("button");

            button.type = "button";
            button.className = "jp_page-btn";
            button.textContent = page;


            button.classList.toggle(
                "jp_page-active",
                page === currentPage
            );


            button.addEventListener(
                "click",
                () => {

                    currentPage = page;

                    applyFilters();
                    scrollToProducts();

                }
            );


            pagination.appendChild(button);
        });


        const next =
            document.createElement("button");

        next.type = "button";
        next.className = "jp_page-btn";
        next.textContent = "Next";
        next.disabled =
            currentPage === totalPages;


        next.addEventListener("click", () => {

            currentPage =
                Math.min(
                    totalPages,
                    currentPage + 1
                );

            applyFilters();
            scrollToProducts();

        });


        pagination.appendChild(next);
    }


    function scrollToProducts() {

        window.scrollTo({

            top:
                grid.getBoundingClientRect().top +
                window.scrollY -
                100,

            behavior: "smooth"

        });
    }


    categoryList.addEventListener(
        "click",
        e => {

            const button =
                e.target.closest(
                    ".jp_filter-btn"
                );

            if (!button) return;


            document
                .querySelectorAll(
                    ".jp_filter-btn"
                )
                .forEach(btn =>
                    btn.classList.remove(
                        "jp_filter-active"
                    )
                );


            button.classList.add(
                "jp_filter-active"
            );


            activeCategory =
                normalize(
                    button.dataset.filter
                );


            currentPage = 1;

            applyFilters();
        }
    );


    searchInput.addEventListener(
        "input",
        () => {

            currentPage = 1;
            applyFilters();

        }
    );


    sortSelect.addEventListener(
        "change",
        () => {

            currentPage = 1;
            applyFilters();

        }
    );


    inStockOnly.addEventListener(
        "change",
        () => {

            currentPage = 1;
            applyFilters();

        }
    );


    priceRange.addEventListener(
        "input",
        () => {

            priceVal.textContent =
                "Rs. " +
                Number(
                    priceRange.value
                ).toLocaleString("en-IN");


            currentPage = 1;

            applyFilters();
        }
    );


    brandChecks.forEach(check => {

        check.addEventListener(
            "change",
            () => {

                currentPage = 1;
                applyFilters();

            }
        );

    });


    resetButton.addEventListener(
        "click",
        () => {

            searchInput.value = "";

            sortSelect.value =
                "featured";


            priceRange.value =
                400000;

            priceVal.textContent =
                "Rs. 4,00,000";


            inStockOnly.checked =
                false;


            brandChecks.forEach(
                check => check.checked = true
            );


            document
                .querySelectorAll(
                    ".jp_filter-btn"
                )
                .forEach(
                    button =>
                        button.classList.remove(
                            "jp_filter-active"
                        )
                );


            document
                .querySelector(
                    '[data-filter="all"]'
                )
                .classList.add(
                "jp_filter-active"
            );


            activeCategory = "all";
            currentPage = 1;

            applyFilters();
        }
    );


    /* Add to cart */

    grid.addEventListener(
        "click",
        e => {

            const button =
                e.target.closest(
                    ".jp_add-btn"
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


    /* Wishlist */

    grid.addEventListener(
        "click",
        e => {

            const button =
                e.target.closest(
                    ".jp_wish"
                );

            if (!button) return;

            const productId =
                button.dataset.productId;

            const isActive =
                button.classList.contains(
                    "jp_wish-active"
                );

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
                        productId
                    );

                    const url =
                        isActive
                            ? "/wishlist/remove-ajax"
                            : "/wishlist/add-ajax";

                    return fetch(
                        url,
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

                    const icon =
                        button.querySelector("i");

                    const nowActive =
                        !isActive;

                    button.classList.toggle(
                        "jp_wish-active",
                        nowActive
                    );

                    icon.classList.toggle(
                        "fa-solid",
                        nowActive
                    );

                    icon.classList.toggle(
                        "fa-regular",
                        !nowActive
                    );

                    showToast(
                        nowActive
                            ? "Added to wishlist"
                            : "Removed from wishlist"
                    );
                })
                .catch(error =>
                    console.error(
                        "Could not update wishlist:",
                        error
                    )
                );
        }
    );


    function showToast(message) {

        const toast =
            document.getElementById(
                "jp_toast"
            );

        const toastMsg =
            document.getElementById(
                "jp_toast-msg"
            );


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


    applyFilters();
}