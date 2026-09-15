initHeader();
initFooter();

window.addEventListener("jp_header_loaded", initCartPage);

function initCartPage() {

    const promoInput =
        document.getElementById("jp_promo-input");

    const promoButton =
        document.getElementById("jp_promo-btn");

    const promoMessage =
        document.getElementById("jp_promo-msg");

    const discountLine =
        document.getElementById("jp_discount-line");

    const discountValue =
        document.getElementById("jp_discount-val");

    const subtotalElement =
        document.getElementById("jp_subtotal");

    const totalElement =
        document.getElementById("jp_grand-total");

    const checkoutButton =
        document.getElementById("jp_checkout-btn");


    const DELIVERY = 200;

    const PROMOS = {
        JESTER10: 0.10,
        NEPAL20: 0.20
    };


    function getSubtotal() {

        const value =
            subtotalElement.textContent
                .replace(/[^\d.]/g, "");

        return parseFloat(value) || 0;
    }


    function formatPrice(value) {

        return "Rs. " +
            value.toLocaleString("en-IN");
    }


    if (promoButton) {

        promoButton.addEventListener(
            "click",
            () => {

                const code =
                    promoInput.value
                        .trim()
                        .toUpperCase();

                const discount =
                    PROMOS[code];


                if (!discount) {

                    promoMessage.textContent =
                        "Invalid promo code.";

                    promoMessage.className =
                        "jp_promo-msg jp_promo-error";

                    discountLine.style.display =
                        "none";

                    totalElement.textContent =
                        formatPrice(
                            getSubtotal() + DELIVERY
                        );

                    return;
                }


                const subtotal =
                    getSubtotal();

                const discountAmount =
                    Math.round(
                        subtotal * discount
                    );

                const total =
                    subtotal +
                    DELIVERY -
                    discountAmount;


                discountValue.textContent =
                    "− " +
                    formatPrice(discountAmount);

                discountLine.style.display =
                    "flex";

                totalElement.textContent =
                    formatPrice(total);

                promoMessage.textContent =
                    (discount * 100) +
                    "% discount applied.";

                promoMessage.className =
                    "jp_promo-msg jp_promo-success";
            }
        );
    }


    if (checkoutButton) {

        checkoutButton.addEventListener(
            "click",
            () => {

                window.location.href =
                    "/checkout";

            }
        );
    }
}