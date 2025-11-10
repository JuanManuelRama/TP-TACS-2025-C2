export default async function handleLogout(ctx) {
  ctx.session = null;
  await ctx.reply("✅ Sesión cerrada correctamente.");
}
