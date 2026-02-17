export interface DocumentIdentifier {
  url: string,
  mimeType: string,
}

const renderableTypes = new Set([
  "application/pdf",
  "image/png",
  "image/jpeg",
  "image/gif",
  "text/plain",
  "text/html",
  "text/css",
  "application/json",
]);

export function handleDownload(id: DocumentIdentifier) {
  setTimeout(() => {
    if (renderableTypes.has(id.mimeType)) {
      // renderable document
      window.open(id.url, '_blank', 'noopener')
    } else {
      // non-renderable document, trigger download
      const a = document.createElement("a");
      a.href = id.url
      a.download = ""
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
    }
  })
}
