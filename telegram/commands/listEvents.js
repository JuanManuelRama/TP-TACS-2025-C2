import axiosInstance from "../axios.js";
import parseEvent from "../helpers/parseEvent.js";

export default async function handleListEvents(ctx) {
  const text = ctx.message?.text || "";
  const [, ...args] = text.trim().split(/\s+/);

  let page = 1;
  if (args[0] && !isNaN(parseInt(args[0], 10))) {
    page = Math.max(1, parseInt(args[0], 10));
  }

  await ctx.replyWithMarkdown(`🚀 Fetching Page **${page}**...`);

  try {
    const response = await axiosInstance.get(`/eventos?page=${page}`, {
      timeout: 5000,
    });

    const events = response.data;

    if (!events || events.length === 0) {
      let replyText = `✅ No events found`;
      if (page > 1) replyText += ` on Page ${page}.\` Try going back to a previous page.`;
      return ctx.reply(replyText);
    }

    await ctx.replyWithMarkdown(`🗓️ **Events (Page ${page})**\n`);

    for (const event of events) {
      const messageText = parseEvent(event);

      await ctx.replyWithMarkdown(messageText, {
        reply_markup: {
          inline_keyboard: [
            [
              {
                text: "📝 Inscribirme",
                callback_data: `inscribirme_${event.id}`, // ✅ fixed here
              },
            ],
          ],
        },
      });
    }

    await ctx.replyWithMarkdown(
      `**To view another page, use:** \`/eventos <page_number>\`\n(e.g., \`/eventos ${page + 1}\`)`
    );

  } catch (error) {
    console.error("Data Fetch Error:", error.message);
    let errorMsg = "❌ Failed to connect to the API server.";
    if (error.response) {
      errorMsg = `❌ Failed to fetch events (Status ${error.response.status})`;
    }
    await ctx.reply(errorMsg);
  }
}
