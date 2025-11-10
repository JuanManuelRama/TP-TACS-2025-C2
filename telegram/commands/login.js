import axiosInstance from "../axios.js";
import Scenes from "telegraf/scenes";

const loginStep1 = (ctx) => {
  if (ctx.session.is_logged_in) {
    ctx.reply("You're already logged in. Use /logout if you need to re-login.");
    return ctx.scene.leave();
  }

  ctx.replyWithMarkdown("🔐 **Login Step 1 of 2**\nPlease enter your API username:");
  return ctx.wizard.next();
};

const loginStep2 = async (ctx) => {
  const apiUsername = ctx.message.text ? ctx.message.text.trim() : null;

  if (!apiUsername) {
    ctx.reply("Invalid username. Please enter a valid API username to continue:");
    return;
  }

  ctx.wizard.state.apiUsername = apiUsername;
  ctx.reply("🔐 **Login Step 2 of 2**\nThank you. Now, please enter your API password:");
  return ctx.wizard.next();
};

const loginStep3 = async (ctx) => {
  const apiPassword = ctx.message.text ? ctx.message.text.trim() : null;

  if (!apiPassword) {
    ctx.reply("Invalid password. Please enter a valid API password to continue:");
    return;
  }

  const { apiUsername } = ctx.wizard.state;

  await ctx.reply("🚀 Attempting to log in...");

  try {
    const response = await axiosInstance.post("/usuarios/login", {
      username: apiUsername,
      password: apiPassword
    });

    const { token, user } = response.data;

    if (token && user) {
      ctx.session.is_logged_in = true;
      ctx.session.api_token = token;
      ctx.session.user = user;

      await ctx.reply(`Login successful! Welcome, ${user.username}.`);
    } else {
      await ctx.reply("Login failed. API did not return a valid token/user object.");
    }

  } catch (error) {
    let errorMsg = "Login failed due to a network error or server issue.";
    if (error.response && error.response.status === 401) {
      errorMsg = "❌ Login failed Please check your username and password and try again.";
    }
    else {
      console.error(error);
    }

    await ctx.reply(errorMsg);
  } finally {
    ctx.wizard.state = {};
    return ctx.scene.leave();
  }
};

// Define the Wizard Scene
const loginWizard = new Scenes.WizardScene('login-scene', loginStep1, loginStep2, loginStep3);

export default loginWizard;