package com.chanl;

public class Random {
    private int count = 10;
    /** Сеттер для count
     * @param cnt Кол-во символов в строке
     */
    public void setCount(int cnt){
        this.count = cnt;
    }

    /** Генерация рандомной строки
     * @return Рандомная строка
     */
    public String getVal(){
        final String rand = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-";
        char[] result = new char[count];
        for(int j = 0; j < count; j++){
            result[j] = rand.charAt((int)(Math.random() * (rand.length() + 1)));
        }
        return String.valueOf(result);
    }
}
