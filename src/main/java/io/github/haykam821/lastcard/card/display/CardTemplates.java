package io.github.haykam821.lastcard.card.display;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import eu.pb4.mapcanvas.api.core.CanvasImage;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import io.github.haykam821.lastcard.Main;
import net.minecraft.ReportedException;
import net.minecraft.CrashReport;

public final class CardTemplates {
	public static final DrawableCanvas RED_FRONT = CardTemplates.load("red_front");
	public static final DrawableCanvas GREEN_FRONT = CardTemplates.load("green_front");
	public static final DrawableCanvas YELLOW_FRONT = CardTemplates.load("yellow_front");
	public static final DrawableCanvas BLUE_FRONT = CardTemplates.load("blue_front");

	public static final DrawableCanvas WILD_FRONT = CardTemplates.load("wild_front");
	public static final DrawableCanvas BACK = CardTemplates.load("back");

	public static final DrawableCanvas DRAW_TWO_SYMBOL = CardTemplates.load("draw_two_symbol");
	public static final DrawableCanvas DRAW_FOUR_SYMBOL = CardTemplates.load("draw_four_symbol");

	public static final DrawableCanvas REVERSE_SYMBOL = CardTemplates.load("reverse_symbol");
	public static final DrawableCanvas SKIP_SYMBOL = CardTemplates.load("skip_symbol");

	private CardTemplates() {
		return;
	}

	private static DrawableCanvas load(String id) {
		try {
			String file = "assets/" + Main.MOD_ID + "/textures/card/" + id + ".png";
			Path path = Main.MOD_CONTAINER.findPath(file).orElseThrow();

			InputStream stream = Files.newInputStream(path);
			BufferedImage image = ImageIO.read(stream);

			return CanvasImage.from(image);
		} catch (Exception exception) {
			CrashReport report = new CrashReport("Loading card template", exception);
			report.addCategory("Card template: " + id);

			throw new ReportedException(report);
		}
	}
}
