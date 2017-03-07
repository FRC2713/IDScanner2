package org.iraiders.idscanner2;

public class IniProperty {
    String section;
    String name;
    String value;

    public IniProperty(String n, String v, String s) {
        name = n;
        value = v;
        section = s;
    }

    public String toString() {
        return ("Name: " + name + ", Value: " + value + ", Section: " + section + "\n");
    }
}

