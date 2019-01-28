/**
 * Copyright (C) 2018 Mao Ito
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from 'react';
import * as ReactDOM from 'react-dom';
import * as styles from './../../css/structure.css';
import auth from "./../auth/auth";
import $ from 'jquery';
import Loading from './../loader/loading';
import Node from './graphComponent/node';
import Edge from './graphComponent/edge';
import EdgeDeletion from './graphComponent/edgeDeletion';
import ReactTooltip from 'react-tooltip';

/**
 * Notice:
 *  (1) use "disable=true" to delete nodes & edges in stead of actually deleting them
 *      => React cannot re-render the components correctly if we actually delete them from array of state, e.g. edges and nodes.
 *
 **/
export default class Graph extends React.Component<Props, {}> {

  constructor(props) {
    super(props);
    this.state = {
      nodes: this.props.items,
      currentChosenNode: null,
      edges: [],
      edgesDeletion: [],
      newEdge: false,
      prevLabel: null,
      transformMatrix: [1, 0, 0, 1, 0, 0],
      centerX: 250,
      centerY: 250,
      zoom: 1.0,
      svg_width: window.innerWidth - 17,
      svg_height: window.innerHeight - 150,
      groupBy: false,
      groupByMove: false,
      groupByXBrowser: 0,
      groupByYBrowser: 0,
      groupByX: 0,
      groupByY: 0,
      groupByWidth:0,
      groupByHeight:0,
      groupByHeaderMargin: 133,
      groupByXMargin: 5,
      isDirectedMode: true
    };
    this.entryPointCallBack = this.entryPointCallBack.bind(this);
    this.moveCircleCallBack = this.moveCircleCallBack.bind(this);
    this.updateMatrix = this.updateMatrix.bind(this);
    this.updateMatrix = this.updateMatrix.bind(this);
    this.pan = this.pan.bind(this);
    this.panRight = this.panRight.bind(this);
    this.panLeft = this.panLeft.bind(this);
    this.panTop = this.panTop.bind(this);
    this.panBottom = this.panBottom.bind(this);
    this.zoom = this.zoom.bind(this);
    this.zoomIn = this.zoomIn.bind(this);
    this.zoomOut = this.zoomOut.bind(this);
    this.groupBy = this.groupBy.bind(this);
    this.addNode = this.addNode.bind(this);
    this.addEdge = this.addEdge.bind(this);
    this.getEdges = this.getEdges.bind(this);
    this.getNodes = this.getNodes.bind(this);
    this.updateDimensions = this.updateDimensions.bind(this);
    this.deleteEdgeCallBack = this.deleteEdgeCallBack.bind(this);
    this.deleteNodeCallBack = this.deleteNodeCallBack.bind(this);
    this.handleMouseDown = this.handleMouseDown.bind(this);
    this.handleMouseUp = this.handleMouseUp.bind(this);
    this.handleMouseMove = this.handleMouseMove.bind(this);
    this.onScroll = this.onScroll.bind(this);
    this.visibleEdge = this.visibleEdge.bind(this);
    this.getCanvasCoordinate = this.getCanvasCoordinate.bind(this);
    this.changeToDirectedMode = this.changeToDirectedMode.bind(this);
    this.changeToUndirectedMode = this.changeToUndirectedMode.bind(this);
  }

  componentWillUnmount() {
    window.removeEventListener("resize", this.updateDimensions);
  }

  componentDidMount() {
    this.updateMatrix();
    window.addEventListener("resize", this.updateDimensions);

    this.zoomIn();
  }

  updateDimensions() {
    this.setState({
      svg_width: window.innerWidth - 17,
      svg_height: window.innerHeight - 150
    });
  }

