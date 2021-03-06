package org.geogebra.common.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Before;
import org.junit.Test;

public class SelectionManagerTest extends BaseUnitTest {

	private SelectionManager selectionManager;

	@Before
	public void setupTest() {
		selectionManager = getApp().getSelectionManager();
	}

	@Test
	public void hasNextShouldSkipInvisibleGeos() {
		createSampleGeos();
		assertTrue(selectionManager.hasNext(lookup("firstVisible")));
		assertFalse(selectionManager.hasNext(lookup("lastVisible")));
	}

	private void createSampleGeos() {
		getApp().getGgbApi().setPerspective("G");
		add("firstVisible:(1,1)");
		add("lastVisible:(2,1)");
		GeoElement hidden = add("hidden:(3,1)");
		hidden.setEuclidianVisible(false);
		GeoElement notSelectable = add("notSelectable:(4,1)");
		notSelectable.setSelectionAllowed(false);
	}

	@Test
	public void selectNextShouldSkipInvisibleGeos() {
		getApp().getGgbApi().setPerspective("G");
		createSampleGeos();

		selectionManager.setSelectedGeos(null);
		GeoElement firstVisible = lookup("firstVisible");
		selectionManager.addSelectedGeo(firstVisible);
		// next jumps to second
		selectionManager.selectNextGeo();
		assertTrue(lookup("lastVisible").isSelected());
		// next does not select anything
		assertFalse(selectionManager.selectNextGeo());
		assertEquals(0, selectionManager.selectedGeosSize());
	}
}
