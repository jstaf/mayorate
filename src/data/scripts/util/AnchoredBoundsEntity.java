package data.scripts.util;

import com.fs.starfarer.api.combat.BoundsAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.entities.AnchoredEntity;
import org.lwjgl.util.vector.Vector2f;

/**
 * Created by jeff on 19/08/15.
 */
public class AnchoredBoundsEntity extends AnchoredEntity {

    /**
     * Creates a {@code CombatEntityAPI} that follows and rotates with another
     * anchoring {@code CombatEntityAPI}. Only difference is this entity has actual bounds and collision radius.
     *
     * @param anchor   The {@link CombatEntityAPI} to follow and rotate with.
     * @param location The location relative to {@code anchor} to track.
     *                 <p/>
     * @since 1.5
     */
    public AnchoredBoundsEntity(CombatEntityAPI anchor, Vector2f location) {
        super(anchor, location);
    }

    /**
     * @return Returns parent's bounds.
     */
    @Override
    public BoundsAPI getExactBounds() {
        return(anchor.getExactBounds());
    }
}
