/* index.js */

/* Wait for header to load via components.js before running scroll logic */
window.addEventListener("jp_header_loaded", init);

function init() {

  const container  = document.getElementById("jp_scroll-container");
  const sections   = Array.from(document.querySelectorAll(".jp_section"));
  const dots       = Array.from(document.querySelectorAll(".jp_dot"));
  const header     = document.getElementById("jp_header");
  const scrollHint = document.getElementById("jp_scroll-hint");

  let currentIndex = 0;
  let isScrolling  = false;
  const LOCK_MS    = 900;

  /* Go to section */
  function goToSection(index) {
    if (index < 0 || index >= sections.length) return;

    currentIndex = index;

    const sectionHeight = sections[0].getBoundingClientRect().height;
    container.scrollTo({ top: index * sectionHeight, behavior: "smooth" });

    /* Update dots */
    dots.forEach(d => d.classList.remove("jp_dot-active"));
    if (dots[index]) dots[index].classList.add("jp_dot-active");

    /* Header — transparent on hero, dark everywhere else */
    if (index === 0) {
      header.classList.remove("jp_header-scrolled");
    } else {
      header.classList.add("jp_header-scrolled");
    }

    /* Hide scroll hint after leaving hero */
    if (index > 0 && scrollHint) {
      scrollHint.style.opacity = "0";
      scrollHint.style.pointerEvents = "none";
    }
  }

  /* Mouse wheel */
  window.addEventListener("wheel", (e) => {
    if (isScrolling) return;
    isScrolling = true;
    goToSection(e.deltaY > 0 ? currentIndex + 1 : currentIndex - 1);
    setTimeout(() => { isScrolling = false; }, LOCK_MS);
  }, { passive: true });

  /* Keyboard */
  window.addEventListener("keydown", (e) => {
    if (isScrolling) return;
    const down = ["ArrowDown", "PageDown", " "];
    const up   = ["ArrowUp", "PageUp"];
    if (down.includes(e.key)) {
      e.preventDefault();
      isScrolling = true;
      goToSection(currentIndex + 1);
      setTimeout(() => { isScrolling = false; }, LOCK_MS);
    } else if (up.includes(e.key)) {
      e.preventDefault();
      isScrolling = true;
      goToSection(currentIndex - 1);
      setTimeout(() => { isScrolling = false; }, LOCK_MS);
    }
  });

  /* Dot clicks */
  dots.forEach(dot => {
    dot.addEventListener("click", () => {
      goToSection(parseInt(dot.getAttribute("data-index"), 10));
    });
  });

  /* Touch swipe */
  let touchStartY = 0;
  container.addEventListener("touchstart", (e) => {
    touchStartY = e.touches[0].clientY;
  }, { passive: true });

  container.addEventListener("touchend", (e) => {
    if (isScrolling) return;
    const delta = touchStartY - e.changedTouches[0].clientY;
    if (Math.abs(delta) < 50) return;
    isScrolling = true;
    goToSection(delta > 0 ? currentIndex + 1 : currentIndex - 1);
    setTimeout(() => { isScrolling = false; }, LOCK_MS);
  }, { passive: true });

  /* Scroll reveal */
  const revealEls = document.querySelectorAll(
      ".jp_forge-card, .jp_standard-item, .jp_cta-inner, .jp_section-label"
  );
  const revealObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add("jp_visible");
        revealObserver.unobserve(entry.target);
      }
    });
  }, { root: container, threshold: 0.1 });

  revealEls.forEach(el => {
    el.classList.add("jp_reveal");
    revealObserver.observe(el);
  });
}