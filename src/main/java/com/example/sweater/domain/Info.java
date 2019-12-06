package com.example.sweater.domain;

import lombok.Data;

@Data
public class Info {
    private String label;

    public Info(String label) {
        this.label = label;
    }
}
