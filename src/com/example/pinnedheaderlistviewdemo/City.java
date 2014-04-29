package com.example.pinnedheaderlistviewdemo;

public class City {
    private String id;
    private String name;

    private String pyf;/*城市拼音全拼   spell full*/
    private String pys;/*城市拼音缩写   spell short*/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPyf() {
        return pyf;
    }

    public void setPyf(String pyf) {
        this.pyf = pyf;
    }

    public String getPys() {
        return pys;
    }

    public void setPys(String pys) {
        this.pys = pys;
    }

    /**
     * 获取拼音的简写  第一个字母
     * abbreviation  缩写词
     * 名字应该改为 getFirstWordOfAbbreviation
     * @return
     */
    public String getSortKey() {
        return pys.substring(0, 1);
    }

    @Override
    public String toString() {
        return "City{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", 拼音='" + pyf + '\'' +
                ", 缩写='" + pys + '\'' +
                '}';
    }
}
