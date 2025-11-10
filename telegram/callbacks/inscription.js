import axiosInstance from "../axios.js";

export default async function handleEventInscription(ctx, data) {
  const id = data.split("_")[1];

  if (!ctx.session?.api_token) {
    await ctx.answerCbQuery("❌ Debes iniciar sesión con /login")
    return;
  }

  try {
    await axiosInstance.post(`/eventos/${id}/inscriptos`, null, {
      jwt: ctx.session.api_token,
    });

    await ctx.answerCbQuery("✅ ¡Te inscribiste correctamente al evento!");
  } catch (err) {
    const status = err.response?.status;
    switch (status) {
      case 400:
        await ctx.answerCbQuery("⚠️ Ya estás inscrito o el evento no está disponible.")
        break;
      case 401:
        await ctx.answerCbQuery("🔒 Tu sesión expiró. Iniciá sesión de nuevo con /login.")
        ctx.session = null;
        break;

      case 403:
        await ctx.answerCbQuery("🚫 No podés inscribirte a tu propio evento.");
        break;
      case 404:
        await ctx.answerCbQuery("❌ Evento o usuario no encontrado.");
        break;
      case 409:
        await ctx.answerCbQuery("⚠️ Ya estás inscripto al evento o eres el organizador.");
        break;
      default:
        await ctx.answerCbQuery("❌ Ocurrió un error inesperado al inscribirte.");
        break;
    }
  }
}