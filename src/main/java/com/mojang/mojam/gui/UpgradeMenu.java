package com.mojang.mojam.gui;

import org.newdawn.slick.*;

import com.mojang.mojam.entity.*;
import com.mojang.mojam.entity.Player.Attributes;
import com.mojang.mojam.world.PizzaResource;

public class UpgradeMenu {

    private Player player;
    private Artichoke artichoke;
    private boolean visible;

    private static final Color shade = new Color(0, 0, 0, .5f);
    private static final Color cantAffordColor = new Color(1, 1, 1, .5f);
    private static final Color goldColor = new Color(224, 217, 111, 255);

    private Animation[] upgradeIcons = new Animation[Player.Attributes.COUNT];
    private Animation[] artichokeUpgradeIcons = new Animation[Artichoke.ArtichokeAttributes.COUNT];

    private final static int UPGRADE_SPACING = 54;
    private final static int[] UPGRADE_ORDER = {
            Attributes.FIRE_DAMAGE_LEVEL, Attributes.FIRERATE, Attributes.SPEED, Attributes.REGENERATE, Attributes.HEALTH, Attributes.BEAM_DURATION, Attributes.SHOCKWAVE, Attributes.JETPACK,
    };
    private final static int[] ARTICHOKE_UPGRADE_ORDER = {
            Artichoke.ArtichokeAttributes.FORTIFICATION, Artichoke.ArtichokeAttributes.TOWERBLASTER, Artichoke.ArtichokeAttributes.SHOCKWAVE, Artichoke.ArtichokeAttributes.SPIDERS
    };

