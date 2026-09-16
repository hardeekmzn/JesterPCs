let jpConfirmReady = null;
let jpConfirmAction = null;


function loadConfirmModal() {

    if (jpConfirmReady) {
        return jpConfirmReady;
    }


    jpConfirmReady =
        fetch("/components/confirm-modal")
            .then(response => {

                if (!response.ok) {
                    throw new Error("Could not load confirmation modal.");
                }

                return response.text();

            })
            .then(html => {

                const mount =
                    document.createElement("div");

                mount.innerHTML = html;

                document.body.appendChild(
                    mount.firstElementChild
                );

                initConfirmModal();

            })
            .catch(error => {

                console.error(
                    "Could not load confirmation modal:",
                    error
                );

            });


    return jpConfirmReady;
}


function initConfirmModal() {

    const modal =
        document.getElementById("jp-confirm-modal");

    const closeButton =
        document.getElementById("jp-confirm-close");

    const cancelButton =
        document.getElementById("jp-confirm-cancel");

    const deleteButton =
        document.getElementById("jp-confirm-delete");

    const backdrop =
        modal.querySelector(".jp-confirm-backdrop");


    closeButton.addEventListener(
        "click",
        closeConfirmModal
    );


    cancelButton.addEventListener(
        "click",
        closeConfirmModal
    );


    backdrop.addEventListener(
        "click",
        closeConfirmModal
    );


    deleteButton.addEventListener(
        "click",
        async () => {

            if (typeof jpConfirmAction === "function") {

                const action =
                    jpConfirmAction;

                closeConfirmModal();

                await action();

            }

        }
    );


    document.addEventListener(
        "keydown",
        event => {

            if (
                event.key === "Escape" &&
                modal.classList.contains("jp-confirm-open")
            ) {

                closeConfirmModal();

            }

        }
    );


    document
        .querySelectorAll(".jp_confirm-delete")
        .forEach(element => {

            element.addEventListener(
                "click",
                handleConfirmLink
            );

        });


    document
        .querySelectorAll(".jp_confirm-delete-form")
        .forEach(form => {

            form.addEventListener(
                "submit",
                handleConfirmForm
            );

        });

}


function handleConfirmLink(event) {

    event.preventDefault();

    const element =
        event.currentTarget;

    const title =
        element.dataset.confirmTitle ||
        "Are you sure?";

    const message =
        element.dataset.confirmMessage ||
        "This action cannot be undone.";

    const actionText =
        element.dataset.confirmAction ||
        "Delete";

    const url =
        element.href;


    showConfirmModal(
        title,
        message,
        actionText,
        () => {

            window.location.href =
                url;

        }
    );

}


function handleConfirmForm(event) {

    event.preventDefault();

    const form =
        event.currentTarget;

    const title =
        form.dataset.confirmTitle ||
        "Are you sure?";

    const message =
        form.dataset.confirmMessage ||
        "This action cannot be undone.";

    const actionText =
        form.dataset.confirmAction ||
        "Delete";


    showConfirmModal(
        title,
        message,
        actionText,
        () => {

            form.submit();

        }
    );

}


function showConfirmModal(
    title,
    message,
    actionText,
    action) {

    loadConfirmModal()
        .then(() => {

            const modal =
                document.getElementById("jp-confirm-modal");

            const titleElement =
                document.getElementById("jp-confirm-title");

            const messageElement =
                document.getElementById("jp-confirm-message");

            const deleteButton =
                document.getElementById("jp-confirm-delete");


            titleElement.textContent =
                title;

            messageElement.textContent =
                message;

            deleteButton.textContent =
                actionText;

            jpConfirmAction =
                action;

            modal.classList.add(
                "jp-confirm-open"
            );

            modal.setAttribute(
                "aria-hidden",
                "false"
            );

            document.body.style.overflow =
                "hidden";

        });

}


function closeConfirmModal() {

    const modal =
        document.getElementById("jp-confirm-modal");

    if (!modal) {
        return;
    }


    modal.classList.remove(
        "jp-confirm-open"
    );


    modal.setAttribute(
        "aria-hidden",
        "true"
    );


    document.body.style.overflow =
        "";


    jpConfirmAction =
        null;

}


document.addEventListener(
    "DOMContentLoaded",
    () => {

        loadConfirmModal();

    }
);