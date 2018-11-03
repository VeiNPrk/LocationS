package com.example.vnprk.locationsearch;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by VNPrk on 16.09.2018.
 */

@Table(database = AppDataBase.class)
public class UserClass extends BaseModel {
    @PrimaryKey
    @Column
    private int id;
    @PrimaryKey
    @Column
    private int type; //0/1/2 - мои подписчики/Я/запрашивающие
    @Column
    private int status;
    @Column
    private String name = "";

    public UserClass(){}
	
	public UserClass(int _id)
    {
        id=_id;
    }
    public UserClass(int _id, String _name)
    {
        id=_id;
        name=_name;
    }

    public UserClass(int _id, String _name, int _type)
    {
        id=_id;
        name=_name;
        type=_type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStrName()
    {
        if(this.name.length()>0)
            return this.name;
        else
            return "id: "+this.id;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return this.getStrName();
    }
}
