package com.chanl.ControlUser;

import com.chanl.DataBase;

public class Invite extends ControlUser {
    private DataBase dbase;

    public Invite(){
        dbase = new DataBase();
    }

    @Override
    public Invite buildData(IControlUserCallback lis){
        listener = lis;
        String[][] table = dbase.querySelect(new String[]{
                "telegram_id"
            },
            "chanl.users",
            String.format("where invite = %b and is_pay = %b", true, true)
        );
        if(table.length == 0){
            return this;
        }
        users = new String[table.length];
        for(int row = 0; row < table.length; row++){
            users[row] = table[row][0];
        }
        return this;
    }
}
