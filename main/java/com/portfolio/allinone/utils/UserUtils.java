package com.portfolio.allinone.utils;

import com.portfolio.allinone.models.BirthDay;
import com.portfolio.allinone.models.Party;
import com.portfolio.allinone.repo.BirthRepo;
import com.portfolio.allinone.repo.PartyRepo;
import com.portfolio.allinone.repo.TeleUserRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

//класс личных сообщений с пользователем
public class UserUtils {

    private static InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
    private static HashMap<Long, Integer> userStates = new HashMap<>();
    private static HashMap<Long, String> eParty = new HashMap<>();

    private static List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
    private static List<List<InlineKeyboardButton>> back = new ArrayList<>();

    //клавиатура по умолчанию
    static{
        back.add(ConvertUtils.makeButton("Отмена","goBack"));
    }

    //поведение при отправке текста
    public static void mainScreen(TeleUserRepo user, PartyRepo party, BirthRepo birth,  Message in, SendMessage out) {
        keyboard.setKeyboard(back);
        //сообщение приветствия
        if (in==null || !userStates.containsKey(in.getChatId())) {

            buttons.clear();
            buttons.add(ConvertUtils.makeButton("События","lookForPartyes"));
            buttons.add(ConvertUtils.makeButton("Дни рождения","lookForDays"));

            keyboard.setKeyboard(buttons);
            out.setText(in == null ? "Привет! Я могу показывать и записывать события и дни рождения" : "Чтобы ввести данные - выберите один из пунктов").setReplyMarkup(keyboard);

        }

        //проверяет ввод даты дня рождения
        else if (userStates.get(in.getChatId())==1){
            if(addDay(birth, in, out)) {
                userStates.remove(in.getChatId());
                birthDays(birth, out);
            }
            else out.setText("Что-то не так - проверьте формат:\n'Имя дд мм'\n(Иван 25 05)").setReplyMarkup(keyboard);
        }

        //проверяет ввод даты при добавлении события(в данный момент не выводится, ошибочная дата обрабатывается, как часть названия)
        else if (userStates.get(in.getChatId())==2){
            if(addParty(party, in, out, null)) {
                userStates.remove(in.getChatId());
                partyes(party, out);
            }
            else out.setText("Что-то не так - проверьте формат:\n'Событий дд мм' \n(Пикник 25 05)").setReplyMarkup(keyboard);
        }
        //проверяет ввод даты на событии при редактировании

        else if (userStates.get(in.getChatId())==3){
            if(addParty(party, in, out, eParty.get(in.getChatId()))) {
                userStates.remove(in.getChatId());
                eParty.remove(in.getChatId());
                partyes(party, out);
            }

            else out.setText("Что-то не так - проверьте формат:\n'дд мм'\n(25 05)").setReplyMarkup(keyboard);
        }

    }

//    поведение при нажатии кнопки меню
    public static void callBackExecute (TeleUserRepo user, PartyRepo party, BirthRepo birth, String in, SendMessage out){
        keyboard.setKeyboard(back);
        if(in.equals("lookForPartyes")){
            partyes(party, out);
        }
        else if(in.contains("goBack")) {
            userStates.remove(Long.parseLong(out.getChatId()));

            mainScreen(user, null, null, null, out);
        }
        else if(in.equals("lookForDays")) {
            birthDays(birth, out);
        }
        else if(in.equals("addDay")) {
            out.setText("Введите день рождения в формате:\n'Имя дд мм'\n(Иван 25 05)").setReplyMarkup(keyboard);
            userStates.put(Long.parseLong(out.getChatId()), 1);
        }
        else if(in.equals("addParty")) {
            out.setText("Введите событие в формате:\n'Название дд мм' или только название\n('Пикник 25 05' или 'Пикник')\n Старайтесь не использовать цифры на конце, а то я запишу их, как дату!").setReplyMarkup(keyboard);
            userStates.put(Long.parseLong(out.getChatId()), 2);
        }

        else if(in.contains("editPartyDate")) {
            out.setText("Введите дату в формате:\n'дд мм'\n(25 05)").setReplyMarkup(keyboard);
            userStates.put(Long.parseLong(out.getChatId()), 3);
            eParty.put(Long.parseLong(out.getChatId()), in.substring(13));
        }
        else if(in.contains("editPartyMenu")){
            editParty(party, out);

        }

        else if(in.contains("removeDay")) {
            birth.deleteByName(in.substring(9));
            birthDays(birth, out);
        }

        else if(in.contains("removePar")) {
            party.deleteByName(in.substring(9));
            partyes(party, out);
        }

        else if(in.contains("delDay")){
            if(in.contains("P")) delDay(party, out);
            else delDay(birth, out);
        }
		else mainScreen(user, null, null, null, out);


    }
//подменю дней рождения
    private static void birthDays(BirthRepo birth, SendMessage out){
        buttons.clear();

        buttons.add(ConvertUtils.makeButton("Добавить день рождения","addDay"));
        buttons.add(ConvertUtils.makeButton("Удалить", "delDay"));
        buttons.add(ConvertUtils.makeButton("Назад","goBack"));

        keyboard.setKeyboard(buttons);

        out.setText("====================\nСписок дней рождения:\n====================\n"+ConvertUtils.getBirthDays(birth.findAll())).setReplyMarkup(keyboard);

    }
    //Вывод полного списка для копирования
    private static void partyes(BirthRepo birth, PartyRepo party, SendMessage out){
        String backup="";
        backup+="список всех внесённых мероприятий\nДни рождения:\n"+allbackup.getAllDates(birth)+"\nМеропиятия\n"+allbackup.getAllDates(party);
        out.setText(backup).setReplyMarkup(keyboard);
    }

//подменю событий
    private static void partyes(PartyRepo party, SendMessage out){
        buttons.clear();

        buttons.add(ConvertUtils.makeButton("Добавить","addParty"));
        buttons.add(ConvertUtils.makeButton("Указать дату","editPartyMenu"));
        buttons.add(ConvertUtils.makeButton("Удалить","delDayP"));
        buttons.add(ConvertUtils.makeButton("Назад","goBack"));

        keyboard.setKeyboard(buttons);
        out.setText(ConvertUtils.getPartyes(party)).setReplyMarkup(keyboard);
    }
//добавление дня рождения в бд
    private static boolean addDay(BirthRepo birth, Message in, SendMessage out){
        String[] parsedDay = in.getText().split(" ");
        try{
            int day = Integer.parseInt(parsedDay[parsedDay.length-2].trim());
            int month = Integer.parseInt(parsedDay[parsedDay.length-1].trim())-1;
            if((month>=0 && month<12 && day>0 && day<=31)){
                if(month==1 && day>29)throw new Exception();
                birth.saveAndFlush(new BirthDay(in.getText().substring(0, in.getText().indexOf(" "+parsedDay[parsedDay.length-2]+" "+parsedDay[parsedDay.length-1])), new Date(LocalDate.now().withDayOfMonth(day).withMonth(month).toEpochDay())));
            }
            else throw new Exception();
        }
        catch(Exception e){
            return false;
        }
        return true;

    }
//удаление дня рождения из бд
    private static void delDay(JpaRepository repo, SendMessage out){
        buttons.clear();
        if(!repo.findAll().isEmpty()){

            for(Object o: repo.findAll()){

                buttons.add(o instanceof Party?ConvertUtils.makeButton(((Party)o).getName(), "removePar"+((Party)o).getName()):ConvertUtils.makeButton(((BirthDay)o).getName(), "removeDay"+((BirthDay)o).getName()));
            }
        }
        buttons.add(ConvertUtils.makeButton("Отмена", repo instanceof BirthRepo?"lookForDays":"lookForPartyes"));
        keyboard.setKeyboard(buttons);
        out.setText(!repo.findAll().isEmpty()?"Нажмите для удаления:":"Нечего удалять").setReplyMarkup(keyboard);

    }
//меню выбора событя без даты
    private static void editParty(PartyRepo repo, SendMessage out){
        buttons.clear();
        if(!repo.findAllByDate((long)0).isEmpty()){
            for(Party o: repo.findAllByDate((long)0)){
                buttons.add(ConvertUtils.makeButton(o.getName(), "editPartyDate"+((Party)o).getName()));
            }
        }
        buttons.add(ConvertUtils.makeButton("Отмена", "lookForPartyes"));
        keyboard.setKeyboard(buttons);
        out.setText(!repo.findAll().isEmpty()?"Нажмите для указания даты:":"Все события имеют даты").setReplyMarkup(keyboard);

    }
//добавление события
    private static boolean addParty(PartyRepo party, Message in, SendMessage out, String partyName){
        String[] parsedDay = in.getText().split(" ");
        Calendar now = Calendar.getInstance();
        try{
            int day = Integer.parseInt(parsedDay[parsedDay.length-2].trim());
            int month = Integer.parseInt(parsedDay[parsedDay.length-1].trim())-1;
            int year;
            if((month>=0 && month<12 && day>0 && day<=31)){
                if(month==1 && day>29)throw new Exception();
                year = month<now.get(Calendar.MONTH)?now.get(Calendar.YEAR)+1: now.get(Calendar.YEAR);
                now.set(year, month, day, 23, 59, 0);
                party.saveAndFlush(new Party(partyName==null?in.getText().substring(0, in.getText().indexOf(" "+parsedDay[parsedDay.length-2]+" "+parsedDay[parsedDay.length-1])):partyName,  new Date(LocalDate.now().withDayOfMonth(day).withMonth(month).toEpochDay())));
            }
            else return false;
        }
        catch(Exception e){
			e.printStackTrace();
            party.saveAndFlush(new Party(in.getText(), null));
        }
        return true;

    }
//    TODO
//test this module in real spring app
}
