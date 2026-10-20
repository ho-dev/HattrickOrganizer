package core.option;

import core.model.TranslationFacility;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
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

    public enum NameFormat {
        HATTRICK,
        FULL_FIRST_LAST,
        FULL_LAST_FIRST,
        SMART_FIRST_LAST,
        SMART_LAST_FIRST
    }

    private static class NameFormatRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
            JList<?> list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);

            if (value instanceof NameFormat format) {
                label.setText(getNameFormatLabel(format));
            }

            return label;
        }
    }

    private static String getNameFormatLabel(NameFormat nameFormat) {
        return TranslationFacility.tr(getTranslationKey(nameFormat));
    }

    static String getTranslationKey(NameFormat nameFormat) {
        return switch (nameFormat) {
            case HATTRICK -> "SmartPlayerNamesPanel.NameFormat.HATTRICK";
            case FULL_FIRST_LAST -> "SmartPlayerNamesPanel.NameFormat.FULL_FIRST_LAST";
            case FULL_LAST_FIRST -> "SmartPlayerNamesPanel.NameFormat.FULL_LAST_FIRST";
            case SMART_FIRST_LAST -> "SmartPlayerNamesPanel.NameFormat.SMART_FIRST_LAST";
            case SMART_LAST_FIRST -> "SmartPlayerNamesPanel.NameFormat.SMART_LAST_FIRST";
        };
    }

    public enum Group {
        SQUAD,
        LINEUP,
        MATCHES,
        TRAINING,
        TRANSFERS,
        YOUTH,
        BEST_WORST_TEAM,
        SPECIAL_EVENTS,
        TEAM_ANALYZER,
        STATISTICS_PLAYER,
        PLAYER_ANALYSIS,
        HALL_OF_FAME
    }

    private static String getGroupLabel(Group group) {
        return TranslationFacility.tr(getTranslationKey(group));
    }

    static String getTranslationKey(Group group) {
        return switch (group) {
            case SQUAD -> "SmartPlayerNamesPanel.Group.Squad";
            case LINEUP -> "SmartPlayerNamesPanel.Group.Lineup";
            case MATCHES -> "SmartPlayerNamesPanel.Group.Matches";
            case TRAINING -> "SmartPlayerNamesPanel.Group.Training";
            case TRANSFERS -> "SmartPlayerNamesPanel.Group.Transfers";
            case YOUTH -> "SmartPlayerNamesPanel.Group.Youth";
            case BEST_WORST_TEAM -> "SmartPlayerNamesPanel.Group.BestWorstTeam";
            case SPECIAL_EVENTS -> "SmartPlayerNamesPanel.Group.SpecialEvents";
            case TEAM_ANALYZER -> "SmartPlayerNamesPanel.Group.TeamAnalyzer";
            case STATISTICS_PLAYER -> "SmartPlayerNamesPanel.Group.Statistics_Player";
            case PLAYER_ANALYSIS -> "SmartPlayerNamesPanel.Group.Player_Analysis";
            case HALL_OF_FAME -> "SmartPlayerNamesPanel.Group.HallOfFame";
        };
    }

    /** Every configurable use of a player name, grouped by application area. */
    public enum Location {
        SQUAD_NAME_COLUMN,
        SQUAD_PLAYER_DETAILS,
        LINEUP_POSITIONS,
        LINEUP_SUBSTITUTES,
        LINEUP_PLAYER_LIST,
        MATCHES_FORMATION,
        PLAYER_ANALYSIS_MATCHES,
        PLAYER_ANALYSIS_PLAYER_COMPARE,
        STATISTICS_PLAYER_PLAYER_SELECTION,
        TRANSFERS_TRANSFER_LIST,
        TRAINING_TRAINING,
        TRAINING_PREDICTION,
        TRAINING_ANALYZER,
        TEAM_ANALYSER_SPECIAL_EVENTS,
        TEAM_ANALYSER_FIELD,
        SPECIAL_EVENTS_SELECTION,
        SPECIAL_EVENTS_PLAYER_COLUMN,
        BEST_WORST_TEAM,
        YOUTH_PLAYER,
        HALL_OF_FAME_PLAYER_LIST;
    }

    private static Group getGroup(Location location) {
        return switch (location) {
            case SQUAD_NAME_COLUMN, SQUAD_PLAYER_DETAILS -> Group.SQUAD;
            case LINEUP_POSITIONS, LINEUP_SUBSTITUTES, LINEUP_PLAYER_LIST -> Group.LINEUP;
            case MATCHES_FORMATION -> Group.MATCHES;
            case PLAYER_ANALYSIS_MATCHES, PLAYER_ANALYSIS_PLAYER_COMPARE -> Group.PLAYER_ANALYSIS;
            case STATISTICS_PLAYER_PLAYER_SELECTION -> Group.STATISTICS_PLAYER;
            case TRANSFERS_TRANSFER_LIST -> Group.TRANSFERS;
            case TRAINING_TRAINING, TRAINING_PREDICTION, TRAINING_ANALYZER -> Group.TRAINING;
            case TEAM_ANALYSER_SPECIAL_EVENTS, TEAM_ANALYSER_FIELD -> Group.TEAM_ANALYZER;
            case SPECIAL_EVENTS_SELECTION, SPECIAL_EVENTS_PLAYER_COLUMN -> Group.SPECIAL_EVENTS;
            case BEST_WORST_TEAM -> Group.BEST_WORST_TEAM;
            case YOUTH_PLAYER -> Group.YOUTH;
            case HALL_OF_FAME_PLAYER_LIST -> Group.HALL_OF_FAME;
        };
    }

    private static String getLocationLabel(Location location) {
        return TranslationFacility.tr(getTranslationKey(location));
    }

    static String getTranslationKey(Location location) {
        return switch (location) {
            case SQUAD_NAME_COLUMN -> "SmartPlayerNamesPanel.Location.SQUAD_NAME_COLUMN";
            case SQUAD_PLAYER_DETAILS -> "SmartPlayerNamesPanel.Location.SQUAD_PLAYER_DETAILS";
            case LINEUP_POSITIONS -> "SmartPlayerNamesPanel.Location.LINEUP_POSITIONS";
            case LINEUP_SUBSTITUTES -> "SmartPlayerNamesPanel.Location.LINEUP_SUBSTITUTES";
            case LINEUP_PLAYER_LIST -> "SmartPlayerNamesPanel.Location.LINEUP_PLAYER_LIST";
            case MATCHES_FORMATION -> "SmartPlayerNamesPanel.Location.MATCHES_FORMATION";
            case PLAYER_ANALYSIS_MATCHES -> "SmartPlayerNamesPanel.Location.PLAYER_ANALYSIS_MATCHES";
            case PLAYER_ANALYSIS_PLAYER_COMPARE -> "SmartPlayerNamesPanel.Location.PLAYER_ANALYSIS_PLAYER_COMPARE";
            case STATISTICS_PLAYER_PLAYER_SELECTION -> "SmartPlayerNamesPanel.Location.STATISTICS_PLAYER_PLAYER_SELECTION";
            case TRANSFERS_TRANSFER_LIST -> "SmartPlayerNamesPanel.Location.TRANSFERS_TRANSFER_LIST";
            case TRAINING_TRAINING -> "SmartPlayerNamesPanel.Location.TRAINING_TRAINING";
            case TRAINING_PREDICTION -> "SmartPlayerNamesPanel.Location.TRAINING_PREDICTION";
            case TRAINING_ANALYZER -> "SmartPlayerNamesPanel.Location.TRAINING_ANALYZER";
            case TEAM_ANALYSER_SPECIAL_EVENTS -> "SmartPlayerNamesPanel.Location.TEAM_ANALYSER_SPECIAL_EVENTS";
            case TEAM_ANALYSER_FIELD -> "SmartPlayerNamesPanel.Location.TEAM_ANALYSER_FIELD";
            case SPECIAL_EVENTS_SELECTION -> "SmartPlayerNamesPanel.Location.SPECIAL_EVENTS_SELECTION";
            case SPECIAL_EVENTS_PLAYER_COLUMN -> "SmartPlayerNamesPanel.Location.SPECIAL_EVENTS_PLAYER_COLUMN";
            case BEST_WORST_TEAM -> "SmartPlayerNamesPanel.Location.BEST_WORST_TEAM";
            case YOUTH_PLAYER -> "SmartPlayerNamesPanel.Location.YOUTH_PLAYER";
            case HALL_OF_FAME_PLAYER_LIST -> "SmartPlayerNamesPanel.Location.HALL_OF_FAME_PLAYER_LIST";
        };
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

        defaultFormatBox.setRenderer(new NameFormatRenderer());
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
            final var group = getGroup(location);
            if (group != previousGroup) {
                addGroupHeader(rows, c, group);
                previousGroup = group;
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
        JLabel label = new JLabel(getGroupLabel(group));
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
                .map(nameFormat -> TranslationFacility.tr("SmartPlayerNamesPanel.Use_default", getNameFormatLabel(nameFormat)))
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
        JLabel label = new JLabel(getLocationLabel(location));
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
                    ? TranslationFacility.tr("SmartPlayerNamesPanel.Use_default", getNameFormatLabel(getDefaultFormat()))
                    : getNameFormatLabel(choice.format));
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
