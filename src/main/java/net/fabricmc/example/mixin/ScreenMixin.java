package net.fabricmc.example.mixin;

import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Collections;
import java.util.List;

@Mixin(value = Screen.class, priority = 1100)
public abstract class ScreenMixin extends AbstractParentElement implements Drawable {

    @Shadow protected MinecraftClient client;
    @Shadow public int width;
    @Shadow public int height;

    @Inject(method = "init", at = @At("RETURN"))
    private void init(MinecraftClient client, int width, int height, CallbackInfo info) {
        if ((Screen)(Object)this instanceof TitleScreen) {
            afterTitleScreenInit();
        } else if ((Screen)(Object)this instanceof GameMenuScreen) {
            afterGameMenuScreenInit();
        }
    }

    private void afterTitleScreenInit() {
        final int spacing = 24;
        int yOffset = 0;
        int posY = 0;
        List<ClickableWidget> widgetList = Screens.getButtons((Screen)(Object)this);
        //Collections.reverse(widgetList);
        for (ClickableWidget button : widgetList) {
            if (this.buttonMatchesKey(button, "menu.singleplayer")) {
                button.visible = false;
            }

            /*if (ConfigHandler.REMOVE_MULTIPLAYER) {
                if (this.buttonMatchesKey(button, "menu.multiplayer")) {
                    button.visible = false;
                    yOffset -= spacing;
                }
            }*/

            if (this.buttonMatchesKey(button, "menu.online")) {
                button.visible = false;
                yOffset -= spacing;
            }

            // TODO: To decide
            /*if (this.buttonMatchesKey(button, "narrator.button.language")) {
                button.visible = false;
            }*/

            // TODO: To decide
            /*if (this.buttonMatchesKey(button, "narrator.button.accessibility")) {
                button.visible = false;
                posY = button.y - yOffset;
            }*/
        }
    }

    private void afterGameMenuScreenInit() {
        final int buttonWidth = 204;
        final int spacing = 24;
        int yOffset = 0;

        for (ClickableWidget button : Screens.getButtons((Screen)(Object)this)) {
            if (this.buttonMatchesKey(button, "menu.sendFeedback")) {
                button.visible = false;
            }

            if (this.buttonMatchesKey(button, "menu.reportBugs")) {
                button.setWidth(buttonWidth);
                button.x = this.width / 2 - buttonWidth / 2;
            }

            if (this.buttonMatchesKey(button, "menu.reportBugs")) {
                button.visible = false;
                yOffset += spacing;
            }
            if (this.buttonMatchesKey(button, "menu.sendFeedback")) {
                button.setWidth(buttonWidth);
                button.x = this.width / 2 - buttonWidth / 2;
            }


            button.y -= yOffset;
        }
    }

    private static boolean buttonMatchesKey(ClickableWidget button, String key) {
        Text buttonMessage = button.getMessage();
        if (buttonMessage instanceof TranslatableText) {
            String buttonKey = ((TranslatableText) buttonMessage).getKey();
            if (buttonKey.equals(key)) {
                return true;
            }
            Object[] textArgs = ((TranslatableText) buttonMessage).getArgs();
            for (Object arg : textArgs) {
                if (arg instanceof TranslatableText) {
                    String argKey = ((TranslatableText) arg).getKey();
                    if (argKey.equals(key)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}