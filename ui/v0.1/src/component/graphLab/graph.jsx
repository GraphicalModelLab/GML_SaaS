import * as React from 'react';
import * as ReactDOM from 'react-dom';
import * as styles from './../../css/structure.css';
import auth from "./../auth/auth";
import $ from 'jquery';
import Loading from './../loader/loading';
import Node from './graphComponent/node';
import Edge from './graphComponent/edge';
import EdgeDeletion from './graphComponent/edgeDeletion'

//http://www.petercollingridge.co.uk/tutorials/svg/interactive/pan-and-zoom/
// Here are some points to take a note:
//  1. Deleting nodes & Edge put "disable=true" in stead of removing element from an array
//     => React cannot re-render the components correctly.
export default class Graph extends React.Component<Props, {}> {

    constructor(props) {
        super(props);
        this.state = {
            nodes : this.props.items,
            currentChosenNode: null,
            edges: [],
            edgesDeletion: [],
            newEdge: false,
            prevLabel: null,
            transformMatrix: [1, 0, 0, 1, 0, 0],
            centerX : 250,
            centerY : 250,
            zoom    : 1.0,
            svg_width: window.innerWidth - 17,
            svg_height: window.innerHeight - 100
        };
        this.entryPointCallBack = this.entryPointCallBack.bind(this);
        this.moveCircleCallBack = this.moveCircleCallBack.bind(this);
        this.updateMatrix = this.updateMatrix.bind(this);
        this.pan = this.pan.bind(this);
        this.panRight = this.panRight.bind(this);
        this.panLeft = this.panLeft.bind(this);
        this.panTop = this.panTop.bind(this);
        this.panBottom = this.panBottom.bind(this);
        this.zoom = this.zoom.bind(this);
        this.zoomIn = this.zoomIn.bind(this);
        this.zoomOut = this.zoomOut.bind(this);
        this.addNode = this.addNode.bind(this);
        this.addEdge = this.addEdge.bind(this);
        this.getEdges = this.getEdges.bind(this);
        this.getNodes = this.getNodes.bind(this);
        this.updateDimensions = this.updateDimensions.bind(this);
        this.deleteEdgeCallBack = this.deleteEdgeCallBack.bind(this);
        this.deleteNodeCallBack = this.deleteNodeCallBack.bind(this);
        this.handleMouseDown = this.handleMouseDown.bind(this);
        this.handleMouseUp = this.handleMouseUp.bind(this);
        this.onScroll = this.onScroll.bind(this);
    }
    componentWillUnmount(){
        window.removeEventListener("resize", this.updateDimensions);
    }

    componentDidMount() {
        this.updateMatrix();
        window.addEventListener("resize", this.updateDimensions);

        ReactDOM.findDOMNode(this.refs.leftArrow).setAttribute('marker-start', "url(#left_ar)");

        ReactDOM.findDOMNode(this.refs.upperArrow).setAttribute('marker-end', "url(#upper_ar)");

        ReactDOM.findDOMNode(this.refs.rightArrow).setAttribute('marker-end', "url(#right_ar)");

        ReactDOM.findDOMNode(this.refs.bottomArrow).setAttribute('marker-end', "url(#bottom_ar)");

        this.zoomIn();
    }

    updateDimensions(){
        this.setState({
            svg_width: window.innerWidth - 17,
            svg_height: window.innerHeight - 100
        });
    }

    entryPointCallBack(label){
        if(this.state.newEdge == true && this.state.prevLabel != label){
        // Create new Edge
          var prevPosition = this.refs[this.state.prevLabel].getCurrentPosition();
          var currPosition = this.refs[label].getCurrentPosition();

          this.addEdge(label, this.state.prevLabel, currPosition.x, currPosition.y, prevPosition.x, prevPosition.y, false);

        }else if(this.state.prevLabel == label){
          this.state.prevLabel = null;
          this.state.newEdge = false;

          this.setState({
            currentChosenNode: null
          });
        }else{
        // Not Create new Edge
          this.state.prevLabel = label;
          this.state.newEdge = true;

          this.setState({
            currentChosenNode: label
          });
        }
    }

    moveCircleCallBack(label,x,y){
        for(var i=0;i<this.state.edges.length;i++){
             if(this.state.edges[i].label1 == label && !this.state.edges[i].disable){

                this.state.edges[i] = {
                    x1: x ,
                    y1: y ,
                    x2: this.state.edges[i].x2,
                    y2: this.state.edges[i].y2,
                    label1: label,
                    label2: this.state.edges[i].label2
                };

                this.refs["edge"+i].update1(x,y);
                this.refs["edgeDeletion"+i].update((x+this.state.edges[i].x2)/2,(y+this.state.edges[i].y2)/2);
             }else if(this.state.edges[i].label2 == label && !this.state.edges[i].disable){
                console.log("Label 2222");
                this.state.edges[i] = {
                                    x1: this.state.edges[i].x1,
                                    y1: this.state.edges[i].y1,
                                    x2: x,
                                    y2: y,
                                    label1: this.state.edges[i].label1,
                                    label2: label
                                };
                this.refs["edge"+i].update2(x,y);
                this.refs["edgeDeletion"+i].update((x+this.state.edges[i].x1)/2,(y+this.state.edges[i].y1)/2);
             }
        }
    }

