package com.portfolio.allinone.utils;

import com.portfolio.allinone.models.BirthDay;
import com.portfolio.allinone.models.Party;
import com.portfolio.allinone.models.TeleUser;
import com.portfolio.allinone.repo.BirthRepo;
import com.portfolio.allinone.repo.PartyRepo;
import com.portfolio.allinone.repo.TeleUserRepo;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.Clock;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.List;


// класс для обработки сбытий в чате

public class ChatUtils {
    public static boolean day=false;
    public static boolean tomorrow=false;
    public static boolean week=false;
    public static boolean month=false;
    public static boolean unassigned=false;

    //время сервера, с которого начинаются оповещения
    private static int time = 11;

    //каждая проверка совершается раз в час, статус текущей проверки восстанавливается пи следующей
    public static int timeFor() {
        if(LocalTime.now().getHour()==time){
            unassigned=false;
            return 1;
		
        }
        else if(LocalTime.now().getHour()==(time+1)){
            day=false;
            return 2;
        }
        else if(LocalTime.now().getHour()==(time+2)){
            tomorrow=false;
            return 3;
        }
        else if(LocalTime.now().getHour()==(time+3)){
            week=false;
            return 4;
        }
        else if(LocalTime.now().getHour()==(time+4)){
            month=false;
            return 5;
        }

        return 0;
    }



//метод добавляет пользователя в бд и посылает личное сообщение
    private static void addMe(TeleUserRepo repo, Message in, SendMessage out){
        String name;
        if(!in.getFrom().getUserName().equals("null")){name=in.getFrom().getUserName();}
        else if(!in.getFrom().getFirstName().equals("null")){name=in.getFrom().getFirstName();}
        else{name=in.getFrom().getLastName();}
        if(!repo.existsByTeleuser((long)in.getFrom().getId())){
            repo.saveAndFlush(new TeleUser((long)in.getFrom().getId(), 0));
            out.setText("Добавлен пользователь "+name);
            UserUtils.mainScreen(repo, null, null, null, out.setChatId((long)in.getFrom().getId()));
        }
        else{
            out.setText(name+" уже есть в списке");}
        }

        //метод проверяет наличие событий в указанном диапазоне
	public static SendMessage searchDays(PartyRepo party, BirthRepo birth, TeleUserRepo repo ,int days){

        StringBuilder on= new StringBuilder();

		long today =Calendar.getInstance().getTimeInMillis();
		long daysInt = ((long)days)*1000*60*60*24;
        if(days!=-1) {

            List<Party> p = party.findAllByDateBetween(today, today + daysInt);
            List<BirthDay> b = birth.findAll();


            if (!p.isEmpty()) {

                on.append(ConvertUtils.notifyText(on.toString(), days));
                on.append("\nСобытия:\n");
                for (Party day : p) {
                    long i = (day.getDate() - today) / (1000 * 60 * 60 * 24);
                    on.append(day.getName()).append(days > 1 ? i > 1 ? " (осталось дней " + i + ")\n" : i > 0 ? " (завтра)\n" : " (сегодня)\n" : "\n");

                }
            }
            if (!b.isEmpty()) {

                for (BirthDay day : b) {

                    Calendar toDate = Calendar.getInstance();
                    toDate.set(Calendar.MONTH, day.getMonth());
                    toDate.set(Calendar.DATE, day.getDay());

                    long i = (toDate.getTimeInMillis() - today) / (1000 * 60 * 60 * 24);


                    if (i >= 0 && i < days) {
                        on.append(ConvertUtils.notifyText(on.toString(), days));
                        if(!on.toString().contains("\nДни рождения:\n") ){
                            on.append("\nДни рождения:\n");}
                        on.append(day.getName()).append(i > 1 ? " (осталось дней " + i + ")\n" : i > 0 ? " (завтра)\n" : " (сегодня)\n" );
                    }

                }
            }
        }
        else{
            List<Party> p = party.findAllByDate((long)0);
            for (Party day : p) {
                on.append(ConvertUtils.notifyText(on.toString(), days));
                on.append(day.getName()).append("\n");

            }
        }
        SendMessage out = new SendMessage();
        out.setChatId(repo.findByTelerole(1).getTeleuser());
        out.setText(on.toString());
		return out;
    }

//обработка команд в чате
    public static void chatCommand(PartyRepo party, BirthRepo birth, TeleUserRepo user, Message in, SendMessage out){
        if(in.getText().contains("/addme")){
            addMe(user, in, out);
        }

    }


}
