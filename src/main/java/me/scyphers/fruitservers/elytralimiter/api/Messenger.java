package me.scyphers.fruitservers.elytralimiter.api;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;


public interface Messenger {

    /**
     * Send an applicable recipient a message
     * @param sender recipient of the message
     * @param message the message to send
     */
    void send(CommandSender sender, String message);

    /**
     * Send an applicable recipient a message
     * @param sender recipient of the message
     * @param message the message to send
     */
    void msg(CommandSender sender, BaseComponent[] message);

    /**
     * Send a player a message
     * @param player recipient of the message
     * @param type where on the screen the message is sent
     * @param message the message to send
     */
    void msg(Player player, ChatMessageType type, BaseComponent[] message);

    /**
     * Send an applicable recipient a message
     * @param sender recipient of the message
     * @param path path of the message as defined in messages.yml
     */
    void msg(CommandSender sender, String path);

    /**
     * Send an applicable recipient a message
     * @param sender recipient of the message
     * @param path path of the message as defined in messages.yml
     * @param replacements replacements of the message.
     *                     Replacements are formatted as a pair of strings, with the first string being the string to replace, and the second being the new string.
     *                    <br> For example, to replace PLAYER_NAME with Steve and PLAYER_PREFIX with [VIP], the replacements would be
     *                    <code>"PLAYER_NAME", "Steve", "PLAYER_PREFIX", "[VIP]"</code>
     */
    void msg(CommandSender sender, String path, String... replacements);


    /**
     * Send an applicable recipient a list of messages
     * @param sender recipient of the message
     * @param path path of the message as defined in messages.yml
     */
    void msgList(CommandSender sender, String path);

    /**
     * Send an applicable recipient a list of messages
     * @param sender recipient of the message
     * @param path path of the message as defined in messages.yml
     * @param replacements replacements of the message.
     *                     Replacements are formatted as a pair of strings, with the first string being the string to replace, and the second being the new string.
     *                    <br> For example, to replace PLAYER_NAME with Steve and PLAYER_PREFIX with [VIP], the replacements would be
     *                    <code>"PLAYER_NAME", "Steve", "PLAYER_PREFIX", "[VIP]"</code>
     */
    void msgList(CommandSender sender, String path, String... replacements);

    /**
     * Get a message from messages.yml
     * @param path path of the message as defined in messages.yml
     * @return The message in a form to suit spigots messaging system
     */
    BaseComponent[] getMsg(String path);

    /**
     * Get a message from messages.yml
     * @param path path of the message as defined in messages.yml
     * @param replacements replacements of the message.
     *                     Replacements are formatted as a pair of strings, with the first string being the string to replace, and the second being the new string.
     *                    <br> For example, to replace PLAYER_NAME with Steve and PLAYER_PREFIX with [VIP], the replacements would be
     *                    <code>"PLAYER_NAME", "Steve", "PLAYER_PREFIX", "[VIP]"</code>
     * @return The message in a form to suit spigots messaging system, with all replacements resolved
     */
    BaseComponent[] getMsg(String path, String... replacements);

    /**
     * Get a message from messages.yml
     * @param path path of the message as defined in messages.yml
     * @return The unformatted message from messages.yml
     */
    String getRawMsg(String path);

    /**
     * Get a message from messages.yml
     * @param path path of the message as defined in messages.yml
     * @param replacements replacements of the message.
     *                     Replacements are formatted as a pair of strings, with the first string being the string to replace, and the second being the new string.
     *                    <br> For example, to replace PLAYER_NAME with Steve and PLAYER_PREFIX with [VIP], the replacements would be
     *                    <code>"PLAYER_NAME", "Steve", "PLAYER_PREFIX", "[VIP]"</code>
     * @return the unformatted message from messages.yml, with with all replacements resolved
     */
    String getRawMsg(String path, String... replacements);

    /**
     * Get a list of messages from messages.yml
     * @param path path of the message as defined in messages.yml
     * @return the list of messages from messages.yml, in a form to suit spigots messaging system
     */
    List<BaseComponent[]> getListMsg(String path);

    /**
     * Get a list of messages from messages.yml
     * @param path path of the message as defined in messages.yml
     * @param replacements replacements of the message.
     *                     Replacements are formatted as a pair of strings, with the first string being the string to replace, and the second being the new string.
     *                    <br> For example, to replace PLAYER_NAME with Steve and PLAYER_PREFIX with [VIP], the replacements would be
     *                    <code>"PLAYER_NAME", "Steve", "PLAYER_PREFIX", "[VIP]"</code>
     * @return the list of messages from messages.yml, in a form to suit spigots messaging system, with all replacements resolved
     */
    List<BaseComponent[]> getListMsg(String path, String... replacements);

    /**
     * Get a list of messages from messages.yml
     * @param path path of the message as defined in messages.yml
     * @return the unformatted list of messages from messages.yml
     */
    List<String> getRawListMsg(String path);

    /**
     * Get a list of messages from messages.yml
     * @param path path of the message as defined in messages.yml
     * @param replacements replacements of the message.
     *                     Replacements are formatted as a pair of strings, with the first string being the string to replace, and the second being the new string.
     *                    <br> For example, to replace PLAYER_NAME with Steve and PLAYER_PREFIX with [VIP], the replacements would be
     *                    <code>"PLAYER_NAME", "Steve", "PLAYER_PREFIX", "[VIP]"</code>
     * @return the unformatted list of messages.yml, with all replacements resolved
     */
    List<String> getRawListMsg(String path, String... replacements);

}
