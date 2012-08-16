package pl.tecna.gwt.connectors.client;

import pl.tecna.gwt.connectors.client.images.ConnectorsBundle;
import pl.tecna.gwt.connectors.client.listeners.change.DiagramChangeEvent;
import pl.tecna.gwt.connectors.client.listeners.change.EndPointConnectEvent;
import pl.tecna.gwt.connectors.client.util.Log;
import pl.tecna.gwt.connectors.client.util.Logger;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class EndPoint extends Point {

	@SuppressWarnings("unused")
	private final Logger LOG = new Logger("EndPoint");
	
	public ConnectionPoint gluedConnectionPoint;
	public Connector connector;
	boolean gluedToConnectionPoint;
	
	// Defines size of connection point
	public static final int CP_MARGIN = 13;

	/**
	 * {@link Connector}s are ended with EndPoints.
	 * You can drag and drop EndPoint to change its position.
	 * When EndPoint is dragging its Connector is redrawing.
	 * <p>
	 * {@link EndPoint} is visible until the {@link Connector} is 
	 * glued to {@link ConnectionPoint}. EndPoints are represented by 
	 * small circles.
	 */
	public EndPoint(Integer left, Integer top, Connector connector) {
		super(left, top);
		
		this.connector = connector;
		this.setGluedToConnectionPoint(false);
		this.setWidget(AbstractImagePrototype.create(ConnectorsBundle.INSTANCE.end_point()).createImage());
		this.getElement().getStyle().setZIndex(3);
	}

	/**
	 * 
	 * @param connectionPoint
	 */
	public void glueToConnectionPoint(ConnectionPoint connectionPoint){

		this.gluedConnectionPoint = connectionPoint;
		connectionPoint.glueToEndPoint(this);
		this.setGluedToConnectionPoint(true);
		this.clear();
		
		EndPointConnectEvent event = new EndPointConnectEvent(
				EndPoint.this, 
				connectionPoint);
		
		if (connector != null && connector.diagram != null) {
			connector.diagram.onDiagramChanged(DiagramChangeEvent.CONNECT, event);
		}
	}
	
	/**
	 * 
	 * @param connectionPoint
	 */
	public void unglueFromConnectionPoint(){
		this.gluedConnectionPoint.unglueFromEndPoint(this);
		this.gluedConnectionPoint = null;
		this.setGluedToConnectionPoint(false);
	}
	
	/**
	 * 
	 */
    public void update() {
		((AbsolutePanel)this.getParent()).setWidgetPosition(this, 
				this.getLeft() - ( this.getOffsetWidth() / 2 ), 
		        this.getTop() - ( this.getOffsetHeight() / 2 ));

    }
	
	/**
	 * @param panel 
	 * 
	 * 
	 */
	public void showOnDiagram(Diagram diagram) {
		// Add EndPoint to given panel
		diagram.boundaryPanel.add(this, this.getLeft() - CP_MARGIN / 2, 
		                this.getTop()  - CP_MARGIN / 2);
		
		// Set EndPoint's cursor
		DOM.setStyleAttribute(this.getElement(), 
                "cursor", "crosshair"); 

		// Make EndPoint draggable
		diagram.endPointDragController.makeDraggable(this);
		
	}
	
	public void updateOpositeEndPointOfVerticalSection() {
		// Updates position of connector after moving the EndPoint
		// Find section, then find opposite Corner Point. 
		// Update position of opposite Corner Point and update Section
		Section prevSection = this.connector.prevSectionForPoint(this);
		if (prevSection != null) {
			prevSection.getStartPoint().setLeft(this.getLeft());
			this.connector.update();
		}

		Section nextSection = this.connector.nextSectionForPoint(this);
		if (nextSection != null) {
			nextSection.getEndPoint().setLeft(this.getLeft());
			this.connector.update();
		}	

	}

	public void updateOpositeEndPointOfHorizontalSection() {
		// Updates position of connector after moving the EndPoint
		// Find section, then find opposite Corner Point. 
		// Update position of opposite Corner Point and update Section
		Section prevSection = this.connector.prevSectionForPoint(this);
		if (prevSection != null) {
			prevSection.getStartPoint().setTop(this.getTop());
			this.connector.update();
		}

		Section nextSection = this.connector.nextSectionForPoint(this);
		if (nextSection != null) {
			nextSection.getEndPoint().setTop(this.getTop());
			this.connector.update();
		}

//System.out.println("updateOpositeEndPointOfHorizontalSection() - ( " +
//			"(" + this.connector.sections.get(0).startPoint.getLeft() + ", " + this.connector.sections.get(0).startPoint.getTop() + "), " +
//			"(" + this.connector.sections.get(0).endPoint.getLeft() + ", " + this.connector.sections.get(0).endPoint.getTop() + "), " +
//			"(" + this.connector.sections.get(1).startPoint.getLeft() + ", " + this.connector.sections.get(1).startPoint.getTop() + "), " +
//			"(" + this.connector.sections.get(1).endPoint.getLeft() + ", " + this.connector.sections.get(1).endPoint.getTop() + "), " +
//			"(" + this.connector.sections.get(2).startPoint.getLeft() + ", " + this.connector.sections.get(2).startPoint.getTop() + "), " +
//			"(" + this.connector.sections.get(2).endPoint.getLeft() + ", " + this.connector.sections.get(2).endPoint.getTop() + "), " +
//			" )");
		
	}
	
	public boolean isGluedToConnectionPoint() {
		return gluedToConnectionPoint;
	}

	public void setGluedToConnectionPoint(boolean gluedToConnectionPoint) {
		this.gluedToConnectionPoint = gluedToConnectionPoint;
	}
	
}