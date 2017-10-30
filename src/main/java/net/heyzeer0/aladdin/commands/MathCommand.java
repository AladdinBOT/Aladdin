package net.heyzeer0.aladdin.commands;

import com.udojava.evalex.Expression;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;

import java.math.BigDecimal;

/**
 * Created by HeyZeer0 on 22/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class MathCommand implements CommandExecutor {

    @Command(command = "mat", description = "Realize operações matemáticas.", aliasses = {"math", "matematica"}, parameters = {"operação"}, type = CommandType.MISCELLANEOUS,
            usage = "a!mat 10+10")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {
        String conta = args.getComplete().replace(" ", "");
        BigDecimal answer;

        try{
            answer = new Expression(conta).eval();
        }catch (Exception ex) {
            e.sendMessage(EmojiList.WORRIED + " Oops, a expressão metemática inserida é invalida");
            return new CommandResult((CommandResultEnum.SUCCESS));
        }

        e.sendMessage(EmojiList.CORRECT + " **Resposta**: `" + answer.doubleValue() + "`");
        return new CommandResult((CommandResultEnum.SUCCESS));
    }
}
