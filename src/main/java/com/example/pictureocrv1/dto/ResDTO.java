package com.example.pictureocrv1.dto;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class ResDTO {
    private AtomicInteger total;
    private AtomicInteger success;
    private AtomicInteger fail;
}
