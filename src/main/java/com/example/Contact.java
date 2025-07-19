package com.example;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class Contact {
    private int contactID;
    private String name;
    private String name1;
    private String email;
    private String postalZip;
    private String address;
    private int rowIndex;
}