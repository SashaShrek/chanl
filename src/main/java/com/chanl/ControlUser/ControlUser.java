package com.chanl.ControlUser;

public class ControlUser {
    public IControlUserCallback listener; //Callback
    public String[] users = null; //Tg ID пользаков
    
    /** Вызов функции обратного вызова
     * 
     */
    private void call(){
        listener.callback();
    }

    /** Построение данных для дальнейшей обработки
     * @param lis
     * @return this
     */
    public ControlUser buildData(IControlUserCallback lis){
        this.listener = lis;
        return this;
    }

    public void startService(){
        if(users == null){
            return;
        }
        call();
    }
}
