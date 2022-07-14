package net.deceivedfx.plugins.runecraft;

import com.openosrs.client.ui.overlay.components.table.TableAlignment;
import com.openosrs.client.ui.overlay.components.table.TableComponent;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ColorUtil;
import net.unethicalite.api.commons.Time;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

@Slf4j
@Singleton
class SneakyRunecrafterOverlay extends OverlayPanel {

    private final Client client;
    private final SneakyRunecrafterPlugin plugin;
    private final SneakyRunecrafterConfig config;

    String timeFormat;
    private String infoStatus = "Starting...";
    private int suppliesCost = 155000; // ESTIMATE

    @Inject
    private SneakyRunecrafterOverlay(final Client client, final SneakyRunecrafterPlugin plugin, final SneakyRunecrafterConfig config)
    {
        super(plugin);
        setPosition(OverlayPosition.BOTTOM_LEFT);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Runecrafter Overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {

        if (!config.showOverlay())
        {
            log.debug("Overlay conditions not met, not starting overlay");
            return null;
        }


        TableComponent tableComponent = new TableComponent();
        tableComponent.setColumnAlignments(TableAlignment.LEFT, TableAlignment.RIGHT);

        //tableComponent.addRow("Active:", String.valueOf(plugin.run));


        tableComponent.addRow("Status:", SneakyRunecrafterPlugin.status);

        if (plugin.isRunning()) {
            Duration duration = Duration.between(plugin.botTimer, Instant.now());
            timeFormat = (duration.toHours() < 1) ? "mm:ss" : "HH:mm:ss";
            tableComponent.addRow("Time running:", Time.format(duration));
            tableComponent.addRow("Runes crafted:", String.valueOf(plugin.runesCrafted) + " (" + String.valueOf(plugin.getRunesPH() + ")"));
            tableComponent.addRow("Levels gained:", String.valueOf((client.getRealSkillLevel(Skill.RUNECRAFT)-plugin.initialLevel)));
            if (duration.toSeconds() != 0) {
                double hoursIn = duration.toSeconds()*0.000277777778;
                tableComponent.addRow("Gp/hr:", String.valueOf(Math.floor((plugin.mudRunePrice * plugin.runesCrafted / hoursIn)-suppliesCost)));
            } else {
                tableComponent.addRow("Gp/hr:", "N/A");
                tableComponent.addRow("Xp/hr:", "N/A");
            }
        } else {
            tableComponent.addRow("Time running:", "00:00");
            tableComponent.addRow("Runes crafted:", "N/A");
            tableComponent.addRow("Levels gained:", "N/A");
            tableComponent.addRow("Gp/hr:", "N/A");
            tableComponent.addRow("Xp/hr:", "N/A");
        }

        if (!tableComponent.isEmpty())
        {
            panelComponent.setBackgroundColor(ColorUtil.fromHex("#B3121212")); //Material Dark default
            panelComponent.setPreferredSize(new Dimension(250, 200));
            panelComponent.setBorder(new Rectangle(5, 5, 5, 5));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Sneaky Runecrafter")
                    .color(ColorUtil.fromHex("#40C4FF"))
                    .build());
            if (plugin.run) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Active:")
                        .right(String.valueOf(plugin.run))
                        .rightColor(Color.GREEN)
                        .build());
            } else {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Active:")
                        .right(String.valueOf(plugin.isRunning()))
                        .rightColor(Color.RED)
                        .build());
            }
            panelComponent.getChildren().add(tableComponent);
            /*
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Marks of grace")
                    .color(ColorUtil.fromHex("#FFA000"))
                    .build());
            //panelComponent.getChildren().add(tableMarksComponent);
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Delays")
                    .color(ColorUtil.fromHex("#F8BBD0"))
                    .build());
            //panelComponent.getChildren().add(tableDelayComponent);
             */

        }
        return super.render(graphics);
    }
}
