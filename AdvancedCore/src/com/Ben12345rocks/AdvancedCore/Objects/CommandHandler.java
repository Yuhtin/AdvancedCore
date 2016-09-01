package com.Ben12345rocks.AdvancedCore.Objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Ben12345rocks.AdvancedCore.Main;
import com.Ben12345rocks.AdvancedCore.Utils;
import com.Ben12345rocks.AdvancedCore.Configs.Config;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandHandler.
 */
public abstract class CommandHandler {

	/** The plugin. */
	static Main plugin = Main.plugin;

	/** The args. */
	private String[] args;

	/** The perm. */
	private String perm;

	/** The help message. */
	private String helpMessage;

	/** The tab complete options. */
	public HashMap<String, ArrayList<String>> tabCompleteOptions;

	/**
	 * Instantiates a new command handler.
	 *
	 * @param args
	 *            the args
	 * @param perm
	 *            the perm
	 */
	public CommandHandler(String[] args, String perm) {
		this.args = args;
		this.perm = perm;
		helpMessage = "Unknown Help Message";
		tabCompleteOptions = new HashMap<String, ArrayList<String>>();
		loadTabComplete();
	}

	/**
	 * Instantiates a new command handler.
	 *
	 * @param args
	 *            the args
	 * @param perm
	 *            the perm
	 * @param helpMessage
	 *            the help message
	 */
	public CommandHandler(String[] args, String perm, String helpMessage) {
		this.args = args;
		this.perm = perm;
		this.helpMessage = helpMessage;
		tabCompleteOptions = new HashMap<String, ArrayList<String>>();
		loadTabComplete();
	}

	/**
	 * Args match.
	 *
	 * @param arg
	 *            the arg
	 * @param i
	 *            the i
	 * @return true, if successful
	 */
	public boolean argsMatch(String arg, int i) {
		if (i < args.length) {
			String[] cmdArgs = args[i].split("&");
			for (String cmdArg : cmdArgs) {
				if (tabCompleteOptions.keySet().contains(cmdArg)
						|| cmdArg.equalsIgnoreCase("(SITENAME)")
						|| cmdArg.equalsIgnoreCase("(reward)")
						|| cmdArg.equalsIgnoreCase("(entity)")
						|| cmdArg.equalsIgnoreCase("(entitydamagecause)")
						|| cmdArg.equalsIgnoreCase("(entityspawnreason)")
						|| cmdArg.equalsIgnoreCase("(RequestMethod)")
						|| cmdArg.equalsIgnoreCase("(number)")
						|| cmdArg.equalsIgnoreCase("(string)")
						|| cmdArg.equalsIgnoreCase("(boolean)")
						|| cmdArg.equalsIgnoreCase("(list)")
						|| arg.equalsIgnoreCase(cmdArg)) {
					return true;
				}
			}
			return false;
		}
		return false;
	}

