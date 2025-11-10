import axiosInstance from "../axios.js";

export default async function handleEventCancel(ctx, data) {
  const id = data.split("_")[2];

  if (!ctx.session?.api_token) {
    await ctx.answerCbQuery("❌ You must be logged in with /login", { show_alert: true });
    return;
  }

  try {
    await axiosInstance.delete(`/eventos/${id}/inscriptos`, {
      headers: { Authorization: `Bearer ${ctx.session.api_token}` },
    });
    await ctx.answerCbQuery("✅ Inscription cancelled successfully!");
  } catch (err) {
    console.error(err);
    switch (err.response?.status) {
      case 401:
        await ctx.answerCbQuery("❌ Session expired. Please /login again.", { show_alert: true });
        ctx.session = null;
        break;
      case 404:
        await ctx.answerCbQuery("⚠️ Event not found or not enrolled.", { show_alert: true });
        break;
      default:
        await ctx.answerCbQuery("❌ Failed to cancel inscription.", { show_alert: true });
    }
  }
}