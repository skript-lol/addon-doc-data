package lol.skript.addondocdata;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class TextUtil {
    private static final MiniMessage mm = MiniMessage.miniMessage();
    public static Component mm(String rawText) {
        return mm.deserialize(rawText);
    }
}
