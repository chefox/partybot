package com.portfolio.allinone.utils;

import com.portfolio.allinone.models.BirthDay;
import com.portfolio.allinone.models.Party;
import com.portfolio.allinone.repo.PartyRepo;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.util.*;




//класс содержит методы работы с бд и формирования текста сообщений
class ConvertUtils {

    private static DateFormatSymbols myDateFormatSymbols = new DateFormatSymbols() {

        @Override
        public String[] getMonths() {
            return new String[]{"января", "февраля", "марта", "апреля", "мая", "июня",
                    "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        }
    };
    private static int daysTo(Calendar day){

        long now = Calendar.getInstance().getTimeInMillis();
        int days=(int)((day.getTimeInMillis()-now)/(24*60*60*1000));
        return days>=0?days:365-days;
    }


//формирует кнопки для меню, обрезает длинные строки
    static List makeButton(String text, String act){
        return Collections.singletonList((new InlineKeyboardButton().setText(text.length()<25?text:text.substring(0, 20)+"...").setCallbackData(act)));
    }

    //генерирует объект календаря по числу и месяцу(формат хранения дней рождения)
    private static Calendar toDate(int d, int m){
        Calendar def = Calendar.getInstance();
        def.set(def.get(Calendar.YEAR),m, d, 23,59, 0);
        return(def);
    }
    //создает список всех дней рождений
    static String getBirthDays(List<BirthDay> all){
        StringBuilder result= new StringBuilder();
        Calendar day;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM", myDateFormatSymbols );

        if(!all.isEmpty()) {
            for (BirthDay b : all) {
                day=toDate(b.getDay(), b.getMonth());
                result.append(b.getName()).append(" ").append(dateFormat.format(day.getTime())).append("(осталось дней: ").append(daysTo(day)).append(")\n");
            }
        }
        return result.length()>0? result.toString() :"Пока ничего нет";
    }
    //создает список всех событий
    static String getPartyes(PartyRepo all){
        String result;
        StringBuilder unassigned= new StringBuilder();
        StringBuilder assigned= new StringBuilder();

        Calendar day;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", myDateFormatSymbols );

        if(!all.findAllByDateAfter(Calendar.getInstance().getTimeInMillis()).isEmpty()) {
            assigned.append("====================\nГрядущие события\n====================\n");
            for (Party p : all.findAllByDateAfter(Calendar.getInstance().getTimeInMillis())) {
                day = Calendar.getInstance();
                day.setTimeInMillis(p.getDate());
                assigned.append(p.getName()).append(" ").append(dateFormat.format(day.getTime())).append("(осталось дней: ").append(daysTo(day)).append(")\n");
            }
        }

        if(!all.findAllByDate((long)0).isEmpty()) {
            unassigned.append("====================\nСобытия без даты\n====================\n");
            for (Party p : all.findAllByDate((long)0)) {
                unassigned.append(p.getName()).append("\n");
            }
        }
        result= unassigned.toString() +assigned;
        return result.length()>0?result:"====================\nСписок событий\n====================\nПока ничего нет";

    }

    // генерирует первую строку оповещения
    static String notifyText(String on, int days){
        if(on.length()==0){
            switch(days){
                case(30):
                    on="В этом месяце ";
                    break;
                case(7):
                    on="На этой неделе ";
                break;
                case(1):
                    on="Завтра";
                break;
                case(0):
                    on="Сегодня ";
                break;
                case(-1):
                    on="Напомню, что не назначена дата следующих событий:\n";
                break;
            }
            return on;
        }
        else return "";
    }
}
