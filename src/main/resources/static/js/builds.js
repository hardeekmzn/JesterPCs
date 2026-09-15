initHeader("jp_nav-builds");
initFooter();

window.addEventListener("jp_header_loaded", initBuildsPage);

function initBuildsPage() {

    const tabsBar = document.querySelector(".jp_tabs-bar");

    const tabsOffset = tabsBar.offsetTop;

    function updateTabs() {
        if (window.scrollY >= tabsOffset) {
            tabsBar.classList.add("jp_tabs-fixed");
        } else {
            tabsBar.classList.remove("jp_tabs-fixed");
        }
    }

    window.addEventListener("scroll", updateTabs);
    updateTabs();


    /* Tab switching */
    document.querySelectorAll(".jp_tab").forEach(tab => {
        tab.addEventListener("click", () => {

            const build = tab.dataset.build;

            document.querySelectorAll(".jp_tab").forEach(t =>
                t.classList.remove("jp_tab-active")
            );

            document.querySelectorAll(".jp_panel").forEach(p =>
                p.classList.remove("jp_panel-active")
            );

            tab.classList.add("jp_tab-active");

            document.getElementById(
                "jp_panel-" + build
            ).classList.add("jp_panel-active");
        });
    });

}