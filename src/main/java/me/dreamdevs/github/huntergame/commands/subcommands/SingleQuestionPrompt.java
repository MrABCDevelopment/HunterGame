package me.dreamdevs.github.huntergame.commands.subcommands;

import me.dreamdevs.github.huntergame.HunterGameMain;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;

public final class SingleQuestionPrompt extends ValidatingPrompt
{
    private static ConversationFactory factory;

    private final String question;
    private final AcceptAnswer listener;

    public static void newPrompt(final Player player, final String question, final AcceptAnswer listener)
    {
        if(factory == null)
            factory = new ConversationFactory(HunterGameMain.getInstance());
        if(!player.isConversing())
        {
            Conversation conv = factory.withModality(false).withFirstPrompt(new SingleQuestionPrompt(question,listener)).withLocalEcho(true).buildConversation(player);
            conv.begin();
        }
    }

    private SingleQuestionPrompt(final String question, final AcceptAnswer listener)
    {
        this.question = question;
        this.listener = listener;
    }

    @Override
    public String getPromptText(ConversationContext context)
    {
        return question;
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext context, String input)
    {
        if(input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("stop") || input.equalsIgnoreCase("end"))
            return Prompt.END_OF_CONVERSATION;

        if(listener.onAnswer(input))
            return Prompt.END_OF_CONVERSATION;
        else
            return this;
    }

    @Override
    protected boolean isInputValid(ConversationContext context, String input)
    {
        return true;
    }
}

interface AcceptAnswer
{
    boolean onAnswer(String input);
}