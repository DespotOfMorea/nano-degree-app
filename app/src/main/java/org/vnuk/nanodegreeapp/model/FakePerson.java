package org.vnuk.nanodegreeapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FakePerson {
    private String title;
    private String firstName;
    private String lastName;
    private String gender;
    private int age;
    private PersonAddress address;

    public FakePerson(String title, String firstName, String lastName) {
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String simpleDataDisplay () {
        return title +" "+ firstName +" "+ lastName;
    }
}
