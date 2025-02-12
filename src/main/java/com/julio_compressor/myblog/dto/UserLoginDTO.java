package com.julio_compressor.myblog.dto;

import com.julio_compressor.myblog.model.User;

import java.io.Serializable;

public record UserLoginDTO(
        String email,
        String password
) implements Serializable {
}
