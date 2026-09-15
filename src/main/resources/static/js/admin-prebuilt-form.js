function initPrebuiltForm() {

    const search = document.getElementById("productSearch");
    const categoryFilter = document.getElementById("categoryFilter");
    const options = document.querySelectorAll(".jp_admin-product-option");
    const selectedCount = document.querySelector(".jp_admin-selected-count");

    if (!options.length) {
        return;
    }

    function updateSelected() {

        let count = 0;

        options.forEach(option => {

            const checkbox =
                option.querySelector('input[type="checkbox"]');

            if (checkbox.checked) {
                option.classList.add("selected");
                count++;
            } else {
                option.classList.remove("selected");
            }
        });

        selectedCount.textContent =
            count +
            (count === 1
                ? " component selected"
                : " components selected");
    }

    function filterProducts() {

        const searchText =
            search.value.toLowerCase().trim();

        const selectedCategory =
            categoryFilter.value;

        options.forEach(option => {

            const text =
                option.textContent.toLowerCase();

            const category =
                option.dataset.category;

            const matchesSearch =
                text.includes(searchText);

            const matchesCategory =
                selectedCategory === "all" ||
                category === selectedCategory;

            if (matchesSearch && matchesCategory) {
                option.classList.remove("hidden");
            } else {
                option.classList.add("hidden");
            }
        });
    }

    options.forEach(option => {

        const checkbox =
            option.querySelector('input[type="checkbox"]');

        checkbox.addEventListener(
            "change",
            updateSelected
        );
    });

    search.addEventListener(
        "input",
        filterProducts
    );

    categoryFilter.addEventListener(
        "change",
        filterProducts
    );

    updateSelected();
}

document.addEventListener(
    "DOMContentLoaded",
    initPrebuiltForm
);