package com.konkitoman.ssanticheat.xray;

import com.konkitoman.ssanticheat.ConfigIN;
import com.konkitoman.ssanticheat.ConfigOUT;
import com.konkitoman.ssanticheat.IConfig;
import com.konkitoman.ssanticheat.xray.visibile_check.VisibleCheckModeLIGHT;
import com.konkitoman.ssanticheat.xray.visibile_check.VisibleCheckModeVISIBLE;
import net.minecraft.block.Blocks;

public class XRayConfig implements IConfig {
    public boolean enable = false;
    public XRay.VisibleCheck mode = new VisibleCheckModeLIGHT();
    public XRay.ShadowBlock shadow = new XRay.ShadowBlockRandom();

    @Override
    public void load(ConfigIN in) {
        in.readBool("enable").ifPresent(value -> {
            this.enable = value;
        });

        in.readString("mode").ifPresent(mode -> {
            switch (mode) {
                case "light":
                    this.mode = new VisibleCheckModeLIGHT();
                    break;
                case "visible":
                    this.mode = new VisibleCheckModeVISIBLE();
                    break;
                default:
                    break;
            }
        });

        in.readConfig("shadow").ifPresent(shadow -> {
            shadow.readString("type").ifPresent(type -> {
                shadow.readConfig("value").ifPresent(value -> {
                    switch (type) {
                        case "random":
                            this.shadow = new XRay.ShadowBlockRandom();
                            this.shadow.load(value);
                            break;
                        case "solid":
                            this.shadow = new XRay.ShadowBlockSolid(Blocks.BLACK_CONCRETE);
                            this.shadow.load(value);
                            break;
                        default:
                            break;
                    }
                });

            });
        });

    }

    @Override
    public ConfigOUT save() {
        ConfigOUT out = new ConfigOUT();

        out.writeBool("enable", enable);

        if (mode instanceof VisibleCheckModeLIGHT) {
            out.writeString("mode", "light");
        }
        if (mode instanceof VisibleCheckModeVISIBLE) {
            out.writeString("mode", "visible");
        }

        if (shadow instanceof XRay.ShadowBlockRandom) {
            ConfigOUT o = new ConfigOUT();
            o.writeString("type", "random");
            o.writeConfig("value", shadow.save());
            out.writeConfig("shadow", o);
        }

        if (shadow instanceof XRay.ShadowBlockSolid) {
            ConfigOUT o = new ConfigOUT();
            o.writeString("type", "solid");
            o.writeConfig("value", shadow.save());
            out.writeConfig("shadow", o);
        }

        return out;
    }
}
