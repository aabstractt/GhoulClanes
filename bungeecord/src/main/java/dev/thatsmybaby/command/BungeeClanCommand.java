package dev.thatsmybaby.command;

import dev.thatsmybaby.BungeeClansLoader;
import dev.thatsmybaby.command.arguments.CreateArgument;
import dev.thatsmybaby.command.arguments.InviteArgument;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class BungeeClanCommand extends Command {

    private final Set<Argument> arguments = new HashSet<>();

    public BungeeClanCommand(String name) {
        super(name);

        addArguments(
                new CreateArgument("create", null, null, true),
                new InviteArgument("invite", null, null, true)
        );
    }

    private void addArguments(Argument... arguments) {
        this.arguments.addAll(Arrays.asList(arguments));
    }

    private Argument getArgument(String name) {
        return this.arguments.stream().filter(argument -> argument.equals(name)).findFirst().orElse(null);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("Run this command in-game").color(ChatColor.RED).create());

            return;
        }

        if (args.length == 0) {
            this.showHelpMessage((ProxiedPlayer) sender);

            return;
        }

        Argument argument = this.getArgument(args[0]);

        if (argument == null) {
            this.showHelpMessage((ProxiedPlayer) sender);

            return;
        }

        if (argument.getPermission() != null && !sender.hasPermission(argument.getPermission())) {
            sender.sendMessage(new ComponentBuilder("You don't have permissions to use this command.").color(ChatColor.RED).create());

            return;
        }

        String[] finalArgs = Arrays.copyOfRange(args, 1, args.length);

        if (argument.isAsync()) {
            ProxyServer.getInstance().getScheduler().runAsync(BungeeClansLoader.getInstance(), () -> {
                try {
                    argument.execute((ProxiedPlayer) sender, this.getName(), args[0], finalArgs);
                } catch (Exception e) {
                    sender.sendMessage(new ComponentBuilder("An error occurred").color(ChatColor.RED).create());
                }
            });

            return;
        }

        try {
            argument.execute((ProxiedPlayer) sender, this.getName(), args[0], finalArgs);
        } catch (Exception e) {
            sender.sendMessage(new ComponentBuilder("An error occurred").color(ChatColor.RED).create());
        }
    }

    private void showHelpMessage(ProxiedPlayer proxiedPlayer) {

    }
}