package com.portfolio.allinone;


import com.portfolio.allinone.models.*;
import com.portfolio.allinone.repo.*;
import com.portfolio.allinone.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


/**
 * Класс-обработчик поступающих к боту сообщений.
 */
@Component
public class Bot extends TelegramLongPollingBot {
    @Autowired

    BirthRepo birthRepo;
    @Autowired
    TeleUserRepo userRepo;
    @Autowired
    PartyRepo partyRepo;


    static {
        ApiContextInitializer.init();
    }

    @Override
    public String getBotToken() {
        return System.getenv("TOKEN");
    }

    @Override
    public void onClosing() {

    }
    //запрос каждые 10 минут необходим, если бот лежит на heroku, пингует приложение каждые 10 минут, чтобы оно не ушло в спящий режим
    @Scheduled(fixedRate = 600000)
    private void flud() {
        URL url;
        try {
            url = new URL("https://chefox.herokuapp.com");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        //при наступлении подходящего времени делает проверку для оповещений
        switch(ChatUtils.timeFor()){
            case(1):
                if(!ChatUtils.day){
                    execute(ChatUtils.searchDays(partyRepo, birthRepo, userRepo,0));
                    ChatUtils.day=true;
                }
                break;
            case(2):
                if(!ChatUtils.tomorrow){
                    execute(ChatUtils.searchDays(partyRepo, birthRepo, userRepo,1));
                    ChatUtils.tomorrow=true;
                }
                break;
            case(3):
                if(!ChatUtils.week){
                    execute(ChatUtils.searchDays(partyRepo, birthRepo, userRepo,7));
                    ChatUtils.week=true;

                }
                break;
            case(4):
                if(!ChatUtils.month){
                    execute(ChatUtils.searchDays(partyRepo, birthRepo, userRepo,30));
                    ChatUtils.month=true;
                }
                break;
                case(5):
                if(!ChatUtils.unassigned){
                    execute(ChatUtils.searchDays(partyRepo, birthRepo, userRepo,-1));
                    ChatUtils.unassigned=true;
                }
                break;

        }
    } catch (IOException | TelegramApiException e) {
        e.printStackTrace();
    }

    }

    @Override
    //получение изменений
    public void onUpdateReceived(Update update) {


        try {
            //если получено сообщение
            if (update.hasMessage() && update.getMessage().hasText()) {
                Message inMessage = update.getMessage();
                SendMessage outMessage = new SendMessage();

                outMessage.setChatId(inMessage.getChatId());
                //назначает чат основным(через него добавляются пользователи и в него происходит рассылка), если база пользователей пуста
                if(userRepo.findAll().isEmpty()){
                    userRepo.saveAndFlush(new TeleUser(inMessage.getChatId(), 1));
                    outMessage.setText("Этот чат назначен основным");
                }
                //если основной чат назначет, но пользователя нет в базе
                else if(!userRepo.existsByTeleuser(inMessage.getChatId())){
                    outMessage.setText("я Вас не знаю");
                }
                else {
                    //обрабатывает команду если это основной чат
                    if(userRepo.findByTeleuser(inMessage.getChatId()).getTelerole()==1){

                        ChatUtils.chatCommand(partyRepo, birthRepo, userRepo, inMessage, outMessage);

                    }
                    //обрабатывает текст, если это пользователь
                    else {
                        UserUtils.mainScreen(userRepo, partyRepo, birthRepo, inMessage, outMessage);
                    //удаляет два последних сообщения, чтобы не загружать личную переписку
                        execute(new DeleteMessage().setChatId(inMessage.getChatId()).setMessageId(inMessage.getMessageId()-1));
                        execute(new DeleteMessage().setChatId(inMessage.getChatId()).setMessageId(inMessage.getMessageId()));
                    }
                }

                execute(outMessage);



            }
            //если нажата кнопка меню
            else  if(update.hasCallbackQuery()) {
                SendMessage outMessage = new SendMessage();
                Message inMessage = update.getCallbackQuery().getMessage();
                String callback = update.getCallbackQuery().getData();
                outMessage.setChatId(inMessage.getChatId());
                UserUtils.callBackExecute(userRepo, partyRepo, birthRepo, callback, outMessage);
                execute(new DeleteMessage().setChatId(inMessage.getChatId()).setMessageId(inMessage.getMessageId()));
                execute(outMessage);
            }
        }
        catch (TelegramApiException ignored){}
    }
    @Override
    public String getBotUsername() {
        return "PartyBot";
    }



}