	/**
	 * Load tab complete.
	 */
	public void loadTabComplete() {
		ArrayList<String> players = new ArrayList<String>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			players.add(player.getName());
		}
		addTabCompleteOption("(Player)", players);
		ArrayList<String> options = new ArrayList<String>();
		options.add("True");
		options.add("False");
		addTabCompleteOption("(boolean)", options);
		options = new ArrayList<String>();
		addTabCompleteOption("(list)", options);
		addTabCompleteOption("(String)", options);
		addTabCompleteOption("(number)", options);
	}

	/**
	 * Update tab complete.
	 */
	public void updateTabComplete() {
		ArrayList<String> players = new ArrayList<String>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			players.add(player.getName());
		}
		addTabCompleteOption("(Player)", players);
	}

	/**
	 * Adds the tab complete option.
	 *
	 * @param toReplace
	 *            the to replace
	 * @param options
	 *            the options
	 */
	public void addTabCompleteOption(String toReplace, ArrayList<String> options) {
		ArrayList<String> replace = new ArrayList<String>();
		for (String str : options) {
			replace.add(str);
		}
		tabCompleteOptions.put(toReplace, replace);
	}

	/**
	 * Gets the tab complete options.
	 *
	 * @param sender
	 *            the sender
	 * @param args
	 *            the args
	 * @param argNum
	 *            the arg num
	 * @return the tab complete options
	 */
	public ArrayList<String> getTabCompleteOptions(CommandSender sender,
			String[] args, int argNum) {
		CommandHandler commandHandler = this;
		updateTabComplete();
		ArrayList<String> cmds = new ArrayList<String>();

		if (sender.hasPermission(commandHandler.getPerm())) {
			String[] cmdArgs = commandHandler.getArgs();
			if (cmdArgs.length > argNum) {
				boolean argsMatch = true;
				for (int i = 0; i < argNum; i++) {
					if (args.length >= i) {
						if (!commandHandler.argsMatch(args[i], i)) {
							argsMatch = false;
						}
					}
				}

				if (argsMatch) {
					String[] cmdArgsList = cmdArgs[argNum].split("&");
					for (String arg : cmdArgsList) {
						boolean add = true;
						for (Entry<String, ArrayList<String>> entry : tabCompleteOptions
								.entrySet()) {
							if (arg.equalsIgnoreCase(entry.getKey())) {
								add = false;
								for (String str : entry.getValue()) {
									if (!cmds.contains(str)) {
										cmds.add(str);
									}
								}
							}
						}
						if (!cmds.contains(arg) && add) {
							cmds.add(arg);
						}
					}

				}

			}
		}

		Collections.sort(cmds, String.CASE_INSENSITIVE_ORDER);

		return cmds;
	}

	/**
	 * Execute.
	 *
	 * @param sender
	 *            the sender
	 * @param args
	 *            the args
	 */
	public abstract void execute(CommandSender sender, String[] args);

	/**
	 * Gets the admin tab complete options.
	 *
	 * @param sender
	 *            the sender
	 * @param args
	 *            the args
	 * @param argNum
	 *            the arg num
	 * @return the admin tab complete options
	 */
	public ArrayList<String> getAdminTabCompleteOptions(CommandSender sender,
			String[] args, int argNum) {
		ArrayList<String> cmds = new ArrayList<String>();
		CommandHandler commandHandler = this;
		if (sender.hasPermission(commandHandler.getPerm())) {
			String[] cmdArgs = commandHandler.getArgs();
			if (cmdArgs.length > argNum) {
				boolean argsMatch = true;
				for (int i = 0; i < argNum; i++) {
					if (args.length >= i) {
						if (!commandHandler.argsMatch(args[i], i)) {
							argsMatch = false;
						}
					}
				}

				if (argsMatch) {
					String[] cmdArgsList = cmdArgs[argNum].split("&");
					for (String arg : cmdArgsList) {
						for (Entry<String, ArrayList<String>> entry : tabCompleteOptions
								.entrySet()) {
							if (arg.equalsIgnoreCase(entry.getKey())) {
								for (String str : entry.getValue()) {
									if (!cmds.contains(str)) {
										cmds.add(str);
									}
								}
							}
						}

					}
				}

			}
		}

		Collections.sort(cmds, String.CASE_INSENSITIVE_ORDER);

		return cmds;
	}

	/**
	 * Gets the args.
	 *
	 * @return the args
	 */
	public String[] getArgs() {
		return args;
	}

	/**
	 * Gets the help line.
	 *
	 * @param command
	 *            the command
	 * @return the help line
	 */
	public TextComponent getHelpLine(String command) {
		String line = Config.getInstance().getFormatHelpLine();

		String commandText = getHelpLineCommand(command);
		line = line.replace("%Command%", commandText);
		if (getHelpMessage() != "") {
			line = line.replace("%HelpMessage%", getHelpMessage());
		}
		TextComponent txt = Utils.getInstance().stringToComp(line);
		txt.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
				commandText));
		txt.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder(getHelpMessage()).color(ChatColor.AQUA)
						.create()));
		return txt;

	}

	/**
	 * Gets the help line command.
	 *
	 * @param command
	 *            the command
	 * @return the help line command
	 */
	public String getHelpLineCommand(String command) {
		String commandText = command;
		for (String arg1 : args) {
			int count = 1;
			for (String arg : arg1.split("&")) {
				if (count == 1) {
					if (arg.equalsIgnoreCase("(player)")) {
						commandText += " (Player)";
					} else if (arg.equalsIgnoreCase("(sitename)")) {
						commandText += " (SiteName)";
					} else if (arg.equalsIgnoreCase("(reward)")) {
						commandText += " (Reward)";
					} else if (arg.equalsIgnoreCase("(boolean)")) {
						commandText += " (True/False)";
					} else if (arg.equalsIgnoreCase("(number)")) {
						commandText += " (Number)";
					} else if (arg.equalsIgnoreCase("(string)")) {
						commandText += " (Text)";
					} else {
						commandText += " " + arg;
					}
				} else {
					if (arg.equalsIgnoreCase("(player)")) {
						commandText += "/(Player)";
					} else if (arg.equalsIgnoreCase("(sitename)")) {
						commandText += "/(SiteName)";
					} else if (arg.equalsIgnoreCase("(reward)")) {
						commandText += "/(Reward)";
					} else if (arg.equalsIgnoreCase("(boolean)")) {
						commandText += "/(True/False)";
					} else if (arg.equalsIgnoreCase("(number)")) {
						commandText += "/(Number)";
					} else if (arg.equalsIgnoreCase("(string)")) {
						commandText += "/(Text)";
					} else {
						commandText += "/" + arg;
					}
				}
				count++;
			}
		}
		return commandText;
	}

	/**
	 * Gets the help message.
	 *
	 * @return the help message
	 */
	public String getHelpMessage() {
		return helpMessage;
	}

	/**
	 * Gets the perm.
	 *
	 * @return the perm
	 */
	public String getPerm() {
		return perm;
	}

	/**
	 * Run command.
	 *
	 * @param sender
	 *            the sender
	 * @param args
	 *            the args
	 * @return true, if successful
	 */
	public boolean runCommand(CommandSender sender, String[] args) {
		if (args.length >= this.args.length) {
			for (int i = 0; i < args.length; i++) {
				if (!argsMatch(args[i], i)) {
					return false;
				}
				if (this.args[i].equalsIgnoreCase("(number)")) {
					if (!Utils.getInstance().isInt(args[i])) {
						sender.sendMessage(Utils.getInstance().colorize(
								Config.getInstance().getFormatNotNumber()
										.replace("%arg%", args[i])));
						return true;
					}
				}
			}

			if (perm != "") {
				if (!sender.hasPermission(perm)) {
					sender.sendMessage(Utils.getInstance().colorize(
							Config.getInstance().getFormatNoPerms()));
					plugin.debug(sender.getName()
							+ " was denied access to command");
					return true;
				}
			}

			execute(sender, args);
			return true;
		}
		return false;
	}

	/**
	 * Sets the help message.
	 *
	 * @param helpMessage
	 *            the new help message
	 */
	public void setHelpMessage(String helpMessage) {
		this.helpMessage = helpMessage;
	}

	/**
	 * Sets the perm.
	 *
	 * @param perm
	 *            the new perm
	 */
	public void setPerm(String perm) {
		this.perm = perm;
	}

}
