package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.Matrix.CoordMatrix4x4;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoDirectionND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra3D.archimedean.support.ArchimedeanSolidFactory;
import geogebra3D.archimedean.support.IArchimedeanSolid;
import geogebra3D.archimedean.support.IFace;

/**
 * @author ggb3D
 * 
 * Creates a new GeoPolyhedron
 *
 */
public class AlgoArchimedeanSolid extends AlgoPolyhedron{

	
	
	private GeoPointND A, B;
	private GeoDirectionND v;
	
	protected CoordMatrix4x4 matrix;


	
	private Coords[] coords;
	
	
	private String name, className;
	
	
	
	/** creates an archimedean solid
	 * @param c construction 
	 * @param labels
	 * @param A 
	 * @param B 
	 * @param v 
	 * @param name 
	 */
	public AlgoArchimedeanSolid(Construction c, String[] labels, 
			GeoPointND A, GeoPointND B, GeoDirectionND v,
			String name) {
		super(c);
		
		this.name = name;
		this.className = "Algo"+name;

		outputPolyhedron.adjustOutputSize(1);
		GeoPolyhedron polyhedron = outputPolyhedron.getElement(0);

		this.A=A;
		this.B=B;
		this.v=v;
		
		matrix = new CoordMatrix4x4();

		createPolyhedron(polyhedron);
		
		compute();
		
		// input 
		setInput();	
		addAlgoToInput();
		
		polyhedron.updateFaces();
		polyhedron.setReverseNormals();
		setOutput();
		
		
        
        polyhedron.initLabels(labels);
        
        update();
	}
	
	
	protected GeoPointND getA(){
		return A;
	}
	
	protected GeoPointND getB(){
		return B;
	}
	
	protected Coords getDirection(){
		return v.getDirectionInD3();
	}
	
	
	protected void setInput(){
		input = new GeoElement[3];
		input[0]=(GeoElement) A;
		input[1]=(GeoElement) B;
		input[2]=(GeoElement) v;
		
	}
	
	
	/**
	 * create the polyhedron (faces and edges)
	 * @param polyhedron
	 */
	protected void createPolyhedron(GeoPolyhedron polyhedron) {
		
		IArchimedeanSolid solid = ArchimedeanSolidFactory.create(name);
		int vertexCount = solid.getVertexCount();
		
		outputPoints.augmentOutputSize(vertexCount-2);
		outputPoints.setLabels(null);
		
		
		//coords
		coords = solid.getVerticesInABv();
		
		//points
		GeoPointND[] points = new GeoPointND[vertexCount];
		points[0] = getA();
		points[1] = getB();
		for (int i=2; i<vertexCount; i++){
			GeoPoint3D point = outputPoints.getElement(i-2);
			points[i] = point;
			point.setCoords(coords[i]);
			polyhedron.addPointCreated(point);
		}
		
		//faces
		IFace[] faces = solid.getFaces();
		for (int i=0; i<solid.getFaceCount(); i++){
			polyhedron.startNewFace();
			for (int j=0; j<faces[i].getVertexCount(); j++)
				polyhedron.addPointToCurrentFace(points[faces[i].getVertexIndices()[j]]);
			polyhedron.endCurrentFace();
		}
		
	}

	@Override
	protected void compute() {
		
		Coords o = getA().getInhomCoordsInD(3);
		
		Coords v1l = getB().getInhomCoordsInD(3).sub(o);
		if (v1l.equalsForKernel(0, Kernel.STANDARD_PRECISION)){
			getPolyhedron().setUndefined();
			return;
		}
		
		v1l.calcNorm();
		double l = v1l.getNorm();
		Coords v1 = v1l.mul(1/l);
		
		Coords v2 = getDirection().crossProduct(v1);
		if (v2.equalsForKernel(0, Kernel.STANDARD_PRECISION)){
			getPolyhedron().setUndefined();
			return;
		}
		
		v2.normalize();
		
		Coords v3 = v1.crossProduct(v2);
		
		
		matrix.setOrigin(o);
		matrix.setVx(v1l);
		matrix.setVy(v2.mul(l));
		matrix.setVz(v3.mul(l));
		
		for (int i=0; i<coords.length-2; i++){
			outputPoints.getElement(i).setCoords(matrix.mul(coords[i+2]),true);
		}
		

	}	
	
	
	
	
	
	
	
	
	
	/////////////////////////////////////////////
	// END OF THE CONSTRUCTION
	////////////////////////////////////////////

	
	
	

	
	
	
	
	public void update() {

		// compute and polyhedron
		super.update();
		
		//output points
		for (int i=0;i<outputPoints.size();i++)
			outputPoints.getElement(i).update();

	}
	
	




	public String getClassName() {
		return className;
	}
	
    
  
    
	
	
}