    updateMatrix(){
        console.log("Update matrix!");
        if(ReactDOM.findDOMNode(this.refs.canvas)){
            console.log("Found canvas : "+ "matrix(" +  this.state.transformMatrix.join(' ') + ")");
            ReactDOM.findDOMNode(this.refs.canvas).setAttribute("transform", "matrix(" +  this.state.transformMatrix.join(' ') + ")");
        }
    }

    pan(dx,dy){
        this.state.transformMatrix[4] += dx;
        this.state.transformMatrix[5] += dy;

        console.log("pan !!!!");
        this.updateMatrix();
    }

    panRight(){
        this.pan(-25,0);
    }

    panLeft(){
        this.pan(25,0);
    }

    panTop(){
        this.pan(0,25);
    }

    panBottom(){
        this.pan(0,-25);
    }

    zoom(scale){
        for(var i=0;i<4;i++){
            this.state.transformMatrix[i] = this.state.transformMatrix[i]*scale;
        }

        this.state.transformMatrix[4] += parseInt((1 - scale) * this.state.centerX);
        this.state.transformMatrix[5] += parseInt((1 - scale) * this.state.centerY);

        this.updateMatrix();

        this.setState({
            zoom: this.state.zoom * scale
        })
    }

    zoomIn(){
        this.zoom(0.8);
    }

    zoomOut(){
        this.zoom(1.25);
    }

    getEdges(){
        var edges = [];
        for(let index in this.state.edges){
            if(!this.state.edges[index].disable){
                edges.push(this.state.edges[index]);
            }
        }

        return edges;
    }

    getNodes(){
        var nodes = [];
        for(let index in this.state.nodes){
            nodes.push({
                label: this.state.nodes[index].label,
                disable: this.state.nodes[index].disable,
                x: this.refs[this.state.nodes[index].label].state.x,
                y: this.refs[this.state.nodes[index].label].state.y,
                properties: this.refs[this.state.nodes[index].label].getProperties()
            });
        }

        return nodes;
    }

    addEdge(label1, label2, x1, y1, x2, y2, disable){
          var newEdge = {
              x1: x1,
              y1: y1,
              x2: x2,
              y2: y2,
              label1: label1,
              label2: label2,
              disable: disable
          };

          var newEdgeDeletion = {
              x: (x1 + x2)/2,
              y: (y1 + y2)/2,
              label1: label1,
              label2: label2,
              disable: disable
          };

          this.state.edges.push(newEdge);
          this.state.edgesDeletion.push(newEdgeDeletion);


          this.setState({
            nodes: this.state.nodes,
            edges: this.state.edges,
            edgesDeletion: this.state.edgesDeletion,
            newEdge: false,
            prevLabel: null,
            currentChosenNode: null
          });
    }

    addNode(label,x,y,disable,properties){
       var nodes = this.state.nodes;

       console.log("add label : "+label)
       console.log(properties);
       nodes.push({
            label: label,
            x: x,
            y: y,
            disable: disable,
            properties: properties
       });

       this.setState({
            nodes: nodes
       });
    }

    deleteEdgeCallBack(label1, label2){
        var deletedIndex = this.state.edges.findIndex(edge => edge.label1 == label1 && edge.label2 == label2);
        this.state.edges[deletedIndex].disable = true;

        var deletedEdgeMarkIndex = this.state.edgesDeletion.findIndex(mark => mark.label1 == label1 && mark.label2 == label2);
        this.state.edgesDeletion[deletedEdgeMarkIndex].disable = true;

        this.setState({
            edges: this.state.edges
        });
    }

    deleteNodeCallBack(label){
        for(let index in this.state.edges){
            if(this.state.edges[index].label1 == label || this.state.edges[index].label2 == label){
                this.state.edges[index].disable = true;
            }
        }
        for(let index in this.state.edgesDeletion){
            if(this.state.edgesDeletion[index].label1 == label || this.state.edgesDeletion[index].label2 == label){
                this.state.edgesDeletion[index].disable = true;
            }
        }

        var deletedNodeIndex = this.state.nodes.findIndex(node => node.label == label);
        this.state.nodes[deletedNodeIndex].disable = true;
        this.refs[label].setDisable(true);

        this.setState({
            edges: this.state.edges,
            edgesDeletion: this.state.edgesDeletion,
            nodes: this.state.nodes,
            currentChosenNode: null,
            prevLabel: null,
            newEdge: false
        });
    }

    handleMouseDown(e){
      e.stopPropagation();

      this.coords = {
            x: e.pageX,
            y: e.pageY
      }
    }

    handleMouseUp(e) {
      e.stopPropagation();

      // Move Coordinate
      const xDiff = e.pageX - this.coords.x;
      const yDiff = e.pageY - this.coords.y;

      if(xDiff > 0) this.pan(xDiff,0);
      else          this.pan(xDiff, 0);

      if(yDiff > 0) this.pan(0,yDiff);
      else          this.pan(0,yDiff);
    }