    public UpgradeMenu(Player player, Artichoke artichoke) {
        this.player = player;
        this.artichoke = artichoke;
        try {
            for (int i = 0; i < upgradeIcons.length; i++) {
                upgradeIcons[i] = new Animation(new SpriteSheet(Player.Attributes.ICONS[i], 48, 48), 200);
            }
            for (int i = 0; i < artichokeUpgradeIcons.length; i++) {
                artichokeUpgradeIcons[i] = new Animation(new SpriteSheet(Artichoke.ArtichokeAttributes.ICONS[i], 48, 48), 200);
            }
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public void update(GameContainer container, int deltaMS) {

        Input input = container.getInput();

        if (input.isMousePressed(0)) {
            int mouseX = input.getMouseX();
            int mouseY = input.getMouseY();

            int yPos = container.getHeight() / 2 - 240 + 50;
            int xPos = container.getWidth() / 2 - 260 - 70;

            if (mouseX >= xPos && mouseX <= xPos + 48) {
                int selectedUpgrade = (mouseY - yPos) / UPGRADE_SPACING;
                if (selectedUpgrade >= 0 && selectedUpgrade < UPGRADE_ORDER.length) {
                    selectedUpgrade = UPGRADE_ORDER[selectedUpgrade];
                    int[] cost = player.getUpgradeCost(selectedUpgrade);
                    if (cost != null) {
                        if (canAfford(cost)) {
                            player.buyUpgrade(selectedUpgrade);
                        }
                    }
//                    player.setMoney(money - upgradeCost);
//                    player.getAttributes().levels[selectedUpgrade]++;
                }
            }
            xPos = container.getWidth() / 2 + 70 / 2;
            if (mouseX >= xPos && mouseX <= xPos + 48) {
                int selectedUpgrade = (mouseY - yPos) / UPGRADE_SPACING;
                if (selectedUpgrade >= 0 && selectedUpgrade < ARTICHOKE_UPGRADE_ORDER.length) {
                    selectedUpgrade = ARTICHOKE_UPGRADE_ORDER[selectedUpgrade];
                    int[] cost = artichoke.getUpgradeCost(selectedUpgrade);
                    if (cost != null) {
                        if (canAfford(cost)) {
                            artichoke.buyUpgrade(selectedUpgrade);
                        }
                    }
                }
            }
        }
    }

    private boolean canAfford(int[] cost) {
        if (cost == null) {
            return false;
        }
        for (int r = 0; r < PizzaResource.NUM_RESOURCES; r++) {
            if (cost[r] > player.getResource(r)) {
                return false;
            }
        }
        return true;
    }

    public void render(GameContainer container, Graphics g) {
        if (!isVisible()) {
            return;
        }

        g.setColor(shade);
        g.fillRect(0, 0, container.getWidth(), container.getHeight());

        int yPos = container.getHeight() / 2 - 240;
        int xPos = container.getWidth() / 2 - 260 - 70;

        g.setColor(Color.white);
        Gui.renderResourceList(g, player.getResources(), xPos, yPos, "Wealth: ", 400);
        yPos += 50;

        g.setColor(shade);
        g.fillRect(xPos - 8, yPos - 8, 312, Attributes.COUNT * UPGRADE_SPACING + 12);

        Attributes attributes = player.getAttributes();
        for (int i = 0; i < Attributes.COUNT; i++) {
            int upgrade = UPGRADE_ORDER[i];

            g.setColor(Color.black);
            g.fillRect(xPos - 2, yPos - 2, 300, 48 + 4);

            if (canAfford(player.getUpgradeCost(upgrade))) {
                upgradeIcons[upgrade].draw(xPos, yPos);
            } else {
                upgradeIcons[upgrade].draw(xPos, yPos, cantAffordColor);
            }

            g.setColor(Color.white);
            Gui.renderResourceList(g, player.getUpgradeCost(upgrade), xPos + 70, yPos + 4, "", 280 - 70);
            if (attributes.levels[upgrade] >= attributes.getMaxLevel(upgrade)) {
                g.setColor(goldColor);
                g.drawString(Attributes.DESCS[upgrade] + ": MAX", xPos + 54, yPos + 31);
            } else {
                g.drawString(Attributes.DESCS[upgrade] + ": " + attributes.levels[upgrade] + " / " + attributes.getMaxLevel(upgrade), xPos + 54, yPos + 31);
            }
            yPos += UPGRADE_SPACING;
        }
        yPos = container.getHeight() / 2 - 240 + 50;
        xPos = container.getWidth() / 2 + 70 / 2;
        g.setColor(shade);
        g.fillRect(xPos - 8, yPos - 8, 312, Attributes.COUNT * UPGRADE_SPACING + 12);
        Artichoke.ArtichokeAttributes artichokeAttributes = artichoke.getAttributes();
        for (int i = 0; i < ARTICHOKE_UPGRADE_ORDER.length; i++) {
            int upgrade = ARTICHOKE_UPGRADE_ORDER[i];

            g.setColor(Color.black);
            g.fillRect(xPos - 2, yPos - 2, 300, 48 + 4);

            if (canAfford(artichoke.getUpgradeCost(upgrade))) {
                artichokeUpgradeIcons[upgrade].draw(xPos, yPos);
            } else {
                artichokeUpgradeIcons[upgrade].draw(xPos, yPos, cantAffordColor);
            }

            g.setColor(Color.white);
            Gui.renderResourceList(g, artichokeAttributes.getUpgradeCost(upgrade), xPos + 70, yPos + 4, "", 280 - 70);
            if (artichokeAttributes.levels[upgrade] >= artichokeAttributes.getMaxLevel(upgrade)) {
                g.setColor(goldColor);
                g.drawString(Artichoke.ArtichokeAttributes.DESCS[upgrade] + ": MAX", xPos + 54, yPos + 31);
            } else {
                g.drawString(Artichoke.ArtichokeAttributes.DESCS[upgrade] + ": " + artichokeAttributes.levels[upgrade] + " / " + artichokeAttributes.getMaxLevel(upgrade), xPos + 54, yPos + 31);
            }
            yPos += UPGRADE_SPACING;
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

}
