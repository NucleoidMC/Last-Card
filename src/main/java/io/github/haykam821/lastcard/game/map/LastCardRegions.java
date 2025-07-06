package io.github.haykam821.lastcard.game.map;

import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.map_templates.TemplateRegion;

public final class LastCardRegions {
	protected static final String WAITING_SPAWN_MARKER = "waiting_spawn";
	protected static final String CHAIR_MARKER = "chair";
	protected static final String PILE_CARD_DISPLAY_MARKER = "pile_card_display";
	protected static final String PRIVATE_CARD_DISPLAY_MARKER = "private_card_display";
	protected static final String PUBLIC_CARD_DISPLAY_MARKER = "public_card_display";

	protected static final String INDEX_KEY = "Index";
	protected static final String TURN_ORDER_KEY = "TurnOrder";
	public static final String ROTATION_KEY = "Rotation";
	
	private LastCardRegions() {
		return;
	}

	protected static TemplateRegion getRegion(MapTemplate template, String marker, int index) {
		return template.getMetadata().getRegions(marker)
			.filter(region -> {
				return region.getData() != null && index == region.getData().getInt(INDEX_KEY, 0);
			})
			.findAny()
			.orElseThrow(() -> {
				return new IllegalStateException("Not enough " + marker + " regions (requesting index " + index + ")");
			});
	}

	protected static float getRotation(TemplateRegion region) {
		if (region.getData() == null) {
			return 0;
		}

		return region.getData().getFloat(ROTATION_KEY, 0);
	}

	protected static int getTurnOrder(TemplateRegion region) {
		if (region.getData() == null) {
			return 0;
		}

		return region.getData().getInt(TURN_ORDER_KEY, 0);
	}
}
