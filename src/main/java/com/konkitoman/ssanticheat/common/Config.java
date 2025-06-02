package com.konkitoman.ssanticheat.common;

import com.konkitoman.ssanticheat.common.xray.XRayConfig;

public class Config implements IConfig {
    public XRayConfig xray = new XRayConfig();

    public Config() {

    }

    @Override
    public void load(ConfigIN in) {
        in.readConfig("xray").ifPresent(value -> {
            this.xray.load(value);
        });

    }

    @Override
    public ConfigOUT save() {
        ConfigOUT out = new ConfigOUT();

        out.writeConfig("xray", xray.save());

        return out;
    }
}
