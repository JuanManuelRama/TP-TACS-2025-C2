import axiosInstance from "../axios.js";
import parseEvent from "../helpers/parseEvent.js";

export default async function handleMyEvents(ctx) {
  if (!ctx.session || !ctx.session.api_token) {
    await ctx.reply("❌ You must be logged in to view your events. Use /login to log in.");
    return;
  }

  let res;
  try {
    res = await axiosInstance.get("/usuarios/eventos", {
      headers: { Authorization: `Bearer ${ctx.session.api_token}` },
    });
  } catch (error) {
    if (error.response?.status === 401) {
      await ctx.reply("❌ Your session has expired. Please log in again using /login.");
      ctx.session = null;
    } else {
      await ctx.reply("❌ An error occurred while fetching your events. Please try again later.");
    }
    return;
  }

  const { eventosConfirmados = [], eventosEnEspera = [], eventosCreados = [] } = res.data || {};

  await ctx.replyWithMarkdown("📅 *Your Events*\n");

  await sendCategory(ctx, eventosConfirmados, "✅ Confirmed", "cancel_inscription");
  await sendCategory(ctx, eventosEnEspera, "⏳ Pending", "cancel_inscription");
  await sendCategory(ctx, eventosCreados, "🧑‍💻 Created", "delete_event");
}

async function sendCategory(ctx, events, label, action) {
  if (!events.length) {
    await ctx.replyWithMarkdown(`${label}: _none_`);
    return;
  }

  await ctx.replyWithMarkdown(`${label}:`);

  for (const e of events) {
    await ctx.replyWithMarkdown(parseEvent(e), {
      reply_markup: {
        inline_keyboard: [
          [
            {
              text: action === "delete_event" ? "🗑 Delete" : "🚫 Cancel inscription",
              callback_data: `${action}_${e.id}`,
            },
          ],
        ],
      },
    });
  }
}
