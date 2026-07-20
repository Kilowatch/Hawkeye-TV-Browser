(function() {
    try {
        var title = document.title || "";
        var h1 = document.querySelector("h1");
        if (h1 && h1.innerText) {
            title = h1.innerText.trim();
        }

        var byline = "";
        var authorEl = document.querySelector("[rel='author'], .author, .byline, .by-line");
        if (authorEl && authorEl.innerText) {
            byline = authorEl.innerText.trim();
        }

        var article = document.querySelector("article, main, .article, .post-content, #content, .content");
        var contentHtml = "";
        if (article) {
            contentHtml = article.innerHTML;
        } else {
            var paragraphs = Array.from(document.querySelectorAll("p")).map(function(p) { return p.outerHTML; }).join("");
            contentHtml = paragraphs || document.body.innerHTML;
        }

        return JSON.stringify({
            title: title,
            byline: byline,
            content: contentHtml,
            length: contentHtml.length
        });
    } catch(e) {
        return JSON.stringify({ error: e.toString() });
    }
})();
