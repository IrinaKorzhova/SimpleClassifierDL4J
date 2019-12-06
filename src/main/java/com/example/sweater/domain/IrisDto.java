package com.example.sweater.domain;

import lombok.Data;


@Data
public class IrisDto {

    private float lengthPetal;
    private float widthPetal;
    private float lengthSepal;
    private float widthSepal;

    public IrisDto() {
    }

    public IrisDto(float lengthPetal, float widthPetal, float lengthSepal, float widthSepal) {
        this.lengthPetal=lengthPetal;
        this.widthPetal=widthPetal;
        this.lengthSepal=lengthSepal;
        this.widthSepal=widthSepal;
    }

}
