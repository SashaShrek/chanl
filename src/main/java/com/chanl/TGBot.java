package com.chanl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.*;
import java.util.Arrays;
import com.chanl.ControlUser.Block;
import com.chanl.ControlUser.Invite;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;

public class TGBot {
    /* Properties */
    private String ID_CHANNEL;
    private String ID_CHAT;
    private String PRICE;
    private String TOKEN;
    private String PAY_TOKEN;
    private String START_MSG;
    private String START_MSG_HI;
    private String INFO_MSG;
    private String NAME_CHANNEL;
    private Long ADMIN_ID;

    public String getIdChannel(){
        return this.ID_CHANNEL;
    }
    public String getIdChat(){
        return this.ID_CHAT;
    }
    public String getPrice(){
        return this.PRICE;
    }
    public String getToken(){
        return this.TOKEN;
    }
    public String getPayToken(){
        return this.PAY_TOKEN;
    }
    public String getStartMsg(){
        return this.START_MSG;
    }
    public String getStartMsgHi(){
        return this.START_MSG_HI;
    }

    private final String PAY_BUT = "ДОСТУП НА 1 МЕСЯЦ - 1399₽";
    private final String INFO_BUT = "Как оплатить?";
    private final String TGID_BUT = "Мой Telegram ID";
    private final String PATH_INFO = "src/main/resources/info.txt";
    private final String PATH = "src/main/resources/app.properties";

    private TelegramBot bot;
    private Keyboard keyboard;
    private DataBase dbase;
    private Logger logger;

    /** Чтение параметров, конструктор
     * 
     */
    public TGBot(){
        File file = new File(PATH);
        Properties prop = new Properties();
        try{
            prop.load(new FileReader(file));
        }
        catch(IOException ioex){
            System.out.println(ioex.getMessage());
            System.exit(1);
        }
        this.ID_CHANNEL = prop.getProperty("id_channel");
        this.ID_CHAT = prop.getProperty("id_chat");
        this.PRICE = prop.getProperty("price");
        this.TOKEN = prop.getProperty("token");
        this.PAY_TOKEN = prop.getProperty("pay_token");
        this.START_MSG = prop.getProperty("start_msg");
        this.START_MSG_HI = prop.getProperty("start_msg_hi");
        this.ADMIN_ID = Long.parseLong(prop.getProperty("admin_id"));
        this.NAME_CHANNEL = prop.getProperty("name_channel");
        this.INFO_MSG = readInfo();
        dbase = new DataBase();
        dbase.readProp().getConn();
        logger = new Logger();
    }

    /** Чтение параметра "Как оплатить?"
     * 
     */
    private String readInfo(){
        String result = "";
        File file = new File(PATH_INFO);
        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line;
            while((line = reader.readLine()) != null){
                result = result.concat(String.format("%s\n", line));
            }
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            System.exit(1);
        }
        return result;
    }

    /** Соединение с ботом
     * 
     */
    public void setConnect(){
        bot = new TelegramBot(TOKEN);
        keyboard = createBtns();
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                switch (update.message().text()) {
                    case "/start":
                        onStart(update);
                        break;
                    case PAY_BUT:
                        System.out.println(update.message().chat().username());
                        System.out.println(update.message().chat().firstName());
                        break;
                    case INFO_BUT:
                        sendMsg(update.message().chat().id(), INFO_MSG);
                        break;
                    case TGID_BUT:
                        sendMsg(update.message().chat().id(), String.format("Ваш Telegram ID: %d", update.message().chat().id()));
                        break;
                    default:
                        sendMsg(update.message().chat().id(), "Нет такой команды!");
                        break;
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, exHand -> {
            if(exHand.response() != null){
                logger.setData((long)-1, "ERROR", "setUpdatesListener", exHand.response().description()).setLog();
                exHand.response().errorCode();
                exHand.response().description();
            }else{
                exHand.printStackTrace();
            }
        });
    }
    
    /** Создание кнопок бота
     *
     */
    private Keyboard createBtns(){
        return new ReplyKeyboardMarkup(
            new KeyboardButton[]{
                new KeyboardButton(PAY_BUT)
            },
            new KeyboardButton[]{
                new KeyboardButton(INFO_BUT),
                new KeyboardButton(TGID_BUT)
            }
        ).resizeKeyboard(true);
    }

    /** Отправка сообщений пользователю
     * @param id - telegram_id пользователя
     * @param msg - сообщение для пользователя
     */
    private void sendMsg(Long id, String msg){
        bot.execute(new SendMessage(id, msg).replyMarkup(keyboard));
    }

    /** Обработчик при нажатии кнопки СТАРТ в боте
     *
     */
    private void onStart(Update update){    
        sendMsg(update.message().chat().id(), START_MSG);
        sendMsg(update.message().chat().id(), String.format(START_MSG_HI, update.message().chat().firstName()));
        String[][] table = dbase.querySelect(
            new String[]{
                "count(*)"
            }, 
            "chanl.users", 
            String.format("where telegram_id = '%s'", update.message().chat().id())
        );
        if(table.length == 0){
            return;
        }
        if(Integer.parseInt(table[0][0]) >= 1){
            logger.setData(update.message().chat().id(), "INFO", "onStart", "Пользователь существует").setLog();
            return;
        }
        String query = String.format("insert into chanl.users (telegram_id, username, fst_name) values ('%s', '%s', '%s');",
            update.message().chat().id(), update.message().chat().username(), update.message().chat().firstName());
        dbase.queryUpdInsDel(query);
        logger.setData(update.message().chat().id(), "INFO", "onStart", "Пользователь добавлен").setLog();
    }

    private void onPay(Update update){}

    private void startServiceThreads(Update update){}
}
