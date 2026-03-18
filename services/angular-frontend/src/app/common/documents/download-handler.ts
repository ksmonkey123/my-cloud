export interface DocumentIdentifier {
  id: string,
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
  const url = '/documents/' + id.id;
  setTimeout(() => {
    if (renderableTypes.has(id.mimeType)) {
      // renderable document
      window.open(url, '_blank', 'noopener');
    } else {
      // non-renderable document, trigger download
      const a = document.createElement("a");
      a.href = url;
      a.download = "";
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
    }
  })
}
