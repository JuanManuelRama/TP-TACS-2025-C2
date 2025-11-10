export default async function handleHelp(ctx) {
  await ctx.replyWithMarkdown(`
🤖 *Available Commands*:
/login 
/logout
/eventos [page] — List events
/myEvents — View events you are registered for or have created
/me — View your profile info
/help — Show this help message`);
}