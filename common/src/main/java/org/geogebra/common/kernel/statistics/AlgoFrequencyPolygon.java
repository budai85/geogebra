/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Creates a frequency polygon.
 * 
 * Input: inputs are identical to AlgoHistogram
 * 
 * Output: PolyLine created from class borders and frequency heights of the
 * histogram generated by the inputs
 * 
 * @author G.Sturr
 */

public class AlgoFrequencyPolygon extends AlgoElement {

	private GeoList list1, list2, list3; // input
	private GeoBoolean isCumulative, useDensity; // input
	private GeoNumeric density;
	private GeoPolyLine outputPolyLine; // output

	private GeoPointND[] points = null;
	private AlgoHistogram algoHistogram;

	private boolean right = false;

	/**
	 * Creates a frequency polygon from two data lists.
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label for the histogram
	 * @param list1
	 *            list of boundaries
	 * @param list2
	 *            list of heights or raw data
	 */
	public AlgoFrequencyPolygon(Construction cons, String label, GeoList list1,
			GeoList list2) {
		this(cons, label, null, list1, list2, null, null, null);
	}

	/**
	 * /** Creates frequency polygon from two data lists with parameter
	 * specifications.
	 * 
	 * @param cons
	 * @param label
	 * @param isCumulative
	 * @param list1
	 * @param list2
	 * @param useDensity
	 * @param density
	 */
	public AlgoFrequencyPolygon(Construction cons, String label,
			GeoBoolean isCumulative, GeoList list1, GeoList list2,
			GeoList list3, GeoBoolean useDensity, GeoNumeric density) {

		this(cons, isCumulative, list1, list2, list3, useDensity, density);
		outputPolyLine.setLabel(label);
	}

	public AlgoFrequencyPolygon(Construction cons, GeoBoolean isCumulative,
			GeoList list1, GeoList list2, GeoList list3, GeoBoolean useDensity,
			GeoNumeric density) {
		super(cons);
		this.list1 = list1;
		this.list2 = list2;
		this.list3 = list3; // optional frequencies
		this.isCumulative = isCumulative;
		this.useDensity = useDensity;
		this.density = density;

		outputPolyLine = new GeoPolyLine(cons, points);

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.FrequencyPolygon;
	}

	@Override
	protected void setInputOutput() {

		ArrayList<GeoElement> tempList = new ArrayList<GeoElement>();

		if (isCumulative != null) {
			tempList.add(isCumulative);
		}
		tempList.add(list1);
		tempList.add(list2);
		if (list3 != null) {
			tempList.add(list3);
		}
		if (useDensity != null) {
			tempList.add(useDensity);
		}
		if (density != null) {
			tempList.add(density);
		}
		input = new GeoElement[tempList.size()];
		input = tempList.toArray(input);

		boolean suppressLabelCreation = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		algoHistogram = new AlgoHistogram(cons, isCumulative, list1, list2,
				list3, useDensity, density, right);

		cons.setSuppressLabelCreation(suppressLabelCreation);

		setOutput();

		// parent of output
		outputPolyLine.setParentAlgorithm(this);
		cons.addToAlgorithmList(this);

		setDependencies(); // done by AlgoElement
	}

	private void setOutput() {
		super.setOutputLength(1);
		super.setOutput(0, outputPolyLine);
	}

	public GeoPolyLine getResult() {
		return outputPolyLine;
	}

	@Override
	public final void compute() {

		// update our histogram to get class borders and y values
		algoHistogram.update();

		if (!algoHistogram.getOutput()[0].isDefined()) {
			outputPolyLine.setUndefined();
			return;
		}
		double[] leftBorder = algoHistogram.getLeftBorder();
		if (leftBorder == null || leftBorder.length < 2) {
			outputPolyLine.setUndefined();
			return;
		}
		double[] yValue = algoHistogram.getValues();
		if (yValue == null || yValue.length < 2) {
			outputPolyLine.setUndefined();
			return;
		}

		// if we got this far everything is ok; now define the polyLine
		outputPolyLine.setDefined();

		// remember old number of points
		int oldPointsLength = points == null ? 0 : points.length;

		// create a new point array
		boolean doCumulative = (isCumulative != null && isCumulative
				.getBoolean());
		int size = doCumulative ? yValue.length : yValue.length + 1;
		points = new GeoPoint[size];

		// create points and load the point array
		boolean suppressLabelCreation = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		if (doCumulative) {
			points[0] = new GeoPoint(cons, null, leftBorder[0], 0.0, 1.0);
			for (int i = 0; i < yValue.length - 1; i++) {
				points[i + 1] = new GeoPoint(cons, null, leftBorder[i + 1],
						yValue[i], 1.0);
			}
		} else {
			double midpoint = leftBorder[0] - 0.5
					* (leftBorder[1] - leftBorder[0]);
			points[0] = new GeoPoint(cons, null, midpoint, 0.0, 1.0);
			for (int i = 0; i < yValue.length - 1; i++) {
				midpoint = 0.5 * (leftBorder[i + 1] + leftBorder[i]);
				points[i + 1] = new GeoPoint(cons, null, midpoint, yValue[i],
						1.0);
			}
			midpoint = 1.5 * leftBorder[yValue.length - 1] - .5
					* (leftBorder[yValue.length - 2]);
			points[yValue.length] = new GeoPoint(cons, null, midpoint, 0.0, 1.0);
		}
		cons.setSuppressLabelCreation(suppressLabelCreation);

		// update the polyLine
		outputPolyLine.setPoints(points);
		if (oldPointsLength != points.length)
			setOutput();

	}

	// TODO Consider locusequability

}
