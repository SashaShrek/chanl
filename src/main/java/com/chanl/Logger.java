package com.chanl;

public class Logger {
    private String ID;
    private String TYPE;
    private String SERVICE;
    private String TEXT;

    private DataBase dbase;
    private Random random;

    public Logger(){
        dbase = new DataBase();
        random = new Random();
    }

    /** Заполнение параметров
     * @param id - telegram_id пользователя
     * @param type - тип лога (INFO, WARN, ERROR)
     * @param service - метод, в котором логирование
     * @param text - текст лога
     * @return - this
     */
    public Logger setData(Long id, String type, String service, String text){
        this.ID = id.toString();
        this.TYPE = type;
        this.SERVICE = service;
        this.TEXT = text;
        return this;
    }

    /** Создание лога
     * 
     */
    public void setLog(){
        if(ID.isEmpty() || TYPE.isEmpty() || SERVICE.isEmpty() || TEXT.isEmpty()){
            return;
        }
        random.setCount(10);
        String rand = random.getVal();
        String query = String.format("insert into chanl.logs (id, user_id, type, service, log_char) select '%s', id, '%s', '%s', '%s' from chanl.users where telegram_id = '%s'",
            rand, TYPE, SERVICE, TEXT, ID);
        dbase.queryUpdInsDel(query);
    }
}
