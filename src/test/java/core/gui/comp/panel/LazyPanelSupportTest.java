package core.gui.comp.panel;

import core.gui.RefreshManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.HierarchyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Covers {@link LazyPanelSupport} directly: initialization on first show, reaction to hierarchy
 * changes, and immediate versus deferred refreshes.
 * <p>
 * The tests run headless, where no window can be shown. The component therefore reports its
 * showing state itself, hands {@link core.gui.CursorToolkit} a lightweight root pane container
 * as its top-level ancestor, and receives its hierarchy events directly.
 */
class LazyPanelSupportTest {

    private final List<String> calls = new ArrayList<>();
    private final FakeComponent component = new FakeComponent();
    private boolean failInitialize = false;
    private final LazyPanelSupport support = new LazyPanelSupport(component, this::initialize, this::update);

    @AfterEach
    void unregister() {
        support.registerRefreshable(false);
    }

    @Test
    void initializesOnFirstShowOnly() {
        assertThat(support.isInitialized()).isFalse();

        component.setShowing(true);
        component.setShowing(false);
        component.setShowing(true);

        assertThat(calls).containsExactly("initialize");
        assertThat(support.isInitialized()).isTrue();
    }

    @Test
    void ignoresHierarchyChangesThatDoNotShowTheComponent() {
        component.setShowing(false);
        component.showing = true;
        component.fireParentChanged();

        assertThat(calls).isEmpty();
        assertThat(support.isInitialized()).isFalse();
    }

    @Test
    void defersRefreshWhileHiddenAndUpdatesAfterInitializeOnShow() {
        support.setNeedsRefresh(true);
        assertThat(calls).isEmpty();
        assertThat(support.needsRefresh()).isTrue();

        component.setShowing(true);

        assertThat(calls).containsExactly("initialize", "update");
        assertThat(support.needsRefresh()).isFalse();
    }

    @Test
    void updatesImmediatelyWhenRefreshIsRequestedWhileShowing() {
        component.setShowing(true);
        calls.clear();

        support.setNeedsRefresh(true);

        assertThat(calls).containsExactly("update");
        assertThat(support.needsRefresh()).isFalse();
    }

    @Test
    void clearingTheRefreshFlagDropsADeferredUpdate() {
        support.setNeedsRefresh(true);
        support.setNeedsRefresh(false);

        component.setShowing(true);

        assertThat(calls).containsExactly("initialize");
    }

    @Test
    void refreshManagerUpdatesAShowingComponentRightAway() {
        component.setShowing(true);
        calls.clear();
        support.registerRefreshable(true);

        RefreshManager.instance().doRefresh();

        assertThat(calls).containsExactly("update");
        assertThat(support.needsRefresh()).isFalse();
    }

    @Test
    void refreshManagerDefersTheUpdateOfAHiddenComponentUntilItIsShown() {
        component.setShowing(true);
        component.setShowing(false);
        calls.clear();
        support.registerRefreshable(true);

        RefreshManager.instance().doRefresh();
        assertThat(calls).isEmpty();
        assertThat(support.needsRefresh()).isTrue();

        component.setShowing(true);

        assertThat(calls).containsExactly("update");
        assertThat(support.needsRefresh()).isFalse();
    }

    @Test
    void unregisteringStopsRefreshManagerRefreshes() {
        support.registerRefreshable(true);
        RefreshManager.instance().doRefresh();
        assertThat(support.needsRefresh()).isTrue();

        support.registerRefreshable(false);
        support.setNeedsRefresh(false);
        RefreshManager.instance().doRefresh();

        assertThat(support.needsRefresh()).isFalse();
    }

    @Test
    void registeringTwiceKeepsTheComponentRegistered() {
        support.registerRefreshable(true);
        support.registerRefreshable(true);

        RefreshManager.instance().doRefresh();
        assertThat(support.needsRefresh()).isTrue();

        // one unregister must be enough: the second register did not add a second listener
        support.registerRefreshable(false);
        support.setNeedsRefresh(false);
        RefreshManager.instance().doRefresh();

        assertThat(support.needsRefresh()).isFalse();
    }

    @Test
    void failedInitializeReleasesTheWaitCursorAndIsRetriedOnNextShow() {
        failInitialize = true;

        assertThatThrownBy(() -> component.setShowing(true)).isInstanceOf(IllegalStateException.class);
        assertThat(support.isInitialized()).isFalse();
        assertThat(component.topLevel.getGlassPane().isVisible()).isFalse();

        failInitialize = false;
        component.setShowing(false);
        component.setShowing(true);

        assertThat(calls).containsExactly("initialize", "initialize");
        assertThat(support.isInitialized()).isTrue();
    }

    @Test
    void waitCursorIsActiveWhileTheCallbacksRun() {
        final List<Boolean> glassPaneVisible = new ArrayList<>();
        final var probed = new FakeComponent();
        final var probing = new LazyPanelSupport(probed,
                () -> glassPaneVisible.add(probed.topLevel.getGlassPane().isVisible()),
                () -> glassPaneVisible.add(probed.topLevel.getGlassPane().isVisible()));

        probing.setNeedsRefresh(true);
        probed.setShowing(true);

        assertThat(glassPaneVisible).containsExactly(true, true);
        assertThat(probed.topLevel.getGlassPane().isVisible()).isFalse();
    }

    private void initialize() {
        calls.add("initialize");
        if (failInitialize) {
            throw new IllegalStateException("initialize failed");
        }
    }

    private void update() {
        calls.add("update");
    }

    /**
     * A component that is "showing" when the test says so, inside a fake top-level container.
     */
    private static final class FakeComponent extends JPanel {
        private final FakeTopLevel topLevel = new FakeTopLevel();
        private boolean showing = false;

        @Override
        public boolean isShowing() {
            return showing;
        }

        @Override
        public Container getTopLevelAncestor() {
            return topLevel;
        }

        void setShowing(boolean showing) {
            this.showing = showing;
            fire(new HierarchyEvent(this, HierarchyEvent.HIERARCHY_CHANGED, this, topLevel,
                    HierarchyEvent.SHOWING_CHANGED));
        }

        void fireParentChanged() {
            fire(new HierarchyEvent(this, HierarchyEvent.HIERARCHY_CHANGED, this, topLevel,
                    HierarchyEvent.PARENT_CHANGED));
        }

        private void fire(HierarchyEvent event) {
            for (final var listener : getHierarchyListeners()) {
                listener.hierarchyChanged(event);
            }
        }
    }

    /**
     * Stands in for the window: headless, no real {@link java.awt.Window} can be created.
     */
    private static final class FakeTopLevel extends Container implements RootPaneContainer {
        private final JRootPane rootPane = new JRootPane();

        @Override
        public JRootPane getRootPane() {
            return rootPane;
        }

        @Override
        public void setContentPane(Container contentPane) {
            rootPane.setContentPane(contentPane);
        }

        @Override
        public Container getContentPane() {
            return rootPane.getContentPane();
        }

        @Override
        public void setLayeredPane(JLayeredPane layeredPane) {
            rootPane.setLayeredPane(layeredPane);
        }

        @Override
        public JLayeredPane getLayeredPane() {
            return rootPane.getLayeredPane();
        }

        @Override
        public void setGlassPane(Component glassPane) {
            rootPane.setGlassPane(glassPane);
        }

        @Override
        public Component getGlassPane() {
            return rootPane.getGlassPane();
        }
    }
}
