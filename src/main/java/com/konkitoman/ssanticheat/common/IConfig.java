package com.konkitoman.ssanticheat.common;

public interface IConfig {
    void load(ConfigIN in);

    ConfigOUT save();
}


