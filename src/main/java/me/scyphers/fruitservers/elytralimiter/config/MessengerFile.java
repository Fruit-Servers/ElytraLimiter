package me.scyphers.fruitservers.elytralimiter.config;

import me.scyphers.fruitservers.elytralimiter.api.Messenger;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessengerFile extends ConfigFile implements Messenger {

    private Map<String, String> messages;

    private Map<String, List<String>> listMessages;

    public static final Pattern hex = Pattern.compile("&#[a-fA-F0-9]{6}");

    // If using this as a template, feel free to customise this exact char - it was chosen at random
    private static final char interactChar = '%';

    private String prefix;

    public MessengerFile(FileManager manager) throws Exception {
        super(manager, "messages.yml");
    }

    @Override
    public void load(YamlConfiguration configuration, YamlConfiguration defaults) throws Exception {
        this.messages = new HashMap<>();
        this.listMessages = new HashMap<>();

        // Loop through every key
        for (String key : configuration.getKeys(true)) {
            if (key.equalsIgnoreCase("prefix")) continue;

            // Check if message is a single line message
            String message = configuration.getString(key, "");
            if (!message.equalsIgnoreCase("")) {
                messages.put(key, message);
                continue;
            }

            // Check if message is a multi-line message
            List<String> listMessage = configuration.getStringList(key);
            if (listMessage.size() != 0) {
                listMessages.put(key, listMessage);
                continue;
            }

            // Something that isn't a message found in config - log it to console
            getManager().getPlugin().getLogger().info("Invalid format for message found at " + key + " in messages.yml");

        }

        // Loop through the defaults to check for any values missing
        for (String key : defaults.getKeys(true)) {
            if (key.equalsIgnoreCase("prefix")) continue;

            // Check if message is a single line message
            String message = defaults.getString(key, "");
            if (!message.equalsIgnoreCase("") && !messages.containsKey(key)) {
                messages.put(key, message);
                continue;
            }

            // Check if message is a multi-line message
            List<String> listMessage = defaults.getStringList(key);
            if (listMessage.size() != 0 && !listMessages.containsKey(key)) {
                listMessages.put(key, listMessage);
            }

        }

        String rawPrefix = configuration.getString("prefix");
        if (rawPrefix != null) this.prefix = rawPrefix;
        else this.prefix = "[COULD_NOT_LOAD_PREFIX] ";

    }

    // Managing Spigots BaseComponents
    public static BaseComponent[] toComponent(String message) {
        return toComponent(message, null);
    }

    public static BaseComponent[] toComponent(String message, BaseComponent interactable) {

        ComponentBuilder builder = new ComponentBuilder();

        boolean validInteractable = interactable != null;

        StringBuilder sb = new StringBuilder();
        TextComponent component = new TextComponent();
        for (int i = 0; i < message.length(); i++) {

            char letter = message.charAt(i);

            if ( letter != '&') {
                sb.append(letter);
            } else {
                // Add text and formatting
                if (!sb.toString().equalsIgnoreCase("")) {
                    component.setText(sb.toString());
                    builder.append(component);
                    sb = new StringBuilder();
                    component = new TextComponent();
                }

                switch (message.charAt(i + 1)) {
                    case '#':
                        String hex = message.substring(i + 1, i + 8);
                        component.setColor(ChatColor.of(hex));
                        i += 7;
                        break;
                    case interactChar:
                        if (validInteractable) {
                            component.setText(sb.toString());
                            builder.append(component);
                            sb = new StringBuilder();
                            builder.append(interactable);
                        }
                        i += 1;
                        break;
                    default:
                        char colourCode = message.charAt(i + 1);
                        switch (colourCode) {
                            case 'k':
                                component.setObfuscated(true);
                                break;
                            case 'l':
                                component.setBold(true);
                                break;
                            case 'm':
                                component.setStrikethrough(true);
                                break;
                            case 'n':
                                component.setUnderlined(true);
                                break;
                            case 'o':
                                component.setItalic(true);
                                break;
                            case 'r':
                                component.setObfuscated(false);
                                component.setBold(false);
                                component.setStrikethrough(false);
                                component.setUnderlined(false);
                                component.setItalic(false);
                            default:
                                component.setColor(ChatColor.getByChar(colourCode));
                                break;
                        }
                        i += 1;
                }
            }
        }

        component.setText(sb.toString());
        builder.append(component);

        return builder.create();

    }

    // Managing formatting/replacements
    public static String markForInteractEvent(String text, String textToMark) {
        return text.replaceAll(textToMark, '&' + String.valueOf(interactChar));
    }

    public static String format(String message) {

        // Replace hex colour codes
        Matcher hexMatcher = hex.matcher(message);
        while (hexMatcher.find()) {
            String rawMatch = message.substring(hexMatcher.start(), hexMatcher.end());
            String hexCode = message.substring(hexMatcher.start() + 1, hexMatcher.end());
            message = message.replace(rawMatch, ChatColor.of(hexCode).toString());
        }

        // Translate normal colour codes and return the message
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String replace(String message, String... replacements) {
        if (replacements != null && replacements[0] != null) {

            if (replacements.length % 2 != 0) throw new IllegalArgumentException("Not all placeholders have a corresponding replacement");

            for (int i = 0; i < replacements.length; i += 2) {
                String placeholder = replacements[i];
                String replacement = replacements[i + 1];
                message = message.replaceAll(placeholder, replacement);
            }

        }
        return message;

    }

    @Override
    public void send(CommandSender sender, String message) {
        BaseComponent[] components = toComponent(message);
        this.msg(sender, components);
    }

    @Override
    public void msg(CommandSender sender, BaseComponent[] message) {
        sender.spigot().sendMessage(message);
    }

    @Override
    public void msg(Player player, ChatMessageType type, BaseComponent[] message) {
        player.spigot().sendMessage(type, null, message);
    }

    @Override
    public void msg(CommandSender sender, String path) {
        this.msg(sender, path, (String) null);
    }

    @Override
    public void msg(CommandSender sender, String path, String... replacements) {
        BaseComponent[] message = this.getMsg(path, replacements);
        if (message.length == 0) return;
        this.msg(sender, message);
    }

    @Override
    public void msgList(CommandSender sender, String path) {
        this.msgList(sender, path, (String) null);
    }

    @Override
    public void msgList(CommandSender sender, String path, String... replacements) {
        for (BaseComponent[] message : this.getListMsg(path, replacements)) {
            this.msg(sender, message);
        }
    }

    @Override
    public BaseComponent[] getMsg(String path) {
        return this.getMsg(path, (String) null);
    }

    @Override
    public BaseComponent[] getMsg(String path, String... replacements) {

        String rawMessage = messages.get(path);
        if (rawMessage == null) return messageNotFound(path);

        if (rawMessage.equals("")) return new BaseComponent[0];

        String messagePrefix = "";
        if (!rawMessage.startsWith("[NO_PREFIX]")) {
            messagePrefix = prefix;
        } else {
            rawMessage = rawMessage.substring(11);
        }

        rawMessage = replace(rawMessage, replacements);

        return toComponent(messagePrefix + rawMessage);

    }

    @Override
    public String getRawMsg(String path) {
        return this.getRawMsg(path, (String) null);
    }

    @Override
    public String getRawMsg(String path, String... replacements) {

        String rawMessage = messages.get(path);

        if (rawMessage == null) return "Could not find message at " + path;

        if (rawMessage.equalsIgnoreCase("")) return "";

        String messagePrefix = "";

        if (!rawMessage.startsWith("[NO_PREFIX]")) {
            messagePrefix = prefix + " ";
        } else {
            rawMessage = rawMessage.substring(11);
        }

        rawMessage = replace(rawMessage, replacements);

        return messagePrefix + rawMessage;

    }

    @Override
    public List<BaseComponent[]> getListMsg(String path) {
        return this.getListMsg(path, (String) null);
    }

    @Override
    public List<BaseComponent[]> getListMsg(String path, String... replacements) {

        List<String> rawList = listMessages.get(path);

        if (rawList == null) {
            getManager().getPlugin().getLogger().warning("No list message found at " + path + " in messages.yml");
            return Collections.emptyList();
        }

        List<BaseComponent[]> list = new LinkedList<>();

        if (rawList.size() == 0) return Collections.singletonList(messageNotFound(path));

        for (String item : rawList) {

            item = replace(item, replacements);

            list.add(toComponent(item));

        }

        return list;

    }

    @Override
    public List<String> getRawListMsg(String path) {
        return this.getRawListMsg(path, (String) null);
    }

    @Override
    public List<String> getRawListMsg(String path, String... replacements) {

        List<String> rawList = listMessages.get(path);

        if (rawList == null) {
            getManager().getPlugin().getLogger().warning("No list message found at " + path + " in messages.yml");
            return Collections.emptyList();
        }
        List<String> list = new LinkedList<>();

        if (rawList.size() == 0) return Collections.singletonList("Could not find message at " + path);

        for (String item : rawList) {

            item = replace(item, replacements);
            list.add(item);

        }

        return list;

    }

    // Generic message sent if a message cannot be found in messages.yml
    public static BaseComponent[] messageNotFound(String messagePath) {
        return new BaseComponent[] {
                new TextComponent("Could not find message at " + messagePath)
        };
    }

}
