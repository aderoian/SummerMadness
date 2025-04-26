package dev.armenderoian.summad.client.toast;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class CombatToast implements Toast {

    private static final Identifier TEXTURE = Identifier.ofVanilla("toast/system");
    private static final int MIN_WIDTH = 200;
    private static final int LINE_HEIGHT = 12;
    private static final int PADDING_Y = 10;
    private final Text text;
    private final long duration;
    private final boolean inCombat;
    private long startTime;
    private boolean justUpdated;
    private boolean hidden;

    public CombatToast(boolean inCombat, long duration) {
        this.inCombat = inCombat;
        this.duration = duration;
        this.text = Text.translatable("toast.summermadness." + (inCombat ? "in_combat" : "out_of_combat"));
    }

    public static CombatToast create(boolean inCombat, long duration) {
        return new CombatToast(inCombat, duration);
    }

    @Override
    public int getWidth() {
        return inCombat ? 120 : 175;
    }

    @Override
    public int getHeight() {
        return 32;
    }

    public void hide() {
        this.hidden = true;
    }

    @Override
    public Toast.Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        int j;
        int i;
        if (this.justUpdated) {
            this.startTime = startTime;
            this.justUpdated = false;
        }
        if ((i = this.getWidth()) == 160) {
            context.drawGuiTexture(TEXTURE, 0, 0, i, this.getHeight());
        } else {
            j = this.getHeight();
            int k = 28;
            int l = Math.min(4, j - 28);
            this.drawPart(context, i, 0, 0, 28);
            for (int m = 28; m < j - l; m += 10) {
                this.drawPart(context, i, 16, m, Math.min(16, j - m - l));
            }
            this.drawPart(context, i, 32 - l, j - l, l);
        }

        context.drawText(manager.getClient().textRenderer, this.text, 18, 12, Colors.YELLOW, false);
        double d = (double) duration * manager.getNotificationDisplayTimeMultiplier();
        long n = startTime - this.startTime;
        return !this.hidden && (double) n < d ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }

    private void drawPart(DrawContext context, int i, int j, int k, int l) {
        int m = j == 0 ? 20 : 5;
        int n = Math.min(60, i - m);
        Identifier identifier = TEXTURE;
        context.drawGuiTexture(identifier, 160, 32, 0, j, 0, k, m, l);
        for (int o = m; o < i - n; o += 64) {
            context.drawGuiTexture(identifier, 160, 32, 32, j, o, k, Math.min(64, i - o - n), l);
        }
        context.drawGuiTexture(identifier, 160, 32, 160 - n, j, i - n, k, n, l);
    }

    public static void add(ToastManager manager, SystemToast.Type type, Text title, @Nullable Text description) {
        manager.add(new SystemToast(type, title, description));
    }

    public static void show(ToastManager manager, SystemToast.Type type, Text title, @Nullable Text description) {
        SystemToast systemToast = manager.getToast(SystemToast.class, type);
        if (systemToast == null) {
            SystemToast.add(manager, type, title, description);
        } else {
            systemToast.setContent(title, description);
        }
    }

    public static void hide(ToastManager manager, SystemToast.Type type) {
        SystemToast systemToast = manager.getToast(SystemToast.class, type);
        if (systemToast != null) {
            systemToast.hide();
        }
    }
}
