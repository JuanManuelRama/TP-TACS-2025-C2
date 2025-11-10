import handleEventInscription from "./inscription.js";
import handleEventCancel from "./cancelInscription.js";
import handleDeleteEvent from "./delete.js";

export default async function handleCallbacks(ctx) {
  const data = ctx.callbackQuery.data;

  if (data.startsWith("inscribirme_")) {
    handleEventInscription(ctx, data);
  }
  if (data.startsWith("cancel_inscription")) {
    handleEventCancel(ctx, data);
  }
  if (data.startsWith("delete_event")) {
    handleDeleteEvent(ctx, data);
  }
}