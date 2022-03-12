package ru.mrbrikster.chatty.commands;

import ru.mrbrikster.baseplugin.config.Configuration;
import ru.mrbrikster.chatty.Chatty;
import ru.mrbrikster.chatty.chat.ChatManager;
import ru.mrbrikster.chatty.chat.JsonStorage;
import ru.mrbrikster.chatty.dependencies.DependencyManager;
import ru.mrbrikster.chatty.moderation.ModerationManager;

public class CommandManager {

    private final Configuration configuration;
    private final ChatManager chatManager;
    private final DependencyManager dependencyManager;
    private final JsonStorage jsonStorage;
    private final ModerationManager moderationManager;

    private ChattyCommand chattyCommand;
    private ClearChatCommand clearChatCommand;
    private SpyCommand spyCommand;
    private SwearsCommand swearsCommand;
    private ChatCommand chatCommand;
    private PrefixCommand prefixCommand;
    private SuffixCommand suffixCommand;

    public CommandManager(Chatty chatty) {
        this.configuration = chatty.getExact(Configuration.class);
        this.chatManager = chatty.getExact(ChatManager.class);
        this.dependencyManager = chatty.getExact(DependencyManager.class);
        this.jsonStorage = chatty.getExact(JsonStorage.class);
        this.moderationManager = chatty.getExact(ModerationManager.class);

        this.init();

        configuration.onReload(config -> {
            this.unregisterAll();
            this.init();
        });
    }

    private void init() {
        this.chattyCommand = new ChattyCommand(configuration);
        this.chattyCommand.register(Chatty.instance());

        if (configuration.getNode("miscellaneous.commands.clearchat.enable").getAsBoolean(false)) {
            this.clearChatCommand = new ClearChatCommand(configuration);
            this.clearChatCommand.register(Chatty.instance());
        }

        if (configuration.getNode("spy.enable").getAsBoolean(false)) {
            this.spyCommand = new SpyCommand(jsonStorage);
            this.spyCommand.register(Chatty.instance());
        }

        if (configuration.getNode("moderation.swear.enable").getAsBoolean(false)) {
            this.swearsCommand = new SwearsCommand();
            this.swearsCommand.register(Chatty.instance());
        }

        if (configuration.getNode("miscellaneous.commands.chat.enable").getAsBoolean(false)) {
            this.chatCommand = new ChatCommand(configuration, chatManager, jsonStorage);
            this.chatCommand.register(Chatty.instance());
        }

        if (configuration.getNode("miscellaneous.commands.prefix.enable").getAsBoolean(false)) {
            this.prefixCommand = new PrefixCommand(configuration, dependencyManager, jsonStorage);
            this.prefixCommand.register(Chatty.instance());
        }

        if (configuration.getNode("miscellaneous.commands.suffix.enable").getAsBoolean(false)) {
            this.suffixCommand = new SuffixCommand(configuration, dependencyManager, jsonStorage);
            this.suffixCommand.register(Chatty.instance());
        }
    }

    public void unregisterAll() {
        this.chattyCommand.unregister(Chatty.instance());

        if (clearChatCommand != null) {
            this.clearChatCommand.unregister(Chatty.instance());
        }

        if (spyCommand != null) {
            this.spyCommand.unregister(Chatty.instance());
        }

        if (swearsCommand != null) {
            this.swearsCommand.unregister(Chatty.instance());
        }

        if (chatCommand != null) {
            this.chatCommand.unregister(Chatty.instance());
        }

        if (prefixCommand != null) {
            this.prefixCommand.unregister(Chatty.instance());
        }

        if (suffixCommand != null) {
            this.suffixCommand.unregister(Chatty.instance());
        }
    }

}
