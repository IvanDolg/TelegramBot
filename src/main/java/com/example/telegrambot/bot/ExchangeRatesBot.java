package com.example.telegrambot.bot;

import com.example.telegrambot.Service.ExchangeRatesService;
import com.example.telegrambot.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;

@Component
public class ExchangeRatesBot extends TelegramLongPollingBot {
    private static final Logger LOG = LoggerFactory.getLogger(ExchangeRatesBot.class);

    private static final String START = "/start";
    private static final String USD = "/usd";
    private static final String EUR = "/eur";
    private static final String HELP = "/help";

    @Autowired
    private ExchangeRatesService exchangeRatesService;
    public ExchangeRatesBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        var massage = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();

        switch (massage){
            case START -> {
                String userName = update.getMessage().getFrom().getUserName();
                startCommand(chatId, userName);
            }
            case USD -> usdCommand(chatId);
            case EUR -> eurCommand(chatId);
            case HELP -> helpCommand(chatId);
            default -> unnoundCommand(chatId);
        }
    }

    @Override
    public String getBotUsername() {
        return "ivan_dauhalaptseu_bot";
    }

    private void startCommand(Long chatId, String userName) {
        var text = """
                Добро пожаловать в бот, %s!
                
                Здесь Вы сможете узнать официальные курсы валют на сегодня, установленные ЦБ РФ.
                
                Для этого воспользуйтесь командами:
                /usd - курс доллара
                /eur - курс евро
                
                Дополнительные команды:
                /help - получение справки
                """;
        var formattedText = String.format(text, userName);
        sendMessage(chatId, formattedText);
    }

    private void usdCommand(Long chatId) {
        String formattedText;
        try {
            var usd = exchangeRatesService.getUSDExchangeRate();
            var text = "Курс доллара на %s составляет %s рублей";

            formattedText = String.format(text, LocalDateTime.now(), usd);
        } catch (ServiceException e) {
            LOG.error("Ошибка получения курса доллара", e);
            formattedText = "Не удалось получить курс доллара, попробуйте позже";
        }
        sendMessage(chatId, formattedText);
    }

    private void eurCommand(Long chatId) {
        String formattedText;
        try {
            var usd = exchangeRatesService.getEURExchangeRate();
            var text = "Курс евро на %s составляет %s рублей";

            formattedText = String.format(text, LocalDateTime.now(), usd);
        } catch (ServiceException e) {
            LOG.error("Ошибка получения курса евро", e);
            formattedText = "Не удалось получить курс евро, попробуйте позже";
        }
        sendMessage(chatId, formattedText);
    }

    private void helpCommand(Long chatId) {
        var text =  """
                Справочная информация по боту
                
                Для получения текущих курсов валют воспользуйтесь командами:
                /usd - курс доллара
                /eur - курс евро
                """;
        sendMessage(chatId, text);
    }

    private void unnoundCommand(Long chatId) {
        var text = "Неизвестная команда";
        sendMessage(chatId, text);
    }
    private void sendMessage(Long chatId, String text) {
        var catIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(catIdStr, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOG.error("Ошибка отправки сообщения", e);
        }
    }
}
