package io.github.haykam821.lastcard.card.display;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import eu.pb4.mapcanvas.api.utils.VirtualDisplay;
import eu.pb4.mapcanvas.api.utils.VirtualDisplay.TypedInteractionCallback;
import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.card.color.CardColor;
import io.github.haykam821.lastcard.card.display.layout.CardLayout;
import io.github.haykam821.lastcard.card.display.layout.CardSpacing;
import io.github.haykam821.lastcard.card.display.layout.LayoutEntry;
import io.github.haykam821.lastcard.card.display.region.CardRegion;
import io.github.haykam821.lastcard.game.map.LastCardRegions;
import io.github.haykam821.lastcard.game.phase.PlayerEntryGetter;
import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ClickType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import xyz.nucleoid.map_templates.BlockBounds;
import xyz.nucleoid.map_templates.TemplateRegion;

public abstract class CardDisplay implements TypedInteractionCallback {
	protected final PlayerEntryGetter entryGetter;

	private final Map<CardRenderData, DrawableCanvas> canvasCache = new HashMap<>();
	private final VirtualDisplay display;

	private final Set<CardRegion> regions = new HashSet<>();

	protected CardDisplay(PlayerEntryGetter entryGetter, TemplateRegion region) {
		this.entryGetter = entryGetter;

		BlockBounds bounds = region.getBounds();
		BlockPos size = bounds.size();

		int rotation = region.getData() == null ? 0 : region.getData().getInt(LastCardRegions.ROTATION_KEY, 0) % 4;

		int x = size.getX() + 1;
		int z = size.getZ() + 1;

		PlayerCanvas canvas = rotation % 2 == 0 ? DrawableCanvas.create(x, z) : DrawableCanvas.create(z, x);
		BlockPos pos = CardDisplay.getDisplayPos(rotation, bounds);

		this.display = VirtualDisplay.builder(canvas, pos, Direction.UP)
			.rotation(BlockRotation.values()[rotation])
			.callback(this)
			.invisible()
			.build();
	}

	public void update() {
		PlayerCanvas canvas = this.getCanvas();

		CanvasUtils.clear(canvas);
		this.regions.clear();

		CardLayout layout = new CardLayout(this);
		layout.addCards(canvas, this.getCards());

		int startX = (canvas.getWidth() - layout.getTotalWidth()) / 2;
		int startY = (canvas.getHeight() - layout.getTotalHeight()) / 2;

		for (LayoutEntry entry : layout.getEntries()) {
			Card card = entry.card();
			CardColor overrideColor = entry.overrideColor();

			entry.render(canvas, this.getCardCanvas(card, overrideColor), this.hasOutline(card), startX, startY);

			CardRegion region = this.getCardRegion(card, overrideColor, startX + entry.x(), startY + entry.y(), startX + entry.maxX(), startY + entry.maxY());
			if (region != null) {
				this.regions.add(region);
			}
		}

		canvas.sendUpdates();
	}

	@Override
	public void onClick(ServerPlayerEntity player, ClickType type, int x, int y) {
		AbstractPlayerEntry entry = this.entryGetter.getPlayerEntry(player);

		if (entry != null) {
			if (entry.getPhase().isGameEnding()) {
				return;
			}

			for (CardRegion region : this.regions) {
				if (region.contains(x, y)) {
					region.onClick(entry, x, y);
					return;
				}
			}
		}
	}

	private final PlayerCanvas getCanvas() {
		return this.display.getCanvas();
	}

	public final void add(ServerPlayerEntity viewer) {
		this.display.addPlayer(viewer);
		this.getCanvas().addPlayer(viewer);
	}

	public final void remove(ServerPlayerEntity viewer) {
		this.display.removePlayer(viewer);
		this.getCanvas().removePlayer(viewer);
	}

	public void moveViewer(ServerPlayerEntity player, CardDisplay toDisplay) {
		if (player != null) {
			this.remove(player);
			toDisplay.add(player);
		}
	}

	public final void destroy() {
		this.display.destroy();
		this.getCanvas().destroy();
	}

	public abstract Iterable<Card> getCards();

	public boolean shouldFlattenSelectors() {
		return false;
	}

	public abstract DrawableCanvas renderCardCanvas(CardRenderData data);

	public final DrawableCanvas getCardCanvas(Card card, CardColor overrideColor) {
		CardRenderData data = new CardRenderData(card, overrideColor);
		return this.canvasCache.computeIfAbsent(data, this::renderCardCanvas);
	}

	/**
	 * {@return the spacing between cards on the X axis}
	 * @param width the width of the last card
	 */
	public int getHorizontalSpacing(int width) {
		return width + CardSpacing.GAP_X;
	}

	/**
	 * {@return the spacing between rows of cards on the Y axis}
	 * @param maxHeight the height of the tallest card on the last row
	 */
	public int getVerticalSpacing(int maxHeight) {
		return maxHeight + CardSpacing.PADDING_Y;
	}

	public int getHorizontalMargin(int row) {
		return CardSpacing.PADDING_X;
	}

	public boolean hasOutline(Card card) {
		return false;
	}

	public CardRegion getCardRegion(Card card, CardColor color, int minX, int minY, int maxX, int maxY) {
		return null;
	}

	private static BlockPos getDisplayPos(int rotation, BlockBounds bounds) {
		BlockPos min = bounds.min();
		BlockPos max = bounds.max();

		int x = rotation == 0 || rotation == 3 ? min.getX() : max.getX();
		int z = rotation == 0 || rotation == 1 ? min.getZ() : max.getZ();

		return new BlockPos(x, min.getY(), z);
	}
}
