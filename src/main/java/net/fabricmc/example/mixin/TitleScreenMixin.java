package net.fabricmc.example.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

	@Shadow private String splashText;
	@Shadow private int copyrightTextX;

	protected TitleScreenMixin(Text title) {
		super(title);
	}

	@Inject(at = @At("TAIL"), method = "init")
	protected void init(CallbackInfo info) {
		this.splashText = null;
		this.copyrightTextX = 1000000000;
	}

	@Inject(at = @At("RETURN"), method = "initWidgetsNormal")
	private void addCustomButton(int y, int spacingY, CallbackInfo ci) {
		this.addButton(new ButtonWidget(this.width / 2 - 100, y, 200, 20, new LiteralText("Conectarse a SquidGame"), (buttonWidget) -> {
			this.client.options.gamma = 1.0f;
			this.client.options.viewDistance = 14;
			this.client.options.particles = ParticlesMode.ALL;
			this.client.options.cloudRenderMode = CloudRenderMode.FAST;
			this.client.options.enableVsync = false;
			this.client.options.maxFps = 250;
			this.client.options.setSoundVolume(SoundCategory.MUSIC, 0);
			this.client.options.setPlayerModelPart(PlayerModelPart.CAPE, false);
			this.client.inGameHud.vignetteDarkness = 0.0f;
			this.client.openScreen(new ConnectScreen(this, this.client, new ServerInfo("SquidGame", "cd95accpjjnfen3g.alecuatro.net:42856", false)));
		}));
	}

	@Inject(method = "init", at = @At("HEAD"))
	protected void setRealmsNotificationsToFalse(CallbackInfo info) {
		assert this.client != null;
		this.client.options.realmsNotifications = false;
	}
}
