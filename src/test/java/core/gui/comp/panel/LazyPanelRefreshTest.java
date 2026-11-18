package core.gui.comp.panel;

import core.gui.RefreshManager;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class LazyPanelRefreshTest {

    @Test
    void lazyPanelUnregistersOnFalse() {
        final var panel = new TestLazyPanel();
        assertUnregisterStopsRefresh(panel::registerRefreshable, panel::needsRefresh, panel::setNeedsRefresh);
    }

    @Test
    void lazyPanelStaysRegisteredWhenRegisteredTwice() {
        final var panel = new TestLazyPanel();
        assertDoubleRegisterKeepsRefresh(panel::registerRefreshable, panel::needsRefresh, panel::setNeedsRefresh);
    }

    @Test
    void lazyImagePanelUnregistersOnFalse() {
        final var panel = new TestLazyImagePanel();
        assertUnregisterStopsRefresh(panel::registerRefreshable, panel::needsRefresh, panel::setNeedsRefresh);
    }

    @Test
    void lazyImagePanelStaysRegisteredWhenRegisteredTwice() {
        final var panel = new TestLazyImagePanel();
        assertDoubleRegisterKeepsRefresh(panel::registerRefreshable, panel::needsRefresh, panel::setNeedsRefresh);
    }

    private static void assertUnregisterStopsRefresh(Consumer<Boolean> register,
                                                     Supplier<Boolean> needsRefresh,
                                                     Consumer<Boolean> setNeedsRefresh) {
        register.accept(true);
        RefreshManager.instance().doRefresh();
        assertThat(needsRefresh.get()).isTrue();

        register.accept(false);
        setNeedsRefresh.accept(false);
        RefreshManager.instance().doRefresh();
        assertThat(needsRefresh.get()).isFalse();
    }

    private static void assertDoubleRegisterKeepsRefresh(Consumer<Boolean> register,
                                                         Supplier<Boolean> needsRefresh,
                                                         Consumer<Boolean> setNeedsRefresh) {
        register.accept(true);
        register.accept(true);
        setNeedsRefresh.accept(false);
        RefreshManager.instance().doRefresh();
        assertThat(needsRefresh.get()).isTrue();

        register.accept(false);
    }

    private static final class TestLazyPanel extends LazyPanel {
        @Override
        protected void initialize() {
            // nothing to build
        }

        @Override
        protected void update() {
            // nothing to update
        }
    }

    private static final class TestLazyImagePanel extends LazyImagePanel {
        @Override
        protected void initialize() {
            // nothing to build
        }

        @Override
        protected void update() {
            // nothing to update
        }
    }
}
