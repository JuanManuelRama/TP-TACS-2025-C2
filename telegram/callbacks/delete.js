import axiosInstance from "../axios.js";

export default async function handleDeleteEvent(ctx, data) {
  const id = data.split("_")[2];

  if (!ctx.session?.api_token) {
    await ctx.answerCbQuery("❌ You must be logged in with /login", { show_alert: true });
    return;
  }

  try {
    await axiosInstance.delete(`/eventos/${id}`, {
      headers: { Authorization: `Bearer ${ctx.session.api_token}` },
    });
    await ctx.answerCbQuery("🗑 Event deleted successfully!");
  } catch (err) {
    console.error(err);
    switch (err.response?.status) {
      case 401:
        await ctx.answerCbQuery("❌ Session expired. Please /login again.", { show_alert: true });
        ctx.session = null;
        break;
      case 403:
        await ctx.answerCbQuery("⚠️ You cannot delete this event.", { show_alert: true });
        break;
      case 404:
        await ctx.answerCbQuery("⚠️ Event not found.", { show_alert: true });
        break;
      default:
        await ctx.answerCbQuery("❌ Failed to delete event.", { show_alert: true });
    }
  }
}