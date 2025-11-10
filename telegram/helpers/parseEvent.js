export default function parseEvent(event) {
  const startDate = new Date(event.inicio).toLocaleString('es-AR', {
    year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
  });

  return `**Title:** ${event.titulo}
**Starts:** ${startDate}
**Price:** $${event.precio.toFixed(2)}
--------------------
`;
}