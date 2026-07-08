package org.cat.shoolpluginmy;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class Placeholders extends PlaceholderExpansion {
    @Override
    // "префикс" вашего плейсхолдера, общепринято писать название плагина %tutorial_(params)%
    // Более научно, это строчка между первого % до _.
    public String getIdentifier() {
        return "createhead";
    }

    @Override
    // Тут указываем автора (вы)
    public String getAuthor() {
        return "Pashacat";
    }

    @Override
    // Тут версию, но чаще всего она бесполезна для ваших целей. Тут уже это для eCloud.
    public String getVersion() {
        return "1.2.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    // Тут можно проверять наличие плагинов и т.п., в нашем случаи ставим просто на true;
    public boolean canRegister() {
        return true;
    } //%createhead_shet%
    @Override
    // Тут мы и будет работать дальше.
    public String onPlaceholderRequest(final Player player, final String params) {
        if (player == null) {
            return null;
        }
        switch (params) {
            case "shet":
                return ShoolPluginMy.getValue(player) + "";
        }
        return null;
    }
}
