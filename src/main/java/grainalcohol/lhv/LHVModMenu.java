package grainalcohol.lhv;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import grainalcohol.lhv.client.LHVModClient;
import grainalcohol.lhv.config.EntityConfig;
import grainalcohol.lhv.config.EnvConfig;
import grainalcohol.lhv.config.GlobalConfig;
import grainalcohol.lhv.config.PlayerConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class LHVModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return HubScreen::new;
    }

    public static class HubScreen extends Screen {
        private final Screen parent;

        protected HubScreen(Screen parent) {
            super(Text.translatable("lhv.config.title"));
            this.parent = parent;
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        protected void init() {
            int buttonWidth = 200;
            int buttonHeight = 20;
            int x = this.width / 2 - buttonWidth / 2;

            this.addDrawableChild(ButtonWidget.builder(
                    Text.translatable("lhv.hub.player"),
                    button -> client.setScreen(buildPlayerConfigScreen())
            ).position(x, 60).size(buttonWidth, buttonHeight).build());

            this.addDrawableChild(ButtonWidget.builder(
                    Text.translatable("lhv.hub.entity"),
                    button -> client.setScreen(buildEntityConfigScreen())
            ).position(x, 90).size(buttonWidth, buttonHeight).build());

            this.addDrawableChild(ButtonWidget.builder(
                    Text.translatable("lhv.hub.env"),
                    button -> client.setScreen(buildEnvConfigScreen())
            ).position(x, 120).size(buttonWidth, buttonHeight).build());

            this.addDrawableChild(ButtonWidget.builder(
                    Text.translatable("lhv.hub.global"),
                    button -> client.setScreen(buildGlobalConfigScreen())
            ).position(x, 150).size(buttonWidth, buttonHeight).build());

            this.addDrawableChild(ButtonWidget.builder(
                    Text.translatable("lhv.hub.back"),
                    button -> client.setScreen(parent)
            ).position(x, this.height - 40).size(buttonWidth, buttonHeight).build());
        }

        private Screen buildPlayerConfigScreen() {
            var handler = PlayerConfig.HANDLER;
            return buildConfigScreen(handler, "lhv.config.player.title", () -> {
                handler.save();
                PlayerConfig.setConfig(handler.instance().toConfig());
            });
        }

        private Screen buildEntityConfigScreen() {
            var handler = EntityConfig.HANDLER;
            return buildConfigScreen(handler, "lhv.config.entity.title", () -> {
                handler.save();
                EntityConfig.setConfig(handler.instance().toConfig());
            });
        }

        private Screen buildEnvConfigScreen() {
            var handler = EnvConfig.HANDLER;
            return buildConfigScreen(handler, "lhv.config.env.title", () -> {
                handler.save();
                EnvConfig.setConfig(handler.instance().toConfig());
            });
        }

        private Screen buildGlobalConfigScreen() {
            var handler = GlobalConfig.HANDLER;
            return buildConfigScreen(handler, "lhv.config.global.title", handler::save);
        }

        private <T> Screen buildConfigScreen(
                ConfigClassHandler<T> handler,
                String titleKey,
                Runnable onSave
        ) {
            var builder = YetAnotherConfigLib.createBuilder()
                    .title(Text.translatable(titleKey))
                    .save(() -> {
                        onSave.run();
                        LHVModClient.ROUTER.clear();
                    });
            for (var category : handler.generateGui().categories()) {
                builder.category(category);
            }
            return builder.build().generateScreen(this);
        }

        @Override
        public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
            this.renderBackground(context);
            context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
            super.render(context, mouseX, mouseY, delta);
        }

        @Override
        public void close() {
            if (client != null) {
                client.setScreen(parent);
            }
        }
    }
}
