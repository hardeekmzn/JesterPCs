/* Load a reusable component */
function loadComponent(file, targetId) {

    return fetch(file)
        .then(res => res.text())
        .then(html => {
            document.getElementById(targetId).innerHTML = html;
        });
}


/* Set the active navigation link */
function setActiveNav(linkId) {

    if (!linkId) return;

    const link = document.getElementById(linkId);

    if (link) {
        link.classList.add("jp_nav-active");
    }
}


/* Mobile menu */
function initMobileMenu() {

    const toggle = document.getElementById("jp_menu-toggle");
    const navs = document.querySelectorAll(".jp_nav");

    if (!toggle || !navs.length) return;

    toggle.addEventListener("click", () => {

        navs.forEach(nav => {
            nav.classList.toggle("jp_nav-open");
        });

        toggle.classList.toggle("jp_menu-open");

    });
}


/* Header scroll effect */
function initHeaderScroll() {

    const header = document.getElementById("jp_header");

    if (!header) return;

    window.addEventListener("scroll", () => {

        header.classList.toggle(
            "jp_header-scrolled",
            window.scrollY > 60
        );

    });
}


/* Cart count */
function syncCartCount() {

    const badge =
        document.getElementById("jp_cart-count");

    if (!badge) {
        return;
    }

    fetch("/cart/count", {
        cache: "no-store"
    })
        .then(response => response.json())
        .then(count => {
            badge.textContent = count;
        })
        .catch(error => {

            console.error(
                "Could not load cart count:",
                error
            );

            badge.textContent = "0";

        });
}


/* Initialize header */
function initHeader(activeNavId, scrolling = false) {

    loadComponent("/header.html", "jp_header-mount")
        .then(() => {

            setActiveNav(activeNavId);

            initMobileMenu();

            syncCartCount();

            if (scrolling) {
                initHeaderScroll();
            }

            window.dispatchEvent(
                new Event("jp_header_loaded")
            );

        });
}


/* Initialize footer */
function initFooter() {

    loadComponent("/footer.html", "jp_footer-mount");
}