  entryPointCallBack(label) {

    var loopEdgeIndex = this.state.edges.findIndex(edge => edge.label1 == label && edge.label2 == label);
    var loopEdgeDisable = false;
    if(loopEdgeIndex > 0) loopEdgeDisable = this.state.edges[loopEdgeIndex].disable;

    if (this.state.newEdge == true
    && (this.state.prevLabel == label && ((loopEdgeIndex > 0 && loopEdgeDisable) || loopEdgeIndex < 0) || this.state.prevLabel != label )) {
      // Create new Edge
      var prevPosition = this.refs[this.state.prevLabel].getCurrentPosition();
      var currPosition = this.refs[label].getCurrentPosition();

      this.addEdge(label, this.state.prevLabel, currPosition.x, currPosition.y, prevPosition.x, prevPosition.y, false, this.state.isDirectedMode);

    } else if (this.state.prevLabel == label) {
      this.state.prevLabel = null;
      this.state.newEdge = false;

      this.setState({
        currentChosenNode: null
      });
    } else {
      // Not Create new Edge
      this.state.prevLabel = label;
      this.state.newEdge = true;

      this.setState({
        currentChosenNode: label
      });
    }
  }

  moveCircleCallBack(label, x, y) {
    for (var i = 0; i < this.state.edges.length; i++) {
      if (this.state.edges[i].label1 == label && !this.state.edges[i].disable) {

        this.state.edges[i] = {
          x1: x,
          y1: y,
          x2: this.state.edges[i].x2,
          y2: this.state.edges[i].y2,
          label1: label,
          label2: this.state.edges[i].label2,
          disable: this.state.edges[i].disable,
          isDirected: this.state.edges[i].isDirected
        };

        this.refs["edge" + i].update1(x, y);
        if(this.state.edges[i].label1 != this.state.edges[i].label2){
            this.refs["edgeDeletion" + i].update((x + this.state.edges[i].x2) / 2, (y + this.state.edges[i].y2) / 2);
        }else{
            this.refs["edgeDeletion" + i].update(this.state.edges[i].x1, this.state.edges[i].y1-50);
        }
      } else if (this.state.edges[i].label2 == label && !this.state.edges[i].disable) {
        this.state.edges[i] = {
          x1: this.state.edges[i].x1,
          y1: this.state.edges[i].y1,
          x2: x,
          y2: y,
          label1: this.state.edges[i].label1,
          label2: label,
          disable: this.state.edges[i].disable,
          isDirected: this.state.edges[i].isDirected
        };
        this.refs["edge" + i].update2(x, y);
        if(this.state.edges[i].label1 != this.state.edges[i].label2){
            this.refs["edgeDeletion" + i].update((x + this.state.edges[i].x1) / 2, (y + this.state.edges[i].y1) / 2);
        }else{
            this.refs["edgeDeletion" + i].update(this.state.edges[i].x1, this.state.edges[i].y1-50);
        }
      }
    }
  }

  updateMatrix() {
    if (ReactDOM.findDOMNode(this.refs.canvas)) {
      ReactDOM.findDOMNode(this.refs.canvas).setAttribute("transform", "matrix(" + this.state.transformMatrix.join(' ') + ")");
    }
  }

  pan(dx, dy) {
    this.state.transformMatrix[4] += dx;
    this.state.transformMatrix[5] += dy;

    this.updateMatrix();
  }

  panRight() {
    this.pan(-25, 0);
  }

  panLeft() {
    this.pan(25, 0);
  }

  panTop() {
    this.pan(0, 25);
  }

  panBottom() {
    this.pan(0, -25);
  }

  zoom(scale) {
    for (var i = 0; i < 4; i++) {
      this.state.transformMatrix[i] = this.state.transformMatrix[i] * scale;
    }

    this.state.transformMatrix[4] += parseInt((1 - scale) * this.state.centerX);
    this.state.transformMatrix[5] += parseInt((1 - scale) * this.state.centerY);

    this.updateMatrix();

    this.setState({
      zoom: this.state.zoom * scale
    });
  }

  groupBy(){
    this.setState({
      groupBy: !this.state.groupBy
    });
  }

  zoomIn() {
    this.zoom(0.8);
  }

  zoomOut() {
    this.zoom(1.25);
  }

  getEdges() {
    var edges = [];
    for (let index in this.state.edges) {
      console.log("edge "+index);
      console.log(this.state.edges[index]);
      if (!this.state.edges[index].disable) {
        edges.push(this.state.edges[index]);
      }
    }

    return edges;
  }

