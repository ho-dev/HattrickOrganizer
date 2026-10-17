package core.option;

import core.model.TranslationFacility;
import lombok.RequiredArgsConstructor;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Preferences panel for the player name formats used throughout HO.
 *
 * <p>This class deliberately does not persist settings. The surrounding
 * options dialog can call {@link #load(Settings)} when opening and
 * {@link #snapshot()} only when the user confirms the dialog. As with all
 * Swing components, call its public methods on the Event Dispatch Thread.</p>
 */
public final class SmartPlayerNamesPanel extends JPanel {

    @RequiredArgsConstructor
    public enum NameFormat {
        HATTRICK("SmartPlayerNamesPanel.NameFormat.HATTRICK"),
        FULL_FIRST_LAST("SmartPlayerNamesPanel.NameFormat.FULL_FIRST_LAST"),
        FULL_LAST_FIRST("SmartPlayerNamesPanel.NameFormat.FULL_LAST_FIRST"),
        SMART_FIRST_LAST("SmartPlayerNamesPanel.NameFormat.SMART_FIRST_LAST"),
        SMART_LAST_FIRST("SmartPlayerNamesPanel.NameFormat.SMART_LAST_FIRST");

        private final String translationKey;

        @Override
        public String toString() {
            return TranslationFacility.tr(translationKey);
        }
    }

    @RequiredArgsConstructor
    public enum Group {
        SQUAD("SmartPlayerNamesPanel.Group.Squad"),
        LINEUP("SmartPlayerNamesPanel.Group.Lineup"),
        MATCHES("SmartPlayerNamesPanel.Group.Matches"),
        TRAINING("SmartPlayerNamesPanel.Group.Training"),
        TRANSFERS("SmartPlayerNamesPanel.Group.Transfers"),
        YOUTH("SmartPlayerNamesPanel.Group.Youth"),
        BEST_WORST_TEAM("SmartPlayerNamesPanel.Group.BestWorstTeam"),
        SPECIAL_EVENTS("SmartPlayerNamesPanel.Group.SpecialEvents"),
        TEAM_ANALYZER("SmartPlayerNamesPanel.Group.TeamAnalyzer"),
        STATISTICS_PLAYER("SmartPlayerNamesPanel.Group.Statistics_Player"),
        PLAYER_ANALYSIS("SmartPlayerNamesPanel.Group.Player_Analysis"),
        HALL_OF_FAME("SmartPlayerNamesPanel.Group.HallOfFame"),
        ;

        private final String translationKey;

        @Override
        public String toString() {
            return TranslationFacility.tr(translationKey);
        }
    }

    /** Every configurable use of a player name, grouped by application area. */
    public enum Location {
        SQUAD_NAME_COLUMN(Group.SQUAD, "SmartPlayerNamesPanel.Location.SQUAD_NAME_COLUMN"),
        SQUAD_PLAYER_DETAILS(Group.SQUAD, "SmartPlayerNamesPanel.Location.SQUAD_PLAYER_DETAILS"),
        LINEUP_POSITIONS(Group.LINEUP, "SmartPlayerNamesPanel.Location.LINEUP_POSITIONS"),
        LINEUP_SUBSTITUTES(Group.LINEUP, "SmartPlayerNamesPanel.Location.LINEUP_SUBSTITUTES"),
        LINEUP_PLAYER_LIST(Group.LINEUP, "SmartPlayerNamesPanel.Location.LINEUP_PLAYER_LIST"),
        MATCHES_FORMATION(Group.MATCHES, "SmartPlayerNamesPanel.Location.MATCHES_FORMATION"),
        PLAYER_ANALYSIS_MATCHES(Group.PLAYER_ANALYSIS, "SmartPlayerNamesPanel.Location.PLAYER_ANALYSIS_MATCHES"),
        PLAYER_ANALYSIS_PLAYER_COMPARE(Group.PLAYER_ANALYSIS, "SmartPlayerNamesPanel.Location.PLAYER_ANALYSIS_PLAYER_COMPARE"),
        STATISTICS_PLAYER_PLAYER_SELECTION(Group.STATISTICS_PLAYER, "SmartPlayerNamesPanel.Location.STATISTICS_PLAYER_PLAYER_SELECTION"),
        TRANSFERS_TRANSFER_LIST(Group.TRANSFERS, "SmartPlayerNamesPanel.Location.TRANSFERS_TRANSFER_LIST"),
        TRAINING_TRAINING(Group.TRAINING, "SmartPlayerNamesPanel.Location.TRAINING_TRAINING"),
        TRAINING_PREDICTION(Group.TRAINING, "SmartPlayerNamesPanel.Location.TRAINING_PREDICTION"),
        TRAINING_ANALYZER(Group.TRAINING, "SmartPlayerNamesPanel.Location.TRAINING_ANALYZER"),
        TEAM_ANALYSER_SPECIAL_EVENTS(Group.TEAM_ANALYZER, "SmartPlayerNamesPanel.Location.TEAM_ANALYSER_SPECIAL_EVENTS"),
        TEAM_ANALYSER_FIELD(Group.TEAM_ANALYZER, "SmartPlayerNamesPanel.Location.TEAM_ANALYSER_FIELD"),
        SPECIAL_EVENTS_SELECTION(Group.SPECIAL_EVENTS, "SmartPlayerNamesPanel.Location.SPECIAL_EVENTS_SELECTION"),
        SPECIAL_EVENTS_PLAYER_COLUMN(Group.SPECIAL_EVENTS, "SmartPlayerNamesPanel.Location.SPECIAL_EVENTS_PLAYER_COLUMN"),
        BEST_WORST_TEAM(Group.BEST_WORST_TEAM, "SmartPlayerNamesPanel.Location.BEST_WORST_TEAM"),
        YOUTH_PLAYER(Group.YOUTH, "SmartPlayerNamesPanel.Location.YOUTH_PLAYER"),
        HALL_OF_FAME_PLAYER_LIST(Group.HALL_OF_FAME, "SmartPlayerNamesPanel.Location.HALL_OF_FAME_PLAYER_LIST");

        private final Group group;
        private final String translationKey;

        Location(Group group, String translationKey) {
            this.group = group;
            this.translationKey = translationKey;
        }

        public Group group() {
            return group;
        }

        public String label() {
            return TranslationFacility.tr(translationKey);
        }
    }

    /**
     * Immutable snapshot. An absent entry in {@code overrides} means the
     * location follows {@code defaultFormat}.
     */
    public record Settings(NameFormat defaultFormat, Map<Location, NameFormat> overrides) {
        public Settings {
            Objects.requireNonNull(defaultFormat, "defaultFormat");
            Objects.requireNonNull(overrides, "overrides");
            EnumMap<Location, NameFormat> copy = new EnumMap<>(Location.class);
            overrides.forEach((location, format) ->
                copy.put(Objects.requireNonNull(location), Objects.requireNonNull(format)));
            overrides = Collections.unmodifiableMap(copy);
        }

        public NameFormat effectiveFormat(Location location) {
            return overrides.getOrDefault(Objects.requireNonNull(location), defaultFormat);
        }
    }

    /** The extra choice is available only for an individual location. */
    private enum Choice {
        USE_DEFAULT(null),
        HATTRICK(NameFormat.HATTRICK),
        FULL_FIRST_LAST(NameFormat.FULL_FIRST_LAST),
        FULL_LAST_FIRST(NameFormat.FULL_LAST_FIRST),
        SMART_FIRST_LAST(NameFormat.SMART_FIRST_LAST),
        SMART_LAST_FIRST(NameFormat.SMART_LAST_FIRST);

        private final NameFormat format;

        Choice(NameFormat format) {
            this.format = format;
        }

        static Choice fromFormat(NameFormat format) {
            if (format == null) {
                return USE_DEFAULT;
            }
            for (Choice choice : values()) {
                if (choice.format == format) {
                    return choice;
                }
            }
            throw new IllegalArgumentException("Unknown name format: " + format);
        }
    }

    private final JComboBox<NameFormat> defaultFormatBox = new JComboBox<>(NameFormat.values());
    private final EnumMap<Location, JComboBox<Choice>> locationBoxes =
        new EnumMap<>(Location.class);
    private final JLabel previewLabel = new JLabel();

    public SmartPlayerNamesPanel() {
        super(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(createDefaultSettingsPanel(), BorderLayout.NORTH);
        add(createLocationsPanel(), BorderLayout.CENTER);

        defaultFormatBox.setSelectedItem(NameFormat.HATTRICK);
        defaultFormatBox.addActionListener(event -> {
            updatePreview();
            locationBoxes.values().forEach(JComponent::repaint);
        });
        updatePreview();
    }

    private JPanel createDefaultSettingsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 6, 0);

        JLabel title = new JLabel(TranslationFacility.tr("SmartPlayerNamesPanel.PlayerNameDisplay"));
        title.setFont(title.getFont().deriveFont(Font.BOLD, title.getFont().getSize2D() + 2f));
        panel.add(title, c);

        c.gridy++;
        c.insets = new Insets(0, 0, 12, 0);
        panel.add(new JLabel(TranslationFacility.tr("SmartPlayerNamesPanel.ChooseDefaultFormat")), c);

        c.gridy++;
        c.gridwidth = 1;
        c.weightx = 0;
        c.insets = new Insets(0, 0, 0, 12);
        JLabel defaultLabel = new JLabel(TranslationFacility.tr("SmartPlayerNamesPanel.DefaultNameFormat"));
        defaultLabel.setLabelFor(defaultFormatBox);
        panel.add(defaultLabel, c);

        c.gridx = 1;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 0, 0);
        defaultFormatBox.setPrototypeDisplayValue(NameFormat.FULL_FIRST_LAST);
        panel.add(defaultFormatBox, c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        c.insets = new Insets(8, 0, 0, 0);
        panel.add(previewLabel, c);
        return panel;
    }

    private JPanel createLocationsPanel() {
        JPanel container = new JPanel(new BorderLayout(0, 8));
        JPanel header = new JPanel(new BorderLayout());

        JLabel title = new JLabel(TranslationFacility.tr("SmartPlayerNamesPanel.IndividualLocations"));
        title.setFont(title.getFont().deriveFont(Font.BOLD));
        header.add(title, BorderLayout.WEST);

        JButton resetButton = new JButton(TranslationFacility.tr("SmartPlayerNamesPanel.ResetOverrides"));
        resetButton.setToolTipText(TranslationFacility.tr("SmartPlayerNamesPanel.MakeEveryLocationUseTheDefaultNameFormat"));
        resetButton.addActionListener(event -> clearOverrides());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.add(resetButton);
        header.add(buttonPanel, BorderLayout.EAST);
        container.add(header, BorderLayout.NORTH);

        JPanel rows = new JPanel(new GridBagLayout());
        rows.setBorder(new EmptyBorder(6, 10, 10, 10));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1;

        Group previousGroup = null;
        for (Location location : Location.values()) {
            if (!location.group().equals(previousGroup)) {
                addGroupHeader(rows, c, location.group());
                previousGroup = location.group();
            }
            addLocationRow(rows, c, location);
        }

        // Stretch rows to the top if the viewport is taller than the content.
        c.gridx = 0;
        c.gridwidth = 2;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 0, 0, 0);
        rows.add(new JPanel(), c);

        JScrollPane scrollPane = new JScrollPane(rows);
        scrollPane.setBorder(BorderFactory.createEtchedBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        scrollPane.setPreferredSize(new Dimension(620, 415));
        container.add(scrollPane, BorderLayout.CENTER);
        return container;
    }

    private void addGroupHeader(JPanel rows, GridBagConstraints c, Group group) {
        c.gridx = 0;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(c.gridy == 0 ? 4 : 14, 0, 4, 0);

        JPanel header = new JPanel(new BorderLayout(10, 0));
        JLabel label = new JLabel(group.toString());
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        header.add(label, BorderLayout.WEST);
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        header.add(separator, BorderLayout.CENTER);
        rows.add(header, c);
        c.gridy++;
    }

    private void addLocationRow(JPanel rows, GridBagConstraints c, Location location) {
        JComboBox<Choice> box = new JComboBox<>(Choice.values());
        box.setSelectedItem(Choice.USE_DEFAULT);
        box.setRenderer(new ChoiceRenderer());
        box.setPrototypeDisplayValue(Choice.FULL_FIRST_LAST);

        final var fontMetrics = box.getFontMetrics(box.getFont());
        final int maxNameFormatWidth =
            Stream.of(NameFormat.values())
                .map(nameFormat -> TranslationFacility.tr("SmartPlayerNamesPanel.Use_default", nameFormat))
                .mapToInt(fontMetrics::stringWidth)
                .max()
                .orElseThrow();
        final int comboWidth = maxNameFormatWidth + 48;
        box.setPreferredSize(new Dimension(comboWidth, box.getPreferredSize().height));
        locationBoxes.put(location, box);

        c.gridx = 0;
        c.gridwidth = 1;
        c.weightx = 1;
        c.insets = new Insets(3, 12, 3, 16);
        JLabel label = new JLabel(location.label());
        label.setLabelFor(box);
        rows.add(label, c);

        c.gridx = 1;
        c.weightx = 0;
        c.insets = new Insets(3, 0, 3, 0);
        rows.add(box, c);
        c.gridy++;
    }

    private final class ChoiceRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean selected, boolean hasFocus) {
            JLabel result = (JLabel) super.getListCellRendererComponent(
                list, value, index, selected, hasFocus);
            if (value instanceof Choice choice) {
                result.setText(choice == Choice.USE_DEFAULT
                    ? TranslationFacility.tr("SmartPlayerNamesPanel.Use_default", getDefaultFormat())
                    : choice.format.toString());
            }
            return result;
        }
    }

    private void updatePreview() {
        String example = switch (getDefaultFormat()) {
            case HATTRICK -> "Cristiano Ronaldo";
            case FULL_FIRST_LAST -> "Cristiano 'CR7' Ronaldo";
            case FULL_LAST_FIRST -> "Ronaldo, Cristiano 'CR7'";
            case SMART_FIRST_LAST -> "C. Ronaldo";
            case SMART_LAST_FIRST -> "Ronaldo, C.";
        };
        previewLabel.setText(TranslationFacility.tr("SmartPlayerNamesPanel.Example", example));
        previewLabel.setToolTipText(
            TranslationFacility.tr("SmartPlayerNamesPanel.Smart_names_are_determined_using_the_other_players_in_the_relevant_group"));
    }

    public NameFormat getDefaultFormat() {
        return (NameFormat) defaultFormatBox.getSelectedItem();
    }

    public void setDefaultFormat(NameFormat format) {
        defaultFormatBox.setSelectedItem(Objects.requireNonNull(format, "format"));
        updatePreview();
        locationBoxes.values().forEach(JComponent::repaint);
    }

    /** Returns the explicit override, or null if the location uses the default. */
    public NameFormat getOverride(Location location) {
        JComboBox<Choice> box = locationBoxes.get(Objects.requireNonNull(location));
        return ((Choice) Objects.requireNonNull(box.getSelectedItem())).format;
    }

    /** Pass null to make this location use the default format. */
    public void setOverride(Location location, NameFormat format) {
        locationBoxes.get(Objects.requireNonNull(location)).setSelectedItem(
            Choice.fromFormat(format));
    }

    public NameFormat getEffectiveFormat(Location location) {
        NameFormat override = getOverride(location);
        return override != null ? override : getDefaultFormat();
    }

    /** Clears individual overrides without changing the global default. */
    public void clearOverrides() {
        locationBoxes.values().forEach(box -> box.setSelectedItem(Choice.USE_DEFAULT));
    }

    /** Loads settings for editing; nothing is persisted until the caller saves. */
    public void load(Settings settings) {
        Objects.requireNonNull(settings, "settings");
        setDefaultFormat(settings.defaultFormat());
        for (Location location : Location.values()) {
            setOverride(location, settings.overrides().get(location));
        }
    }

    /** Gets a detached snapshot suitable for persistence by the options dialog. */
    public Settings snapshot() {
        EnumMap<Location, NameFormat> overrides = new EnumMap<>(Location.class);
        for (Location location : Location.values()) {
            NameFormat format = getOverride(location);
            if (format != null) {
                overrides.put(location, format);
            }
        }
        return new Settings(getDefaultFormat(), overrides);
    }
}
