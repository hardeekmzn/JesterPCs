initHeader("jp_nav-custom");
initFooter();

window.addEventListener("jp_header_loaded", initCustomPage);

function initCustomPage() {

    /* PRODUCT DATA */
    const PRODUCTS = {
        cpu: [],
        gpu: [],
        ram: [],
        storage: [],
        motherboard: [],
        psu: [],
        case: [],
        cooling: []
    };

    DB_PRODUCTS.forEach(product => {

        const category = product.categoryName
            ? product.categoryName.toLowerCase()
            : "";

        if (!PRODUCTS[category]) {
            return;
        }

        PRODUCTS[category].push({
            id: product.productId,
            name: product.productName,
            price: Number(product.price),
            spec: product.specifications || "",
            socket: product.socketType,
            ramType: product.ramType,
            ramSpeed: product.ramSpeed,
            storageInterface: product.storageInterface,
            wattage: product.wattage,
            tdp: category === "cpu"
                ? Number(product.wattage || 0)
                : 0,
            powerDraw: category === "gpu"
                ? Number(product.wattage || 0)
                : 0,
            gpuLength: product.gpuLength,
            coolerHeight: product.coolerHeight,
            tier: product.performanceTier,
            img: product.imageUrl
        });
    });


    /* Preset builds */
    const PRESETS = {
        budget: { cpu: 0, gpu: 0, ram: 0, storage: 0, motherboard: 0, psu: 0, case: 0, cooling: 0 },
        mid:    { cpu: 1, gpu: 1, ram: 1, storage: 1, motherboard: 1, psu: 1, case: 1, cooling: 1 },
        max:    { cpu: 2, gpu: 2, ram: 2, storage: 1, motherboard: 2, psu: 2, case: 2, cooling: 1 }
    };


    /* STATE — null means no part selected for that slot */
    const state = {
        cpu: null, gpu: null, ram: null, storage: null,
        motherboard: null, psu: null, case: null, cooling: null,
    };

    let toastTimer = null;
    let activeSlot = null;


    /* FORMAT HELPERS */
    function formatPrice(n) {
        return n === 0 ? "Included" : "Rs. " + n.toLocaleString("en-IN");
    }

    const TIER_ORDER = { budget: 1, mid: 2, max: 3 };


    /* RENDER SLOT */
    function renderSlot(slot) {
        const part      = state[slot];
        const nameEl    = document.getElementById("jp_slot-name-" + slot);
        const specEl    = document.getElementById("jp_slot-spec-" + slot);
        const priceEl   = document.getElementById("jp_slot-price-" + slot);
        const slotEl    = document.querySelector(".jp_slot[data-slot='" + slot + "']");
        const chooseBtn = slotEl.querySelector(".jp_choose-btn");

        if (part) {
            nameEl.textContent  = part.name;
            specEl.textContent  = part.spec;
            priceEl.textContent = formatPrice(part.price);
            slotEl.classList.add("jp_slot-filled");
            chooseBtn.innerHTML = 'Change <i class="fa-solid fa-arrow-right-arrow-left"></i>';
        } else {
            nameEl.textContent  = "No " + slot.charAt(0).toUpperCase() + slot.slice(1) + " selected";
            specEl.textContent  = "";
            priceEl.textContent = "";
            slotEl.classList.remove("jp_slot-filled");
            chooseBtn.innerHTML = 'Choose <i class="fa-solid fa-plus"></i>';
        }
    }


    /* COMPATIBILITY CHECKER */
    function checkCompatibility() {
        const warnings = [];
        const { cpu, gpu, ram, motherboard, psu } = state;

        /* CPU + Motherboard socket */
        if (cpu && motherboard) {
            if (cpu.socket !== motherboard.socket) {
                warnings.push({ type: "error", message: "Socket mismatch — " + cpu.name + " uses " + cpu.socket + " but " + motherboard.name + " is " + motherboard.socket + ". These will not work together." });
            } else {
                warnings.push({ type: "good", message: "CPU and motherboard sockets match (" + cpu.socket + ")." });
            }
        }

        /* CPU + RAM type */
        if (cpu && ram) {
            if (cpu.ramType !== ram.ramType) {
                warnings.push({ type: "error", message: "RAM type mismatch — " + cpu.name + " requires " + cpu.ramType + " but " + ram.name + " is " + ram.ramType + ". This build will not POST." });
            } else {
                warnings.push({ type: "good", message: "RAM type is compatible with your CPU (" + ram.ramType + ")." });
            }
        }

        /* Motherboard + RAM type */
        if (motherboard && ram && !(cpu && ram)) {
            if (motherboard.ramType !== ram.ramType) {
                warnings.push({ type: "error", message: "RAM type mismatch — " + motherboard.name + " supports " + motherboard.ramType + " but " + ram.name + " is " + ram.ramType + "." });
            }
        }

        /* PSU wattage */
        if (psu) {
            const cpuTdp      = cpu ? cpu.tdp : 0;
            const gpuDraw     = gpu ? gpu.powerDraw : 0;
            const totalDraw   = cpuTdp + gpuDraw + 100;
            const headroom    = psu.wattage - totalDraw;

            if (cpu && gpu) {
                if (headroom < 0) {
                    warnings.push({ type: "error", message: "PSU undersized — your build needs at least " + totalDraw + "W but the " + psu.name + " only provides " + psu.wattage + "W. The system will be unstable or fail to start." });
                } else if (headroom < 100) {
                    warnings.push({ type: "warn", message: "PSU headroom is tight — only " + headroom + "W of spare capacity. Consider a higher wattage unit for stability and future upgrades." });
                } else {
                    warnings.push({ type: "good", message: "PSU wattage is sufficient — " + headroom + "W of headroom above estimated draw." });
                }
            }
        }

        /* Bottleneck check */
        if (cpu && gpu) {
            const cpuTier = TIER_ORDER[cpu.tier];
            const gpuTier = TIER_ORDER[gpu.tier];
            const diff    = gpuTier - cpuTier;

            if (diff >= 2) {
                warnings.push({ type: "error", message: "Severe CPU bottleneck — " + cpu.name + " will severely limit the " + gpu.name + ". You are paying for GPU performance you will not be able to use. Upgrade your CPU." });
            } else if (diff === 1) {
                warnings.push({ type: "warn", message: "Mild bottleneck — " + cpu.name + " may hold back the " + gpu.name + " in CPU-heavy games and tasks. Consider a higher tier CPU for a better-balanced build." });
            } else if (diff < 0) {
                warnings.push({ type: "warn", message: "GPU may be a bottleneck — " + gpu.name + " could limit the full potential of " + cpu.name + " in GPU-heavy workloads. Consider a higher tier GPU." });
            } else {
                warnings.push({ type: "good", message: "CPU and GPU are well matched — expect a balanced build with no major bottlenecks." });
            }
        }

        /* Stock cooler with high TDP CPU */
        if (state.cooling && state.cooling.name === "AMD Stock Cooler" && cpu) {
            if (cpu.tdp >= 105) {
                warnings.push({ type: "warn", message: cpu.name + " has a TDP of " + cpu.tdp + "W and runs hot under load. The stock cooler will throttle performance. An aftermarket cooler is strongly recommended." });
            }
        }

        /* All parts selected tip */
        const filled = Object.values(state).filter(Boolean).length;
        if (filled === 8) {
            warnings.push({ type: "info", message: "All 8 parts selected. Review your build score and warnings above before adding to cart." });
        }

        return warnings;
    }


    /* SCORE CALCULATOR */
    function calcScore(warnings) {
        const filled = Object.values(state).filter(Boolean).length;
        if (filled < 2) return null;

        let score = 100;

        warnings.forEach(w => {
            if (w.type === "error") score -= 35;
            if (w.type === "warn")  score -= 12;
        });

        return Math.max(0, Math.min(100, score));
    }


    /* RENDER ANALYSIS — score bar, warnings list, quick stats */
    function renderAnalysis() {
        const warnings  = checkCompatibility();
        const score     = calcScore(warnings);
        const scoreEl   = document.getElementById("jp_score-num");
        const barEl     = document.getElementById("jp_score-bar");
        const descEl    = document.getElementById("jp_score-desc");
        const listEl    = document.getElementById("jp_warnings-list");
        const countEl   = document.getElementById("jp_parts-count");

        const filled = Object.values(state).filter(Boolean).length;
        countEl.textContent = filled + " / 8";

        /* Score display */
        if (score === null) {
            scoreEl.textContent  = "—";
            scoreEl.className    = "jp_score-num";
            barEl.style.width    = "0%";
            barEl.className      = "jp_score-bar";
            descEl.textContent   = "Select at least a CPU and GPU to see your build score.";
        } else {
            scoreEl.textContent = score + "/100";
            barEl.style.width   = score + "%";

            if (score >= 80) {
                scoreEl.className = "jp_score-num jp_score-green";
                barEl.className   = "jp_score-bar jp_bar-green";
                descEl.textContent = "Great build. No major compatibility issues detected.";
            } else if (score >= 50) {
                scoreEl.className = "jp_score-num jp_score-amber";
                barEl.className   = "jp_score-bar jp_bar-amber";
                descEl.textContent = "Good start. Some issues need attention before ordering.";
            } else {
                scoreEl.className = "jp_score-num jp_score-red";
                barEl.className   = "jp_score-bar jp_bar-red";
                descEl.textContent = "Critical issues detected. This build may not function correctly.";
            }
        }

        /* Quick stats */
        const cpu = state.cpu;
        const gpu = state.gpu;
        const psu = state.psu;

        if (cpu || gpu) {
            const totalDraw = (cpu ? cpu.tdp : 0) + (gpu ? gpu.powerDraw : 0) + 100;
            document.getElementById("jp_power-draw").textContent = totalDraw + "W";

            if (psu) {
                const headroom = psu.wattage - totalDraw;
                const hrEl = document.getElementById("jp_psu-headroom");
                hrEl.textContent  = (headroom >= 0 ? "+" : "") + headroom + "W";
                hrEl.style.color  = headroom < 0 ? "#c0392b" : headroom < 100 ? "#c8a84b" : "#4caf72";
            } else {
                document.getElementById("jp_psu-headroom").textContent = "—";
            }
        } else {
            document.getElementById("jp_power-draw").textContent  = "—";
            document.getElementById("jp_psu-headroom").textContent = "—";
        }

        /* Warnings list */
        if (warnings.length === 0) {
            listEl.innerHTML = '<div class="jp_warning jp_warn-info"><i class="fa-solid fa-circle-info"></i><span>No issues detected so far.</span></div>';
            return;
        }

        const iconMap = {
            error: "fa-circle-xmark",
            warn:  "fa-triangle-exclamation",
            good:  "fa-circle-check",
            info:  "fa-circle-info",
        };

        listEl.innerHTML = warnings.map(w => `
        <div class="jp_warning jp_warn-${w.type}">
          <i class="fa-solid ${iconMap[w.type]}"></i>
          <span>${w.message}</span>
        </div>
      `).join("");
    }


    /* RENDER TOTAL */
    function renderTotal() {
        const total = Object.values(state)
            .filter(Boolean)
            .reduce((sum, p) => sum + p.price, 0);

        document.getElementById("jp_build-total").textContent = "Rs. " + total.toLocaleString("en-IN");
    }


    /* FULL RENDER — called after every state change */
    function render() {
        Object.keys(state).forEach(renderSlot);
        renderAnalysis();
        renderTotal();
    }


    /* MODAL — open, populate, select */
    const modalOverlay = document.getElementById("jp_modal-overlay");

    document.querySelectorAll(".jp_choose-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            activeSlot = btn.dataset.slot;
            openModal(activeSlot);
        });
    });

    function openModal(slot) {
        const parts = PRODUCTS[slot];
        const label = slot.charAt(0).toUpperCase() + slot.slice(1);

        document.getElementById("jp_modal-title").textContent = "Choose " + label;
        document.getElementById("jp_modal-sub").textContent   = parts.length + " options available";

        const listEl = document.getElementById("jp_modal-list");
        listEl.innerHTML = parts.map((p, i) => `
        <div class="jp_modal-option" data-index="${i}">
          <img src="${p.img}" alt="${p.name}" class="jp_modal-img" />
          <div class="jp_modal-info">
            <span class="jp_modal-name">${p.name}</span>
            <span class="jp_modal-spec">${p.spec}</span>
          </div>
          <div class="jp_modal-right">
            <span class="jp_modal-price">${formatPrice(p.price)}</span>
            <button class="jp_modal-select" data-index="${i}">${state[slot] && state[slot].name === p.name ? "Selected" : "Select"}</button>
          </div>
        </div>
      `).join("");

        modalOverlay.classList.add("jp_modal-open");
        document.body.style.overflow = "hidden";
    }

    document.getElementById("jp_modal-list").addEventListener("click", (e) => {
        const btn = e.target.closest(".jp_modal-select");
        if (!btn) return;

        const idx = parseInt(btn.dataset.index);
        state[activeSlot] = PRODUCTS[activeSlot][idx];
        closeModal();
        render();
        showToast(state[activeSlot].name + " added");
    });

    function closeModal() {
        modalOverlay.classList.remove("jp_modal-open");
        document.body.style.overflow = "";
    }

    document.getElementById("jp_modal-close").addEventListener("click", closeModal);

    modalOverlay.addEventListener("click", e => {
        if (e.target === modalOverlay) closeModal();
    });

    document.addEventListener("keydown", e => {
        if (e.key === "Escape") closeModal();
    });


    /* PRESET BUILDS */
    document.querySelectorAll(".jp_preset-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            const preset = PRESETS[btn.dataset.preset];

            Object.keys(preset).forEach(slot => {
                state[slot] = PRODUCTS[slot][preset[slot]] || null;
            });

            render();

            showToast(
                btn.dataset.preset.charAt(0).toUpperCase() +
                btn.dataset.preset.slice(1) +
                " build loaded"
            );
        });
    });


    /* RESET */
    document.getElementById("jp_reset-btn").addEventListener("click", () => {
        Object.keys(state).forEach(k => state[k] = null);
        render();
        showToast("Build reset");
    });


    /* ADD BUILD TO CART */
    document.getElementById("jp_add-build-btn").addEventListener("click", () => {

        const productIds = Object.values(state)
            .filter(Boolean)
            .map(part => part.id);

        if (productIds.length === 0) {
            showToast("Select at least one part first");
            return;
        }

        const form = document.createElement("form");

        form.method = "POST";
        form.action = "/cart/add-build";

        productIds.forEach(productId => {

            const input = document.createElement("input");

            input.type = "hidden";
            input.name = "productIds";
            input.value = productId;

            form.appendChild(input);
        });

        document.body.appendChild(form);
        form.submit();
    });


    /* TOAST */
    function showToast(msg) {
        document.getElementById("jp_toast-msg").textContent = msg;
        document.getElementById("jp_toast").classList.add("jp_toast-show");
        clearTimeout(toastTimer);

        toastTimer = setTimeout(() => {
            document.getElementById("jp_toast").classList.remove("jp_toast-show");
        }, 2500);
    }


    /* Initial render */
    render();
}