  getNodes() {
    var nodes = [];
    for (let index in this.state.nodes) {
      nodes.push({
        label: this.state.nodes[index].label,
        disable: this.refs[this.state.nodes[index].label].state.disable,
        x: this.refs[this.state.nodes[index].label].state.x,
        y: this.refs[this.state.nodes[index].label].state.y,
        properties: this.refs[this.state.nodes[index].label].getProperties(),
        shape: this.refs[this.state.nodes[index].label].state.shape
      });
    }

    return nodes;
  }

  addEdge(label1, label2, x1, y1, x2, y2, disable, isDirected) {
    var existingEdgeIndex = this.state.edges.findIndex(edge => edge.label1 == label1 && edge.label2 == label2);

    if (existingEdgeIndex < 0) {
      var newEdge = {
                x1: x1,
                y1: y1,
                x2: x2,
                y2: y2,
                label1: label1,
                label2: label2,
                disable: disable,
                isDirected: isDirected
      };

      this.state.edges.push(newEdge);

      if(label1 != label2){
          var newEdgeDeletion = {
            x: (x1 + x2) / 2,
            y: (y1 + y2) / 2,
            label1: label1,
            label2: label2,
            disable: disable
          };

          this.state.edgesDeletion.push(newEdgeDeletion);

      }else{
        // Self Edge
        var newEdgeDeletion = {
            x: x1,
            y: y1 - 50,
            label1: label1,
            label2: label2,
            disable: disable
        };

        this.state.edgesDeletion.push(newEdgeDeletion);
      }

      this.setState({
        nodes: this.state.nodes,
        edges: this.state.edges,
        edgesDeletion: this.state.edgesDeletion,
        newEdge: false,
        prevLabel: null,
        currentChosenNode: null
      });
    } else {
      this.visibleEdge(label1, label2, x1, y1, x2, y2, false, isDirected);
    }
  }

  addNode(label, x, y, disable, properties,shape) {
    var nodes = this.state.nodes;

    nodes.push({
      label: label,
      x: x,
      y: y,
      disable: disable,
      properties: properties,
      shape: shape
    });

    this.setState({
      nodes: nodes
    });
  }

  visibleEdge(label1, label2, x1, y1, x2, y2, disable,isDirected) {
    var disableIndex = this.state.edges.findIndex(edge => edge.label1 == label1 && edge.label2 == label2);
    this.state.edges[disableIndex].disable = disable;
    this.state.edges[disableIndex].isDirected = isDirected;
    if (!disable) {
      this.state.edges[disableIndex].x1 = x1;
      this.state.edges[disableIndex].x2 = x2;
      this.state.edges[disableIndex].y1 = y1;
      this.state.edges[disableIndex].y2 = y2;
    }

    var disableEdgeMarkIndex = this.state.edgesDeletion.findIndex(mark => mark.label1 == label1 && mark.label2 == label2);
    this.state.edgesDeletion[disableEdgeMarkIndex].disable = disable;
    if (!disable) {
      if(label1 != label2){
          this.state.edgesDeletion[disableEdgeMarkIndex].x = (x1 + x2) / 2;
          this.state.edgesDeletion[disableEdgeMarkIndex].y = (y1 + y2) / 2;
      }else{
          this.state.edgesDeletion[disableEdgeMarkIndex].x = x1;
          this.state.edgesDeletion[disableEdgeMarkIndex].y = y1 - 50;
      }
    }

    this.setState({
      edges: this.state.edges
    });

  }
  deleteEdgeCallBack(label1, label2) {
    this.visibleEdge(label1, label2, 0, 0, 0, 0, true, this.state.isDirectedMode);
  }

