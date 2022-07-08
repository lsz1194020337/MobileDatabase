package com.example.mobiledatabase.bean;

import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.annotation.SmartTable;

@SmartTable(name = "Table")
public class Table {
    @SmartColumn(id = 1, name = "column1")
    private String column1;
    @SmartColumn(id = 2, name = "column2")
    private String column2;
    @SmartColumn(id = 3, name = "column3")
    private String column3;
    @SmartColumn(id = 4, name = "column4")
    private String column4;
    @SmartColumn(id = 5, name = "column5")
    private String column5;
    @SmartColumn(id = 6, name = "column6")
    private String column6;
    @SmartColumn(id = 7, name = "column7")
    private String column7;
    @SmartColumn(id = 8, name = "column8")
    private String column8;
    @SmartColumn(id = 9, name = "column9")
    private String column9;
    @SmartColumn(id = 10, name = "column10")
    private String column10;
    private int _id;

    public Table(String column1,
                 String column2,
                 String column3,
                 String column4,
                 String column5,
                 String column6,
                 String column7,
                 String column8,
                 String column9,
                 String column10) {
        super();
        this.column1 = column1;
        this.column2 = column2;
        this.column3 = column3;
        this.column4 = column4;
        this.column5 = column5;
        this.column6 = column6;
        this.column7 = column7;
        this.column8 = column8;
        this.column9 = column9;
        this.column10 = column10;
    }

    @Override
    public String toString() {
        return "Table{" +
                "column1='" + column1 + '\'' +
                ", column2='" + column2 + '\'' +
                ", column3='" + column3 + '\'' +
                ", column4='" + column4 + '\'' +
                ", column5='" + column5 + '\'' +
                ", column6='" + column6 + '\'' +
                ", column7='" + column7 + '\'' +
                ", column8='" + column8 + '\'' +
                ", column9='" + column9 + '\'' +
                ", column10='" + column10 + '\'' +
                ", _id=" + _id +
                '}';
    }

    public String getColumn1() {
        return column1;
    }

    public void setColumn1(String column1) {
        this.column1 = column1;
    }

    public String getColumn2() {
        return column2;
    }

    public void setColumn2(String column2) {
        this.column2 = column2;
    }

    public String getColumn3() {
        return column3;
    }

    public void setColumn3(String column3) {
        this.column3 = column3;
    }

    public String getColumn4() {
        return column4;
    }

    public void setColumn4(String column4) {
        this.column4 = column4;
    }

    public String getColumn5() {
        return column5;
    }

    public void setColumn5(String column5) {
        this.column5 = column5;
    }

    public String getColumn6() {
        return column6;
    }

    public void setColumn6(String column6) {
        this.column6 = column6;
    }

    public String getColumn7() {
        return column7;
    }

    public void setColumn7(String column7) {
        this.column7 = column7;
    }

    public String getColumn8() {
        return column8;
    }

    public void setColumn8(String column8) {
        this.column8 = column8;
    }

    public String getColumn9() {
        return column9;
    }

    public void setColumn9(String column9) {
        this.column9 = column9;
    }

    public String getColumn10() {
        return column10;
    }

    public void setColumn10(String column10) {
        this.column10 = column10;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }
}
