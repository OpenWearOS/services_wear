package org.openwearos.server.modes;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Combines mode flags into the effective value of features.
 * shortcut: synchronous, appliers run under the lock; make appliers async when one gets slow.
 */
public final class ModeEngine {
    /** Decides when a feature is forced. */
    public interface Policy {
        /** Ids of the modes that can force this feature. */
        Set<String> modes();

        /** The forced value for the current mode states, or null for "not forced". */
        Object forced(Map<String, Object> modeStates);
    }

    /** Forces {@code value} while any of {@code modeIds} is Boolean.TRUE. */
    public static Policy forcedWhenAny(Object value, String... modeIds) {
        final Set<String> ids = Set.of(modeIds);
        return new Policy() {
            @Override public Set<String> modes() { return ids; }

            @Override public Object forced(Map<String, Object> s) {
                for (String id : ids) if (Boolean.TRUE.equals(s.get(id))) return value;
                return null;
            }
        };
    }

    private static final class Feature {
        final Policy policy;
        final Consumer<Object> applier;
        Object own;          // what the user chose
        Object effective;    // what is applied now
        boolean applied;

        Feature(Policy policy, Consumer<Object> applier, Object own) {
            this.policy = policy;
            this.applier = applier;
            this.own = own;
        }
    }

    private final Map<String, Object> mModes = new HashMap<>();
    private final Map<String, Feature> mFeatures = new LinkedHashMap<>();

    public synchronized void addFeature(String id, Object own, Policy policy, Consumer<Object> applier) {
        Feature f = new Feature(policy, applier, own);
        mFeatures.put(id, f);
        refresh(f);
    }

    public synchronized void setMode(String id, Object value) {
        if (Objects.equals(mModes.put(id, value), value)) return;
        for (Feature f : mFeatures.values()) if (f.policy.modes().contains(id)) refresh(f);
    }

    /** The user changed the feature; takes effect immediately unless a mode is forcing it. */
    public synchronized void setOwn(String id, Object value) {
        Feature f = feature(id);
        f.own = value;
        refresh(f);
    }

    public synchronized Object effective(String id) {
        return feature(id).effective;
    }

    public synchronized Object own(String id) {
        return feature(id).own;
    }

    private Feature feature(String id) {
        Feature f = mFeatures.get(id);
        if (f == null) throw new IllegalArgumentException("unknown feature " + id);
        return f;
    }

    private void refresh(Feature f) {
        Object forced = f.policy.forced(mModes);
        Object v = forced != null ? forced : f.own;
        if (f.applied && Objects.equals(v, f.effective)) return;
        f.effective = v;
        f.applied = true;
        f.applier.accept(v);
    }
}