  deleteNodeCallBack(label) {
    for (let index in this.state.edges) {
      if (this.state.edges[index].label1 == label || this.state.edges[index].label2 == label) {
        this.state.edges[index].disable = true;
      }
    }
    for (let index in this.state.edgesDeletion) {
      if (this.state.edgesDeletion[index].label1 == label || this.state.edgesDeletion[index].label2 == label) {
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

  handleMouseDown(e) {

    e.stopPropagation();

    this.coords = {
      x: e.pageX,
      y: e.pageY
    }

    if(this.state.groupByMove){
        this.setState({
          groupByMove: false,
          groupByX:0,
          groupByY:0,
          groupByWidth:0,
          groupByHeight:0
        });
        document.removeEventListener('mousemove', this.handleMouseMove);

        for (let index in this.state.nodes) {
            if(
                this.refs[this.state.nodes[index].label].state.selected
            ){
                this.refs[this.state.nodes[index].label].deselect();
            }
        }
    }
    if(this.state.groupBy){
        var canvasPoint = this.getCanvasCoordinate(e.pageX - this.state.groupByXMargin,e.pageY - this.state.groupByHeaderMargin);

        this.setState({
            groupByX: canvasPoint[0],
            groupByY: canvasPoint[1],
            groupByXBrowser: e.pageX,
            groupByYBrowser: e.pageY
        });

        document.addEventListener('mousemove', this.handleMouseMove);

    }
  }

  /**
   * Given a point on browser coordinate (i.e. OnClick x & y data),
   *      Calculate the corresponding point (x,y) on Canvas Coordinate
   */
  getCanvasCoordinate(x, y){
        var matrix = document.createElementNS("http://www.w3.org/2000/svg", "svg").createSVGMatrix();
        matrix.a = this.state.transformMatrix[0];
        matrix.b = this.state.transformMatrix[1];
        matrix.c = this.state.transformMatrix[2];
        matrix.d = this.state.transformMatrix[3];
        matrix.e = this.state.transformMatrix[4];
        matrix.f = this.state.transformMatrix[5];

        var inverseMatrix = matrix.inverse();

        return [
            x * inverseMatrix.a + y * inverseMatrix.c + inverseMatrix.e, // X coordinate
            x * inverseMatrix.b + y * inverseMatrix.d + inverseMatrix.f  // Y coordinate
        ]
  }

  handleMouseMove(e){

    if(this.state.groupBy
    && e.pageX - this.state.groupByXBrowser > 0
    && e.pageY - this.state.groupByYBrowser > 0
    ){
        this.setState({
            groupByWidth: (e.pageX - this.state.groupByXBrowser)*(1 / this.state.zoom),
            groupByHeight: (e.pageY - this.state.groupByYBrowser)*(1 / this.state.zoom)
        });

        // Select nodes if they are in the
        for (let index in this.state.nodes) {
            if(
                this.state.groupByX < this.refs[this.state.nodes[index].label].state.x && this.refs[this.state.nodes[index].label].state.x < this.state.groupByX + this.state.groupByWidth &&
                this.state.groupByY < this.refs[this.state.nodes[index].label].state.y && this.refs[this.state.nodes[index].label].state.y < (this.state.groupByY+ this.state.groupByHeight)
            ){
                this.refs[this.state.nodes[index].label].select();
            }else{
                this.refs[this.state.nodes[index].label].deselect();
            }
        }
    }

    if(this.state.groupByMove){
        const xDiff = this.coordsPrev.x - e.pageX;
        const yDiff = this.coordsPrev.y - e.pageY;

        for (let index in this.state.nodes) {
            if(
             this.refs[this.state.nodes[index].label].state.selected
            ){
                this.refs[this.state.nodes[index].label].move(xDiff,yDiff);
            }
        }

        this.setState({
            groupByX: this.state.groupByX - xDiff*(1 / this.state.zoom),
            groupByY: this.state.groupByY - yDiff*(1 / this.state.zoom)
        })

        this.coordsPrev.x = e.pageX;
        this.coordsPrev.y = e.pageY;
    }
  }

  handleMouseUp(e) {
    console.log("mouse up"+this.state.groupBy);
    e.stopPropagation();

    if(!this.state.groupBy && !this.groupByMove){
        // Move Coordinate
        const xDiff = e.pageX - this.coords.x;
        const yDiff = e.pageY - this.coords.y;

        if (xDiff > 0) this.pan(xDiff, 0);
        else this.pan(xDiff, 0);

        if (yDiff > 0) this.pan(0, yDiff);
        else this.pan(0, yDiff);
    }else if(this.state.groupBy){

        this.setState({
          groupBy: false,
          groupByMove: true
        });

        this.coordsPrev = {
            x: e.pageX,
            y: e.pageY
        }
    }
  }

  changeToDirectedMode(){
    this.setState({
      isDirectedMode: true
    });
  }

  changeToUndirectedMode(){
    this.setState({
      isDirectedMode: false
    });
  }

  onScroll(e) {
    e.stopPropagation();
  }

  clearSvgPane() {
    this.setState({
      nodes: [],
      currentChosenNode: null,
      edges: [],
      edgesDeletion: [],
      prevLabel: null
    });
  }

  render() {
    return (
      <svg className={ !this.state.groupBy ? styles.svgPane : styles.svgPaneWithCrossChair } id="svg-graph" width={ this.state.svg_width } height={ this.state.svg_height } xmlns="http://www.w3.org/2000/svg" version="1.1" onMouseDown={ this.handleMouseDown }
        onMouseUp={ this.handleMouseUp } onScroll={ this.onScroll }>
        <g>
          <g onClick={ this.zoomIn } data-tip="Zoom In">
            <circle cx="25" cy="20.5" r="8" fill="white" />
            <line stroke="#000088" x1={ 21.5 } y1={ 20.5 } x2={ 28.5 } y2={ 20.5 } />
          </g>
          <g onClick={ this.zoomOut } data-tip="Zoom Out">
            <circle cx="25" cy="39.5" r="8" fill="white" />
            <line stroke="#000088" x1={ 21.5 } y1={ 39.5 } x2={ 28.5 } y2={ 39.5 } />
            <line stroke="#000088" x1={ 25 } y1={ 35.5 } x2={ 25 } y2={ 43.5 } />
          </g>
          <g onClick={ this.groupBy } data-tip="GroupBy Mode: Drag & Release & Move a group of nodes by Cursor ">
            <circle cx="55" cy="20.5" r="8" fill="white" />
            <circle cx="55" cy="20.5" r="3" fill="blue" />
          </g>
          <g onClick={ this.changeToDirectedMode } data-tip="Change to Directed Design Mode">
            <circle cx="85" cy="20.5" r="8" fill="white" />
            <circle cx="85" cy="20.5" r="3" fill="yellow" />
          </g>
          <g onClick={ this.changeToUndirectedMode } data-tip="Change to Undirected Design Mode">
            <circle cx="115" cy="20.5" r="8" fill="white" />
            <circle cx="115" cy="20.5" r="3" fill="green" />
          </g>
        </g>
        <g ref="canvas">
          { this.state.nodes.map((d, idx) => {
              return <Node key={ d.label } modelparameter={ this.props.modelparameter } zoom={ this.state.zoom } ref={ d.label } disable={ d.disable } label={ d.label }
                       x={ d.x } y={ d.y } properties={ d.properties } shape={d.shape} entryPointCallBack={ this.entryPointCallBack } moveCircleCallBack={ this.moveCircleCallBack } currentChosenNode={ this.state.currentChosenNode }
                       deleteNodeCallBack={ this.deleteNodeCallBack } />
            }) }
          { this.state.edges.map((d, idx) => {
              if (!d.disable) {
                return <Edge key={ "edge" + idx } ref={ "edge" + idx } x1={ d.x1 } y1={ d.y1 } x2={ d.x2 } y2={ d.y2 } label1={ d.label1 }
                         label2={ d.label2 } isDirected={ d.isDirected} />
              }
            }) }
          { this.state.edgesDeletion.map((d, idx) => {
              if (!d.disable) {
                return <EdgeDeletion key={ "edgeDeletion" + idx } ref={ "edgeDeletion" + idx } x={ d.x } y={ d.y } label1={ d.label1 } label2={ d.label2 }
                         deleteEdgeCallBack={ this.deleteEdgeCallBack } />
              }
            }) }
            <g ref="groupByRectangle">
                { this.state.groupBy || this.state.groupByMove ? (
                 <rect className={ styles.groupByRectangle } width={ this.state.groupByWidth } height={ this.state.groupByHeight } x={ this.state.groupByX  } y={ this.state.groupByY } fill="blue"></rect>
                 )
                 :(<g></g>)
                 }
            </g>
        </g>
        <ReactTooltip />
      </svg>
    )
  }
}