    onScroll(e){
      e.stopPropagation();
        console.log(e);
    }

    clearSvgPane(){
         this.setState({
            nodes : [],
            currentChosenNode: null,
            edges: [],
            edgesDeletion: [],
            prevLabel: null
         });
    }

    render() {
        return (
                <svg className={styles.svgPane}　id="svg-graph" width={this.state.svg_width} height={this.state.svg_height} xmlns="http://www.w3.org/2000/svg" version="1.1"
                            onMouseDown={this.handleMouseDown}
                            onMouseUp={this.handleMouseUp}
                            onScroll={this.onScroll}
                >
                    <g>
                        <defs>
                          <marker id="left_ar" orient="180"
                                                                           markerWidth='2' markerHeight='4'
                                                                           refX='0.1' refY='2'>
                                                                           <path ref="left_ar_symbol" d='M0,0 V4 L2,2 Z' />
                          </marker>
                          <marker id="upper_ar" orient="270"
                                                                                                       markerWidth='2' markerHeight='4'
                                                                                                       refX='0.1' refY='2'>
                                                                                                       <path ref="upper_ar_symbol" d='M0,0 V4 L2,2 Z' />
                          </marker>
                          <marker id="right_ar" orient="0"
                                                                                                       markerWidth='2' markerHeight='4'
                                                                                                       refX='0.1' refY='2'>
                                                                                                       <path ref="right_ar_symbol" d='M0,0 V4 L2,2 Z' />
                          </marker>
                          <marker id="bottom_ar" orient="90"
                                                                                                       markerWidth='2' markerHeight='4'
                                                                                                       refX='0.1' refY='2' >
                                                                                                       <path ref="bottom_ar_symbol" d='M0,0 V4 L2,2 Z' />
                          </marker>
                        </defs>

                        <g onClick={this.panLeft}>
                            <circle cx="16.5" cy={this.state.svg_height/2} r="10" fill="#eaeaea"/>
                            <line ref="leftArrow" strokeWidth="4px" stroke="#000088" x1={13} y1={this.state.svg_height/2} x2={20} y2={this.state.svg_height/2} />
                        </g>

                        <g onClick={this.panTop}>
                            <circle cx={this.state.svg_width/2} cy={16.5} r="10" fill="#eaeaea"/>
                            <line ref="upperArrow" strokeWidth="4px" stroke="#000088"  x1={this.state.svg_width/2} y1={20} x2={this.state.svg_width/2} y2={13}/>
                        </g>

                        <g onClick={this.panRight}>
                            <circle cx={this.state.svg_width - 16.5} cy={this.state.svg_height/2} r="10" fill="#eaeaea"/>
                            <line ref="rightArrow" strokeWidth="4px" stroke="#000088" x1={this.state.svg_width - 20} y1={this.state.svg_height/2} x2={this.state.svg_width - 13} y2={this.state.svg_height/2} />
                        </g>

                        <g onClick={this.panBottom}>
                            <circle cx={this.state.svg_width/2} cy={this.state.svg_height - 16.5} r="10" fill="#eaeaea"/>
                            <line ref="bottomArrow" strokeWidth="4px" stroke="#000088" x1={this.state.svg_width/2} y1={this.state.svg_height - 20} x2={this.state.svg_width/2} y2={this.state.svg_height - 13} />
                        </g>

                        <g onClick={this.zoomIn} >
                            <circle cx="25" cy="20.5" r="8" fill="white"/>
                            <line stroke="#000088" x1={21.5} y1={20.5} x2={28.5} y2={20.5} />
                        </g>
                        <g onClick={this.zoomOut} >
                            <circle cx="25" cy="39.5" r="8" fill="white"/>
                            <line stroke="#000088" x1={21.5} y1={39.5} x2={28.5} y2={39.5} />
                            <line stroke="#000088" x1={25} y1={35.5} x2={25} y2={43.5} />
                        </g>
                    </g>

                    <g ref="canvas">
                        { this.state.nodes.map((d, idx) => {
                            return <Node key={d.label} zoom={this.state.zoom} ref={d.label} disable={d.disable} label={d.label} x={d.x} y={d.y} properties={d.properties} entryPointCallBack={this.entryPointCallBack} moveCircleCallBack={this.moveCircleCallBack} currentChosenNode={this.state.currentChosenNode} deleteNodeCallBack={this.deleteNodeCallBack} />
                        }) }

                        { this.state.edges.map((d, idx) => {
                            if(!d.disable){
                                return <Edge key={"edge"+idx} ref={"edge"+idx} x1={d.x1} y1={d.y1} x2={d.x2} y2={d.y2} label1={d.label1} label2={d.label2}/>
                            }
                        })}

                        { this.state.edgesDeletion.map((d, idx) => {
                            if(!d.disable){
                                return <EdgeDeletion key={"edgeDeletion"+idx} ref={"edgeDeletion"+idx} x={d.x} y={d.y} label1={d.label1} label2={d.label2} deleteEdgeCallBack={this.deleteEdgeCallBack}/>
                            }
                        })}
                    </g>
                </svg>
           )
    }
}