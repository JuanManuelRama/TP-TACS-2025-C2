import dotenv from "dotenv";

import {session, Telegraf} from "telegraf"

import loginWizard from "./commands/login.js";
import Scenes from "telegraf/scenes";
import handleLogout from "./commands/logout.js";
import handleListEvents from "./commands/listEvents.js";
import handleHelp from "./commands/help.js";
import {message} from "telegraf/filters";
import handleMyEvents from "./commands/myEvents.js";
import handleCallbacks from "./callbacks/callback.js";

dotenv.config();
const bot = new Telegraf(process.env.BOT_TOKEN);

const stage = new Scenes.Stage([loginWizard], {
  ttl: 10 * 60 // 10 minutes
});

bot.use(session());
bot.use(stage.middleware());


bot.command('login', (ctx) => ctx.scene.enter('login-scene'));
bot.command("logout", handleLogout);
bot.command("eventos", handleListEvents)
bot.command("help", handleHelp);
bot.command("myEvents", handleMyEvents);
bot.command("me", async (ctx) => {
  const user = ctx.session?.user;
  if (user) {
    ctx.replyWithMarkdown(`👤 You are logged in as: ${user.username}`);
  }
  else {
    ctx.reply("You are not logged in. Use /login to log in.");
  }
});

bot.on(message("text"), async (ctx) => {
  const text = ctx.message.text.toLowerCase();
  if (text.startsWith("/")) return;
  await ctx.reply("I didn't get that 🤔. Send /help to see available commands");
});

bot.on("callback_query", handleCallbacks);

bot.start((ctx) => ctx.reply("Welcome! Use /help to see available commands"));

await bot.launch();

process.once("SIGINT", () => {bot.stop()})
process.once("SIGTERM", () => {bot.stop()})
process.once("SIGKILL", () => {bot.stop()})