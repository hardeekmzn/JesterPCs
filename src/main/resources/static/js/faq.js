initHeader(null);
initFooter();

/* Accordion — each FAQ item opens/closes on click */
document.querySelectorAll(".jp_faq-q").forEach(btn => {
    btn.addEventListener("click", () => {
        const item     = btn.closest(".jp_faq-item");
        const isOpen   = item.classList.contains("jp_faq-open");

        /* Close all open items */
        document.querySelectorAll(".jp_faq-item.jp_faq-open").forEach(el => {
            el.classList.remove("jp_faq-open");
        });

        /* Open clicked one if it was closed */
        if (!isOpen) item.classList.add("jp_faq-open");
    });
});

/* Search — filters questions by text match */
document.getElementById("jp_faq-search").addEventListener("input", (e) => {
    const query = e.target.value.toLowerCase().trim();

    document.querySelectorAll(".jp_faq-item").forEach(item => {
        const q = item.querySelector(".jp_faq-q").textContent.toLowerCase();
        const a = item.querySelector(".jp_faq-a").textContent.toLowerCase();
        item.style.display = (!query || q.includes(query) || a.includes(query)) ? "block" : "none";
    });

    /* Hide group headings if all their items are hidden */
    document.querySelectorAll(".jp_faq-group").forEach(group => {
        const visible = Array.from(group.querySelectorAll(".jp_faq-item"))
            .some(item => item.style.display !== "none");
        group.style.display = visible ? "block" : "none";
    });
});