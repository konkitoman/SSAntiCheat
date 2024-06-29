package com.konkitoman.ssanticheat;

public interface IConfig {
    void load(ConfigIN in);

    ConfigOUT save();
}


